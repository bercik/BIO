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
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.optimization.graph.FlowGraphAnalyse;
import pl.rcebula.utils.OptimizationStatistics;

/**
 *
 * @author robert
 */
public class CodeOptimizer
{
    private final IntermediateCode ic;
    
    public CodeOptimizer(IntermediateCode ic, OptimizationStatistics statistics)
            throws CodeOptimizationError
    {
        Logger logger = Logger.getGlobal();
        logger.info("CodeOptimizer");
        logger.fine(ic.toStringWithLinesNumber());
        
        this.ic = ic;
        
        new RemovePushPopcSequences(ic, statistics);
        new RemovePushBoolJmpSequences(ic, statistics);
        new RemoveRedundantJumps(ic, statistics);
        new FlowGraphAnalyse(ic, statistics);
        new RemoveJmpsToNextLine(ic, statistics);
        
        logger.fine(ic.toStringWithLinesNumber());
    }
}
