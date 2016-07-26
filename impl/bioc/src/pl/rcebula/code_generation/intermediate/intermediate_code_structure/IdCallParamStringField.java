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
package pl.rcebula.code_generation.intermediate.intermediate_code_structure;

import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;
import java.io.DataOutputStream;
import java.io.IOException;
import pl.rcebula.Constants;
import pl.rcebula.analysis.tree.IdCallParam;
import pl.rcebula.code.IdValueType;

/**
 *
 * @author robert
 */
public class IdCallParamStringField extends StringField
{
    private final IdCallParam icp;
    private final IdValueType idValueType;

    public IdCallParamStringField(IdCallParam icp, boolean isVar)
    {
        super(constructString(icp, isVar));
        this.icp = icp;
        
        if (isVar)
        {
            idValueType = IdValueType.VAR;
        }
        else
        {
            idValueType = IdValueType.ID;
        }
    }
    
    private static String constructString(IdCallParam icp, boolean isVar)
    {
        // id/var:value
        String str = "";
        if (isVar)
        {
            str += IdValueType.VAR.toString();
        }
        else
        {
            str += IdValueType.ID.toString();
        }

        str += Constants.valueSeparator;
        str += icp.getName();
        
        return str;
    }

    @Override
    public void writeToBinaryFile(DataOutputStream dos) throws IOException
    {
        dos.writeByte(idValueType.getOpcode());
        dos.writeUTF(icp.getName());
    }
}
