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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import pl.rcebula.Constants;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code_generation.intermediate.IntermediateCode;
import pl.rcebula.code_generation.intermediate.LabelField;
import pl.rcebula.code_generation.intermediate.Line;
import pl.rcebula.code_generation.intermediate.StringField;

/**
 *
 * @author robert
 */
public class FlowGraph
{
    private List<CodeBlock> codeBlocks = new ArrayList<>();
    private List<CodeBlock> endBlocks = new ArrayList<>();
    private CodeBlock startBlock = null;

    private final IntermediateCode ic;
    private final int start;
    private final int end;

    public FlowGraph(IntermediateCode ic, int start, int end)
    {
        this.ic = ic;
        this.start = start;
        this.end = end;

        construct();
    }

    private void construct()
    {
        // pomocnicza tablica ln
        TreeSet<Integer> ln = new TreeSet<>();

        // przejdź po wszystkich linijkach kodu
        for (int i = start; i < end; ++i)
        {
            Line l = ic.getLine(i);
            StringField sf = (StringField)l.getField(0);
            String fn = sf.getStr();

            // jeżeli funkcja jmp lub jmp_if_false
            if (fn.equals(InterpreterFunction.JMP.toString())
                    || fn.equals(InterpreterFunction.JMP_IF_FALSE.toString()))
            {
                LabelField lf = (LabelField)l.getField(1);
                int nr = lf.getLabel().getLine();
                ln.add(nr);
                ln.add(i + 1);
            }
            // jeżeli funkcja to call_loc, return
            else if (fn.equals(InterpreterFunction.CALL_LOC.toString()))
            {
                StringField sf2 = (StringField)l.getField(1);
                String fn2 = sf2.getStr();
                if (fn2.equals(Constants.returnFunctionName))
                {
                    ln.add(i + 1);
                }
            }
        }
        
        // konwertujemy tree set na array list
        ArrayList<Integer> lna = new ArrayList<>(ln);
        // tworzymy bloki (na razie bez odniesień do innych)
        for (int i = 0; i < lna.size() - 1; ++i)
        {
            int blockStart = lna.get(i);
            int blockEnd = lna.get(i + 1) - 1;
            codeBlocks.add(new CodeBlock(blockStart, blockEnd));
        }
        
        // przypisz pierwszy blok jako startowy
        startBlock = codeBlocks.get(0);
        
        // tworzymy połączenia między blokami
        for (int i = 0; i < codeBlocks.size(); ++i)
        {
            CodeBlock cb = codeBlocks.get(i);
            int lnr = cb.getEnd();
            Line l = ic.getLine(lnr);
            StringField sf = (StringField)l.getField(0);
            String fn = sf.getStr();
            
            // jeżeli funkcja jmp
            if (fn.equals(InterpreterFunction.JMP.toString()))
            {
                LabelField lf = (LabelField)l.getField(1);
                int nr = lf.getLabel().getLine();
                CodeBlock ocb = findCodeBlock(nr);
                cb.addOutCodeBlock(ocb);
            }
            // jeżeli funkcja jmp_if_false
            else if (fn.equals(InterpreterFunction.JMP_IF_FALSE.toString()))
            {
                LabelField lf = (LabelField)l.getField(1);
                int nr = lf.getLabel().getLine();
                CodeBlock ocb = findCodeBlock(nr);
                cb.addOutCodeBlock(ocb);
                
                ocb = findCodeBlock(lnr + 1);
                cb.addOutCodeBlock(ocb);
            }
            // jeżeli funkcja to call_loc, return
            else if (fn.equals(InterpreterFunction.CALL_LOC.toString()))
            {
                StringField sf2 = (StringField)l.getField(1);
                String fn2 = sf2.getStr();
                if (fn2.equals(Constants.returnFunctionName))
                {
                    // dodaj do end blocks
                    endBlocks.add(cb);
                }
                else
                {
                    CodeBlock ocb = findCodeBlock(lnr + 1);
                    cb.addOutCodeBlock(ocb);
                }
            }
            else
            {
                CodeBlock ocb = findCodeBlock(lnr + 1);
                cb.addOutCodeBlock(ocb);
            }
        }
    }
    
    private CodeBlock findCodeBlock(int startLine)
    {
        for (CodeBlock cb : codeBlocks)
        {
            if (cb.getStart() == startLine)
            {
                return cb;
            }
        }
        
        throw new RuntimeException("There is no code block which starts in " + startLine + " line.");
    }

    public List<CodeBlock> getCodeBlocks()
    {
        return codeBlocks;
    }

    public List<CodeBlock> getEndBlocks()
    {
        return endBlocks;
    }

    public CodeBlock getStartBlock()
    {
        return startBlock;
    }
    
    public void resetVisited()
    {
        for (CodeBlock cb : codeBlocks)
        {
            cb.resetVisited();
        }
    }
}
