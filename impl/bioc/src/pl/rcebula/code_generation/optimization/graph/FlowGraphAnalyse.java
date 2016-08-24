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

import java.util.logging.Logger;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.optimization.CodeOptimizationError;
import pl.rcebula.code_generation.optimization.IOptimizer;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.code_generation.optimization.OptimizationStatistics;

/**
 *
 * @author robert
 */
public class FlowGraphAnalyse implements IOptimizer
{
    private final IntermediateCode ic;
    private final OptimizationStatistics statistics;
    private final MyFiles files;
    
    private boolean optimize = false;

    public FlowGraphAnalyse(IntermediateCode ic, OptimizationStatistics statistics,
            MyFiles files)
            throws CodeOptimizationError
    {
        Logger logger = Logger.getGlobal();
        logger.info("FlowGraphAnalyse");
        
        this.ic = ic;
        this.statistics = statistics;
        this.files = files;
        
        analyse();
    }

    @Override
    public boolean isOptimized()
    {
        return optimize;
    }
    
    private void analyse()
            throws CodeOptimizationError
    {
        // szukamy początku pierwszej funkcji
        int firstFunctionStart = 0;
        while (!ic.getLine(firstFunctionStart).isEmptyLine()) 
        {
            firstFunctionStart++;
        }
        
        int end = ic.numberOfLines() - 1;
        
        // analizujemy kod od końca wyodrębniając funkcję
        // ostatnia linijka kodu jest zawsze pusta
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
            throws CodeOptimizationError
    {
        // tworzymy graf przepływu
        FlowGraph fg = new FlowGraph(ic, start, end);
        // usuwamy nieużywane bloki kodu
        IOptimizer optimizer = new RemoveUnusedCodeBlocks(ic, statistics, fg);
        optimize = optimizer.isOptimized();
        // szukamy nieskończonych pętli
        new FindInfiniteLoops(ic, fg, files);
    }
}
