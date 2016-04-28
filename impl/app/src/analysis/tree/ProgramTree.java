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

    @Override
    public String toString()
    {
        String toReturn = "";
        int indent = 0;
        
        for (UserFunction uf : userFunctions)
        {
            toReturn += uf.getName() + "\n";
            
            toReturn += "params: [";
            boolean comma = false;
            for (Param p : uf.getParams())
            {
                toReturn += p.getName() + ", ";
                comma = true;
            }
            if (comma)
            {
                toReturn = toReturn.substring(0, toReturn.length() - 2);
            }
            toReturn += "]\n";
            
            indent += 4;
            for (Call c : uf.getCalls())
            {
                toReturn += indent(indent) + c.getName() + "\n";
                indent += 4;
                toReturn += callToString(c, indent);
                indent -= 4;
                toReturn += "\n";
            }
            indent -= 4;
            
            toReturn += "\n";
        }
        
        return toReturn;
    }
    
    private String callToString(Call c, int indent)
    {
        String toReturn = "";
        
        for (CallParam cp : c.getCallParams())
        {
            if (cp instanceof IdCallParam)
            {
                IdCallParam icp = (IdCallParam)cp;
                toReturn += indent(indent) + "id: " + icp.getName();
            }
            else if (cp instanceof ConstCallParam)
            {
                ConstCallParam ccp = (ConstCallParam)cp;
                toReturn += indent(indent) + 
                        ccp.getValueType().toString() + ": " + (ccp.getValue() != null ? ccp.getValue() : "");
            }
            else if (cp instanceof Call)
            {
                Call cc = (Call)cp;
                toReturn += indent(indent) + cc.getName() + "\n";
                indent += 4;
                    toReturn += callToString(cc, indent);
                indent -= 4;
            }
            
            toReturn += "\n";
        }
        
        return toReturn;
    }
    
    private String indent(int indent)
    {
        String toReturn = "";
        
        for (int i = 0; i < indent; ++i)
        {
            toReturn += " ";
        }
        
        return toReturn;
    }
}
