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

import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.CallParam;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.IdCallParam;

/**
 *
 * @author robert
 */
public enum ParamType
{
    ID, // identyfikator
    CALL, // wywołanie funkcyjne
    CONST, // stała
    ALL; // wszystko
    
    @Override
    public String toString()
    {
        return this.name().toLowerCase().replace("_", " ");
    }
    
    public static ParamType convert(CallParam callParam)
    {
        if (callParam instanceof Call)
        {
            return ParamType.CALL;
        }
        else if (callParam instanceof IdCallParam)
        {
            return ParamType.ID;
        }
        else if (callParam instanceof ConstCallParam)
        {
            return ParamType.CONST;
        }
        else
        {
            throw new RuntimeException("Unrecognized call param");
        }
    }
    
    public static boolean compare(ParamType pt1, ParamType pt2)
    {
        // jeżeli pt1 jest all to zawsze true
        if (pt1.equals(ParamType.ALL))
        {
            return true;
        }
        else
        {
            return pt1.equals(pt2);
        }
    }
}
