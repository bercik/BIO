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

import java.util.List;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.optimization.IOptimizer;
import pl.rcebula.code_generation.optimization.OptimizationStatistics;

/**
 *
 * @author robert
 */
public class RemoveUnusedCodeBlocks implements IOptimizer
{
    private final IntermediateCode ic;
    private final OptimizationStatistics statistics;
    private final FlowGraph fg;
    
    private boolean optimize = false;

    public RemoveUnusedCodeBlocks(IntermediateCode ic, OptimizationStatistics statistics, FlowGraph fg)
    {
        this.ic = ic;
        this.statistics = statistics;
        this.fg = fg;
        
        analyseAndRemove();
    }

    @Override
    public boolean isOptimized()
    {
        return optimize;
    }
    
    private void analyseAndRemove()
    {
        // wykrywamy nieużywane bloki kodu przechodząc graf od bloku startowego
        fg.resetVisited();
        fg.getStartBlock().traverse();
        // wszystkie bloki, które nie zostały odwiedzone są nieosiągalne i można je bezpiecznie usunąć
        List<CodeBlock> notVisited = fg.getNotVisited();
        
        // usuwamy od tyłu
        for (int i = notVisited.size() - 1; i >= 0; --i)
        {
            CodeBlock cb = notVisited.get(i);
            for (int lnr = cb.getEnd(); lnr >= cb.getStart(); --lnr)
            {
                ic.removeLineWithLabels(lnr);
                statistics.addUnusedCodeBlocksLinesRemoved();
                optimize = true;
            }
            // usuwamy blok kodu z grafu przepływu
            fg.removeCodeBlock(cb);
        }
    }
}
