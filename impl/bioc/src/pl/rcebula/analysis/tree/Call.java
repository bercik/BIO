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
package pl.rcebula.analysis.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author robert
 */
public class Call extends CallParam
{
    private final String name;
    private final Call parentCall;
    private final List<CallParam> callParams = new ArrayList<>();

    public Call(String name, Call parentCall, Integer line, Integer chNum)
    {
        super(line, chNum);
        this.name = name;
        this.parentCall = parentCall;
    }

    public void addCallParam(CallParam cp)
    {
        callParams.add(cp);
    }
    
    public String getName()
    {
        return name;
    }

    public Call getParentCall()
    {
        return parentCall;
    }

    public List<CallParam> getCallParams()
    {
        return callParams;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.callParams);
        return hash;
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
        final Call other = (Call)obj;
        if (!Objects.equals(this.name, other.name))
        {
            return false;
        }
        if (!Objects.equals(this.callParams, other.callParams))
        {
            return false;
        }
        return true;
    }
}
