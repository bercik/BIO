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

import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code_generation.intermediate.IntermediateCode;
import pl.rcebula.code_generation.intermediate.Label;
import pl.rcebula.code_generation.intermediate.LabelField;
import pl.rcebula.code_generation.intermediate.Line;
import pl.rcebula.code_generation.intermediate.StringField;
import pl.rcebula.utils.Statistics;

/**
 *
 * @author robert
 */
public class RemoveRedundantJumps
{
    private final IntermediateCode ic;
    private final Statistics statistics;

    public RemoveRedundantJumps(IntermediateCode ic, Statistics statistics)
            throws CodeOptimizationError
    {
        this.ic = ic;
        this.statistics = statistics;
        
        analyseAndRemove();
    }
    
    private void analyseAndRemove()
            throws CodeOptimizationError
    {
        int line = 0;
        while (line < ic.numberOfLines())
        {
            // usuwaj niepotrzebne skoki z danej linjki tak długo jak się da, jeżeli nie usunięto
            // przejdź do następnej linii
            if (!removeRedundantJump(line))
            {
                ++line;
            }
            else
            {
                statistics.addRedundantJumpRemoved();
            }
        }
    }
    
    private boolean removeRedundantJump(int lineNumber)
            throws CodeOptimizationError
    {
        Line line = ic.getLine(lineNumber);
        
        if (line.numberOfFields() > 0)
        {
            StringField sf = (StringField)line.getField(0);
            String funName = sf.getStr();
            
            // jeżeli to skok warunkowy lub bezwarunkowy
            if (funName.equals(InterpreterFunction.JMP.toString()) ||
                    funName.equals(InterpreterFunction.JMP_IF_FALSE.toString()))
            {
                LabelField lfOrig = (LabelField)line.getField(1);
                Label labelOrig = lfOrig.getLabel();
                int jmpDest = labelOrig.getLine();
                
                // jeżeli skaczemy do samego siebie to błąd
                if (jmpDest == lineNumber)
                {
                    int errorLine = Integer.parseInt(((StringField)line.getField(2)).getStr());
                    int errorChNum = Integer.parseInt(((StringField)line.getField(3)).getStr());
                    String message = "Infinite loop near";
                    throw new CodeOptimizationError(message, errorLine, errorChNum);
                }
                
                Line jmpDestLine = ic.getLine(jmpDest);
                if (jmpDestLine.numberOfFields() > 0)
                {
                    sf = (StringField)jmpDestLine.getField(0);
                    funName = sf.getStr();
                    
                    // jeżeli docelowa linijka do której skaczemy też jest skokiem, to możemy zredukować
                    // o ten skok pośredni
                    if (funName.equals(InterpreterFunction.JMP.toString()))
                    {
                        LabelField lf = (LabelField)jmpDestLine.getField(1);
                        Label labelDest = lf.getLabel();
                        // podmieniamy etykietę
                        lfOrig.setLabel(labelDest);
                        
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}
