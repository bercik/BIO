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

import java.util.Collections;
import java.util.List;
import pl.rcebula.utils.Pair;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;
import pl.rcebula.code_generation.optimization.CodeOptimizationError;

/**
 *
 * @author robert
 */
public class FindInfiniteLoops
{
    private final IntermediateCode ic;
    private final FlowGraph fg;

    public FindInfiniteLoops(IntermediateCode ic, FlowGraph fg)
            throws CodeOptimizationError
    {
        this.ic = ic;
        this.fg = fg;
        
        analyse();
    }
    
    private void analyse()
            throws CodeOptimizationError
    {
        CodeBlock minLengthCycleCodeBlock = null;
        int minLengthCycle = Integer.MAX_VALUE;
        List<CodeBlock> codeBlocks = fg.getCodeBlocks();
        List<CodeBlock> endBlocks = fg.getEndBlocks();
        // sprawdzamy wszystkie bloki
        for (int i = 0; i < codeBlocks.size(); ++i)
        {
            CodeBlock cb = codeBlocks.get(i);
            // jeżeli to wierzchołek końcowy to szukamy dalej
            if (endBlocks.contains(cb))
            {
                continue;
            }
            
            fg.resetVisited();
            List<Integer> cycles = cb.traverse(cb);
            
            List<CodeBlock> visitedCodeBlocks = fg.getVisited();
            // sprawdź czy został odwiedzony któryś z bloków końcowych, jeżeli nie to
            if (Collections.disjoint(visitedCodeBlocks, endBlocks))
            {
                // sprawdź czy znaleziono jakiś cykl
                if (cycles.size() > 0)
                {
                    // znajdź najmniejszy element
                    int minVal = Collections.min(cycles);
                    // jeżeli mniejszy niż dotychczasowy to zapisz
                    if (minVal < minLengthCycle)
                    {
                        minLengthCycle = minVal;
                        minLengthCycleCodeBlock = cb;
                    }
                }
            }
        }
        
        // jeżeli znaleziono jakiś blok, który należy do nieskończonej pętli
        if (minLengthCycleCodeBlock != null)
        {
            CodeBlock cb = minLengthCycleCodeBlock;
            
            // szukaj linii w tym bloku, która zawiera informacje o lini i znaku występowania w kodzie źródłowym
            for (int lnr = cb.getStart(); lnr <= cb.getEnd(); ++lnr)
            {
                Line line = ic.getLine(lnr);
                
                Pair<Integer, Integer> lineAndChPair = getLineAndCh(line);
                if (lineAndChPair.getLeft() != -1)
                {
                    int errorLine = lineAndChPair.getLeft();
                    int errorChNum = lineAndChPair.getRight();
                    
                    String message = "Infinite loop near";
                    throw new CodeOptimizationError(message, errorLine, errorChNum);
                }
            }
            
            String message = "Infinite loop";
            throw new CodeOptimizationError(message);
        }
    }
    
    private Pair<Integer, Integer> getLineAndCh(Line line)
    {
        StringField sf = (StringField)line.getField(0);
        String fn = sf.getStr();
        
        Integer l = -1;
        Integer ch = -1;
        
        if (fn.equals(InterpreterFunction.CALL.toString()) 
                || fn.equals(InterpreterFunction.CALL_LOC.toString()) 
                || fn.equals(InterpreterFunction.PUSH.toString()) 
                || fn.equals(InterpreterFunction.JMP.toString()) 
                || fn.equals(InterpreterFunction.JMP_IF_FALSE.toString()))
        {
            StringField sf2 = (StringField)line.getField(2);
            l = Integer.parseInt(sf2.getStr());
            
            StringField sf3 = (StringField)line.getField(3);
            ch = Integer.parseInt(sf3.getStr());
        }
        
        return new Pair<Integer, Integer>(l, ch);
    }
}
