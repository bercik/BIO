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
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public class Call extends CallParam
{
    private String name;
    private final Call parentCall;
    private final List<CallParam> callParams = new ArrayList<>();
    private List<Integer> orderList = null;
    // używane przy generowaniu funkcji wbudowanych o zmiennej ilości parametrów
    private int repeatCycles = 0;

    public Call(String name, Call parentCall, ErrorInfo errorInfo)
    {
        this(name, parentCall, errorInfo, "", null);
    }
    
    public Call(String name, Call parentCall, ErrorInfo errorInfo, String parName, ErrorInfo parNamErrorInfo)
    {
        super(errorInfo, parName, parNamErrorInfo);
        this.name = name;
        this.parentCall = parentCall;
    }
    
    public void addCallParam(CallParam cp)
    {
        callParams.add(cp);
    }

    public List<Integer> getOrderList()
    {
        return orderList;
    }

    public void setOrderList(List<Integer> orderList)
    {
        this.orderList = orderList;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Call getParentCall()
    {
        return parentCall;
    }

    public List<CallParam> getCallParams()
    {
        return callParams;
    }

    public int getRepeatCycles()
    {
        return repeatCycles;
    }

    public void setRepeatCycles(int repeatCycles)
    {
        this.repeatCycles = repeatCycles;
    }
    
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.callParams);
        hash = 59 * hash + Objects.hashCode(this.repeatCycles);
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
        if (!Objects.equals(this.repeatCycles, other.repeatCycles))
        {
            return false;
        }
        return true;
    }
}
