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
package pl.rcebula.analysis.semantic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author robert
 */
public class BuiltinFunction
{
    private final String name;
    private final boolean special;
    private final List<ParamType> params;

    public BuiltinFunction(String name, boolean special, List<ParamType> params)
    {
        this.name = name;
        this.special = special;
        this.params = new ArrayList<>(params);
    }
    
    public BuiltinFunction(String name, boolean special, ParamType... params)
    {
        this.name = name;
        this.special = special;
        this.params = Arrays.asList(params);
    }

    public String getName()
    {
        return name;
    }

    public boolean isSpecial()
    {
        return special;
    }

    public List<ParamType> getParams()
    {
        return Collections.unmodifiableList(params);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + (this.special ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.params);
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
        final BuiltinFunction other = (BuiltinFunction)obj;
        if (this.special != other.special)
        {
            return false;
        }
        if (!Objects.equals(this.name, other.name))
        {
            return false;
        }
        if (!Objects.equals(this.params, other.params))
        {
            return false;
        }
        return true;
    }
}
