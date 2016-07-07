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
import pl.rcebula.code_generation.intermediate.IntermediateCode;
import pl.rcebula.utils.Statistics;

/**
 *
 * @author robert
 */
public class RemoveUnusedCodeBlocks
{
    private final IntermediateCode ic;
    private final Statistics statistics;
    private final FlowGraph fg;

    public RemoveUnusedCodeBlocks(IntermediateCode ic, Statistics statistics, FlowGraph fg)
    {
        this.ic = ic;
        this.statistics = statistics;
        this.fg = fg;
        
        analyseAndRemove();
    }
    
    private void analyseAndRemove()
    {
        // wykrywamy nieużywane bloki kodu przechodząc graf od bloku startowego
        fg.resetVisitedAndCyclesLength();
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
            }
            // usuwamy blok kodu z grafu przepływu
            fg.removeCodeBlock(cb);
        }
    }
}
