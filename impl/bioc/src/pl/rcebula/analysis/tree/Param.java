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
public class Param
{
    private final String name;
    private final ErrorInfo errorInfo;

    public Param(String name, ErrorInfo errorInfo)
    {
        this.name = name;
        this.errorInfo = errorInfo;
    }

    public String getName()
    {
        return name;
    }

    public ErrorInfo getErrorInfo()
    {
        return errorInfo;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.name);
        hash = 73 * hash + Objects.hashCode(this.errorInfo);
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
        final Param other = (Param)obj;
        if (!Objects.equals(this.name, other.name))
        {
            return false;
        }
        if (!Objects.equals(this.errorInfo, other.errorInfo))
        {
            return false;
        }
        return true;
    }
}
