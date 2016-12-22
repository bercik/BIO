/*
 * Copyright (C) 2016 robert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.rcebula.code_generation.optimization;

import java.util.logging.Logger;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code.ValueType;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.InterpreterFunctionsGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.LabelField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class RemovePushBoolJmpSequences implements IOptimizer
{
    private final IntermediateCode ic;
    private final OptimizationStatistics statistics;
    private final MyFiles files;
    
    private boolean optimize = false;

    public RemovePushBoolJmpSequences(IntermediateCode ic, OptimizationStatistics statistics, MyFiles files)
    {
//        Logger logger = Logger.getGlobal();
//        logger.info("RemovePushBoolJmpSequences");
//        logger.fine(ic.toStringWithLinesNumber());
        
        this.ic = ic;
        this.statistics = statistics;
        this.files = files;
        
        analyseAndRemove();
    }

    @Override
    public boolean isOptimized()
    {
        return optimize;
    }
    
    private void analyseAndRemove()
    {
        int c = ic.numberOfLines() - 1;
        
        while (c >= 0)
        {
            Line line = ic.getLine(c);
            
            // komenda jmp_if_false zawsze zawiera cel skoku, czyli co najmniej dwa pola
            if (line.numberOfFields() > 1)
            {
                String funName = ((StringField)line.getField(0)).getStr();
                // jeżeli jmp_if_false
                if (funName.equals(InterpreterFunction.JMP_IF_FALSE.toString()))
                {
                    // sprawdzamy czy trzy linie wyżej (jedną wyżej jest jmp_if_not_bool, a dwie wyżej jest pop)
                    // jest komenda push
                    Line l = ic.getLine(c - 3);
                    if (l.numberOfFields() > 1)
                    {
                        funName = ((StringField)l.getField(0)).getStr();
                        // jeżeli PUSH
                        if (funName.equals(InterpreterFunction.PUSH.toString()))
                        {
                            String valField = ((StringField)l.getField(1)).getStr();
                            String[] splitted = valField.split(":");
                            
                            // jeżeli po rozłączeniu mamy dwa stringi
                            if (splitted.length == 2)
                            {
                                // jeżeli wartość jest typu bool
                                if (splitted[0].equals(ValueType.BOOL.toString()))
                                {
                                    boolean val = Boolean.parseBoolean(splitted[1]);
                                    
                                    // jeżeli false to dodaj za linią c linię jmp, label, jmpLine, jmpChNum
                                    if (!val)
                                    {
                                        InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();
                                        Label label = ((LabelField)line.getField(1)).getLabel();
                                        Integer jmpLine = Integer.parseInt(((StringField)line.getField(2)).getStr());
                                        Integer jmpChNum = Integer.parseInt(((StringField)line.getField(3)).getStr());
                                        Integer jmpFnum = Integer.parseInt(((StringField)line.getField(4)).getStr());
                                        
                                        ErrorInfo jmpErrorInfo = new ErrorInfo(jmpLine, jmpChNum, 
                                                files.getFromNum(jmpFnum));
                                        
                                        line = ifg.generateJmp(label, jmpErrorInfo);
                                        ic.insertLine(line, c+1);
                                        statistics.removePushBoolSequenceRemoved();
                                        optimize = true;
                                    }
                                    // usuń linijki c-3, c-2,c-1,c
                                    int i = 4;
                                    while (i-- > 0)
                                    {
                                        ic.removeLine(c - 3);
                                        statistics.addPushBoolSequenceRemoved();
                                        optimize = true;
                                    }
                                    
                                    c = c - 3;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            
            --c;
        }
    }
}
