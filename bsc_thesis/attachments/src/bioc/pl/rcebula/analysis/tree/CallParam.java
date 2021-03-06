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

import java.util.Objects;
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public abstract class CallParam
{
    private final ErrorInfo errorInfo;
    private final String parName;
    private final ErrorInfo parNameErrorInfo;

    public CallParam(ErrorInfo errorInfo, String parName, ErrorInfo parNameErrorInfo)
    {
        this.errorInfo = errorInfo;
        this.parName = parName;
        this.parNameErrorInfo = parNameErrorInfo;
    }

    public ErrorInfo getErrorInfo()
    {
        return errorInfo;
    }

    public String getParName()
    {
        return parName;
    }

    public ErrorInfo getParNameErrorInfo()
    {
        return parNameErrorInfo;
    }
    
    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.errorInfo);
        hash = 89 * hash + Objects.hashCode(this.parName);
        hash = 89 * hash + Objects.hashCode(this.parNameErrorInfo);
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
        final CallParam other = (CallParam) obj;
        if (!Objects.equals(this.parName, other.parName))
        {
            return false;
        }
        if (!Objects.equals(this.errorInfo, other.errorInfo))
        {
            return false;
        }
        if (!Objects.equals(this.parNameErrorInfo, other.parNameErrorInfo))
        {
            return false;
        }
        return true;
    }
}
