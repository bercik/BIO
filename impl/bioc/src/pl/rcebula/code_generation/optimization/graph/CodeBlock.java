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

/**
 *
 * @author robert
 */
public class CodeBlock
{
    private final int start;
    private final int end;
    private final List<CodeBlock> out = new ArrayList<>();
    private boolean visited = false;

    public CodeBlock(int start, int end)
    {
        this.start = start;
        this.end = end;
    }
    
    public void addOutCodeBlock(CodeBlock cb)
    {
        out.add(cb);
    }

    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }

    public List<CodeBlock> getOut()
    {
        return out;
    }

    public boolean isVisited()
    {
        return visited;
    }
    
    public void resetVisited()
    {
        visited = false;
    }

    public void traverse()
    {
        visited = true;
        
        for (CodeBlock cb : out)
        {
            if (!cb.isVisited())
            {
                cb.traverse();
            }
        }
    }
    
    public List<Integer> traverse(CodeBlock origin)
    {
        List<Integer> cyclesLength = new ArrayList<>();
        traverse(origin, 0, cyclesLength);
        return cyclesLength;
    }
    
    private void traverse(CodeBlock origin, int length, List<Integer> cyclesLength)
    {
        visited = true;
        
        for (CodeBlock cb : out)
        {
            // jeżeli wierzchołek początkowy to mamy cykl
            if (cb == origin)
            {
                cyclesLength.add(length + 1);
            }
            else if (!cb.isVisited())
            {
                cb.traverse(origin, length + 1, cyclesLength);
            }
        }
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final CodeBlock other = (CodeBlock)obj;
        if (this.start != other.start)
        {
            return false;
        }
        if (this.end != other.end)
        {
            return false;
        }
        
        if (this.out.size() != other.out.size())
        {
            return false;
        }
        
        for (int i = 0; i < this.out.size(); ++i)
        {
            CodeBlock thisCb = this.out.get(i);
            CodeBlock otherCb = other.out.get(i);
            
            if (thisCb.getStart() != otherCb.getStart())
            {
                return false;
            }
            if (thisCb.getEnd() != otherCb.getEnd())
            {
                return false;
            }
        }
        
        return true;
    }
}
