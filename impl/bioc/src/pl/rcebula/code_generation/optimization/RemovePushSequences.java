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
import pl.rcebula.utils.Statistics;

/**
 *
 * @author robert
 */
public class RemovePushSequences
{
    private final IntermediateCode ic;
    private final Statistics statistics;

    public RemovePushSequences(IntermediateCode ic, Statistics statistics)
    {
        this.ic = ic;
        this.statistics = statistics;
        
        // powtarzamy proces tak długo aż nie dokonujemy żadnych usunięć w kodzie
        while (analyseAndRemove()) { }
    }

    private boolean analyseAndRemove()
    {
        boolean removed = false;
        int lineEnd = ic.numberOfLines() - 1;

        while (lineEnd >= 0)
        {
            Line line = ic.getLine(lineEnd);

            int toRemove = howMuchToRemove(line);

            if (toRemove == 0)
            {
                --lineEnd;
                continue;
            }

            int lineStart = lineEnd;
            if (toRemove > 0)
            {
                boolean cont = false;
                // jeżeli się da to usuń toRemove ilość pushy od tego miejsca i to miejsce
                while (toRemove-- > 0)
                {
                    if (isPush(ic.getLine(lineStart-1)))
                    {
                        --lineStart;
                    }
                    else
                    {
                        cont = true;
                        break;
                    }
                }
                
                // kontynuuj przetwarzanie nie usuwając żadnych linii
                if (cont)
                {
                    --lineEnd;
                    continue;
                }
            }
            else if (toRemove < 0)
            {
                // przesuwamy o jeden do góry, ponieważ nie chcemy usunąć CLEAR_STACK
                --lineEnd;
                // usuń wszystkie pushe od tego miejsca (bez tego miejsca)
                while (true)
                {
                    if (!isPush(ic.getLine(lineStart-1)))
                    {
                        break;
                    }
                    --lineStart;
                }
            }
            
            // usuwamy linie
            int c = lineEnd - lineStart + 1;

            if (c > 0)
            {
                removed = true;
            }
            while (c-- > 0)
            {
                ic.removeLine(lineStart);
                statistics.addPushSequenceRemoved();
            }

            lineEnd = lineStart - 1;
        }
        
        return removed;
    }

    private boolean isPush(Line line)
    {
        if (line.numberOfFields() > 0)
        {
            String functionName = ((StringField)line.getField(0)).getStr();

            return functionName.equals(InterpreterFunction.PUSH.toString());
        }

        return false;
    }

    // -1 oznacza tyle ile jest możliwe
    // inna liczba oznacza dokładną liczbę
    private int howMuchToRemove(Line line)
    {
        if (line.numberOfFields() > 0)
        {
            String functionName = ((StringField)line.getField(0)).getStr();

            if (functionName.equals(InterpreterFunction.CLEAR_STACK.toString()))
            {
                return -1;
            }
            else if (functionName.equals(InterpreterFunction.POPC.toString()))
            {
                StringField f = (StringField)line.getField(1);
                return Integer.parseInt(f.getStr());
            }
        }

        return 0;
    }
}
