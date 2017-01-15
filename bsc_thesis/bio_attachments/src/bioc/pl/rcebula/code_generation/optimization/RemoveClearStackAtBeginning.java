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
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;

/**
 *
 * @author robert
 */
public class RemoveClearStackAtBeginning implements IOptimizer
{
    private final IntermediateCode ic;
    private final OptimizationStatistics statistics;
    
    private boolean optimize = false;

    public RemoveClearStackAtBeginning(IntermediateCode ic, OptimizationStatistics statistics)
    {
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
        // idziemy od początku szukając nowej linii. Jeżeli taką napotkamy to pomijamy następną i sprawdzamy
        // czy kolejna to CLEAR_STACK. Jeżeli tak to usuwamy
        int c = 0;
        while (c < ic.numberOfLines())
        {
            Line line = ic.getLine(c);
            
            if (line.isEmptyLine())
            {
                c += 2;
                
                if (c < ic.numberOfLines())
                {
                    line = ic.getLine(c);
                    
                    if (line.numberOfFields() > 0)
                    {
                        String funName = ((StringField)line.getField(0)).getStr();
                        
                        if (funName.equals(InterpreterFunction.CLEAR_STACK.toString()))
                        {
                            ic.removeLine(c);
                            statistics.addClearStackAtBeginningRemoved();
                            optimize = true;
                            --c;
                        }
                    }
                }
                else
                {
                    break;
                }
            }
            
            ++c;
        }
    }
}
