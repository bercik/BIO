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
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.LabelField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;

/**
 *
 * @author robert
 */
public class RemoveJmpsToNextLine implements IOptimizer
{
    private final IntermediateCode ic;
    private final OptimizationStatistics statistics;
    private final Logger logger = Logger.getGlobal();
    private boolean optimize = false;

    public RemoveJmpsToNextLine(IntermediateCode ic, OptimizationStatistics statistics)
    {
        logger.info("RemoveJmpsToNextLine");
        logger.fine(ic.toStringWithLinesNumber());
        
        this.ic = ic;
        this.statistics = statistics;
        
        analyseAndRemove();
    }

    @Override
    public boolean isOptimized()
    {
        return optimize;
    }
    
    private void analyseAndRemove()
    {
        for (int lnr = ic.numberOfLines() - 1; lnr >= 0; --lnr)
        {
            analyseAndRemove(lnr);
        }
    }
    
    private void analyseAndRemove(int lnr)
    {
        Line line = ic.getLine(lnr);
        if (line.numberOfFields() > 0)
        {
            StringField sf = (StringField)line.getField(0);
            String funName = sf.getStr();

            // jeżeli to skok warunkowy lub bezwarunkowy
            if (funName.equals(InterpreterFunction.JMP.toString())
                    || funName.equals(InterpreterFunction.JMP_IF_FALSE.toString()))
            {
                LabelField lf = (LabelField)line.getField(1);
                Label l = lf.getLabel();
                int jmpDest = l.getLine();
                
                // jeżeli skok do następnej lini
                if (jmpDest == lnr + 1)
                {
                    optimize = true;
                    statistics.addJumpsToNextLineRemoved();
                    // jeżeli JMP to po prostu usuń
                    if (funName.equals(InterpreterFunction.JMP.toString()))
                    {
                        ic.removeLine(lnr);
                    }
                    // jeżeli JMP_IF_FALSE to usuń i zamień powyższy POP na POPC
                    else if (funName.equals(InterpreterFunction.JMP_IF_FALSE.toString()))
                    {
                        // wiadomość błędu
                        String errorMsg = "Line above JMP_IF_FALSE should be POP";
                        // pobierz linię wyżej
                        Line lineAbove = ic.getLine(lnr - 1);
                        // sprawdź czy ma jakieś pola
                        if (lineAbove.numberOfFields() == 0)
                        {
                            throw new RuntimeException(errorMsg);
                        }
                        // sprawdź czy to na pewno POP
                        sf = (StringField)lineAbove.getField(0);
                        if (!sf.getStr().equals(InterpreterFunction.POP.toString()))
                        {
                            throw new RuntimeException(errorMsg);
                        }
                        // zamień na POPC
                        sf.setStr(InterpreterFunction.POPC.toString());
                        
                        ic.removeLine(lnr);
                    }
                    else
                    {
                        throw new RuntimeException("Something go really bad");
                    }
                }
            }
        }
    }
}
