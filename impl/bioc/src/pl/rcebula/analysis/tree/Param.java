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

/**
 *
 * @author robert
 */
public class Param
{
    private final String name;
    private final Integer line;
    private final Integer chNum;

    public Param(String name, Integer line, Integer chNum)
    {
        this.name = name;
        this.line = line;
        this.chNum = chNum;
    }

    public String getName()
    {
        return name;
    }

    public Integer getLine()
    {
        return line;
    }

    public Integer getChNum()
    {
        return chNum;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.name);
        hash = 73 * hash + Objects.hashCode(this.line);
        hash = 73 * hash + Objects.hashCode(this.chNum);
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
        if (!Objects.equals(this.line, other.line))
        {
            return false;
        }
        if (!Objects.equals(this.chNum, other.chNum))
        {
            return false;
        }
        return true;
    }
}
