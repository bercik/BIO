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
package pl.rcebula.code_generation.optimization.graph;

import pl.rcebula.code_generation.intermediate.IntermediateCode;
import pl.rcebula.code_generation.optimization.CodeOptimizationError;
import pl.rcebula.utils.Statistics;

/**
 *
 * @author robert
 */
public class FlowGraphAnalyse
{
    private final IntermediateCode ic;
    private final Statistics statistics;

    public FlowGraphAnalyse(IntermediateCode ic, Statistics statistics)
            throws CodeOptimizationError
    {
        this.ic = ic;
        this.statistics = statistics;
        
        analyse();
    }
    
    private void analyse()
            throws CodeOptimizationError
    {
        // szukamy początku pierwszej funkcji
        int firstFunctionStart = 0;
        while (!ic.getLine(firstFunctionStart).isEmptyLine()) {}
        
        int end = ic.numberOfLines() - 1;
        
        // analizujemy kod od końca wyodrębniając funkcję
        for (int i = end - 1; i >= firstFunctionStart; --i)
        {
            if (ic.getLine(i).isEmptyLine())
            {
                // początek funkcji
                int start = i + 2;
                // funkcja : <start, end)
                // przeprowadzamy analizę na funkcji
                analyseFunction(start, end);
                // koniec następnej funkcji znajduje się w aktualnej linijce
                end = i;
            }
        }
    }
    
    // <start, end)
    private void analyseFunction(int start, int end)
    {
        // tworzymy graf przepływu
        FlowGraph fg = new FlowGraph(ic, start, end);
    }
}
