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
package analysis.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author robert
 */
public class ProgramTree
{
    private final List<UserFunction> userFunctions = new ArrayList<>();
    
    public void addUserFunction(UserFunction uf)
    {
        userFunctions.add(uf);
    }

    public List<UserFunction> getUserFunctions()
    {
        return userFunctions;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.userFunctions);
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
        final ProgramTree other = (ProgramTree)obj;
        if (!Objects.equals(this.userFunctions, other.userFunctions))
        {
            return false;
        }
        return true;
    }
}
