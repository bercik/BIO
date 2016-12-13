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

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.code.ValueType;
import pl.rcebula.analysis.lexer.Token;

/**
 *
 * @author robert
 */
public class ConstCallParam extends CallParam
{
    private final ValueType valueType;
    private final Object value;

    public ConstCallParam(Token<?> token, ErrorInfo errorInfo)
    {
        this(token, errorInfo, "", null);
    }
    
    public ConstCallParam(Token<?> token, ErrorInfo errorInfo, String parName, ErrorInfo parNameErrorInfo)
    {
        super(errorInfo, parName, parNameErrorInfo);

        switch (token.getTokenType())
        {
            case BOOL:
                Boolean b = (Boolean)token.getValue();
                valueType = ValueType.BOOL;
                value = b;
                break;
            case INT:
                Integer i = (Integer)token.getValue();
                valueType = ValueType.INT;
                value = i;
                break;
            case FLOAT:
                Float f = (Float)token.getValue();
                valueType = ValueType.FLOAT;
                value = f;
                break;
            case STRING:
                String s = (String)token.getValue();
                valueType = ValueType.STRING;
                value = s;
                break;
            case NONE:
                valueType = ValueType.NONE;
                value = null;
                break;
            default:
                throw new RuntimeException("ConstCallParam expected INT, FLOAT, STRING, BOOL"
                        + " or NONE token, got " + token.getTokenType().toString());
        }
    }

    public ValueType getValueType()
    {
        return valueType;
    }

    public Object getValue()
    {
        return value;
    }
}
