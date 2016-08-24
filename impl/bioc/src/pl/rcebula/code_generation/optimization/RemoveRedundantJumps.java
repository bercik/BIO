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
public class RemoveRedundantJumps implements IOptimizer
{
    private final IntermediateCode ic;
    private final OptimizationStatistics statistics;
    private final Logger logger = Logger.getGlobal();
    
    private boolean optimize = false;

    public RemoveRedundantJumps(IntermediateCode ic, OptimizationStatistics statistics)
    {
        logger.info("RemoveRedundantJumps");
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
                optimize = true;
            }
        }
    }

    private boolean removeRedundantJump(int lineNumber)
    {
        Line line = ic.getLine(lineNumber);

        if (line.numberOfFields() > 0)
        {
            StringField sf = (StringField)line.getField(0);
            String funName = sf.getStr();

            // jeżeli to skok warunkowy lub bezwarunkowy
            if (funName.equals(InterpreterFunction.JMP.toString())
                    || funName.equals(InterpreterFunction.JMP_IF_FALSE.toString()))
            {
                LabelField lfOrig = (LabelField)line.getField(1);
                Label labelOrig = lfOrig.getLabel();
                int jmpDest = labelOrig.getLine();

                // jeżeli skaczemy do samego siebie to zwróc false
                if (jmpDest == lineNumber)
                {
                    return false;
                }

                Line jmpDestLine = ic.getLine(jmpDest);
                if (jmpDestLine.numberOfFields() > 0)
                {
                    sf = (StringField)jmpDestLine.getField(0);
                    funName = sf.getStr();

                    // jeżeli docelowa linijka do której skaczemy też jest skokiem, to możemy zredukować
                    // o ten skok pośredni, chyba, że skacze sama do siebie to tego nie robimy
                    if (funName.equals(InterpreterFunction.JMP.toString()))
                    {
                        LabelField lf = (LabelField)jmpDestLine.getField(1);
                        Label labelDest = lf.getLabel();
                        int jmpLabelDest = labelDest.getLine();
                        if (jmpLabelDest == jmpDest)
                        {
                            return false;
                        }
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
