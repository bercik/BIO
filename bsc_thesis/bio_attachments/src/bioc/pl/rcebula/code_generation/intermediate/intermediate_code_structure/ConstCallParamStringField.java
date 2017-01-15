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

import java.io.DataOutputStream;
import java.io.IOException;
import pl.rcebula.Constants;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.code.ValueType;
import pl.rcebula.utils.StringUtils;

/**
 *
 * @author robert
 */
public class ConstCallParamStringField extends StringField
{
    private final ConstCallParam ccp;

    public ConstCallParamStringField(ConstCallParam ccp)
    {
        super(constructString(ccp));
        this.ccp = ccp;
    }
    
    private static String constructString(ConstCallParam ccp)
    {
        String str = "";

        str += ccp.getValueType().toString();
        str += Constants.valueSeparator;

        Object val = ccp.getValue();
        if (val != null)
        {
            String s = val.toString();
            // jeżeli string to otaczamy cudzysłowiem i dodajemy \ przed każdym \
            if (ccp.getValueType().equals(ValueType.STRING))
            {
                s = StringUtils.addSlashToSpecialCharacters(s);
                s = "\"" + s + "\"";
            }

            str += s;
        }
        
        return str;
    }

    @Override
    public void writeToBinaryFile(DataOutputStream dos) throws IOException
    {
        dos.writeByte(ccp.getValueType().getOpcode());
        
        switch (ccp.getValueType())
        {
            case BOOL:
                dos.writeBoolean((boolean)ccp.getValue());
                break;
            case FLOAT:
                dos.writeFloat((float)ccp.getValue());
                break;
            case INT:
                dos.writeInt((int)ccp.getValue());
                break;
            case STRING:
                dos.writeUTF((String)ccp.getValue());
                break;
            case NONE:
                break;
            default:
                throw new RuntimeException("Unrecognized ValueType: " + ccp.getValueType().toString());
        }
    }
}
