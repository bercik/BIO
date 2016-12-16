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
package pl.rcebula.analysis.lexer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robert
 */
public enum TokenType
{
    INT(Integer.class),
    FLOAT(Float.class),
    STRING(String.class),
    BOOL(Boolean.class),
    NONE(null),
    OPEN_BRACKET(null),
    CLOSE_BRACKET(null),
    COMMA(null),
    EQUAL(null),
    ID(String.class),
    ID_STRUCT(String.class),
    END(null),
    KEYWORD(String.class),
    EXPRESSION(String.class);

    private final Class valueType;

    private TokenType(Class type)
    {
        this.valueType = type;
    }

    public Class getValueType()
    {
        return valueType;
    }

    public static List<TokenType> getTokenTypesWithNullType()
    {
        List<TokenType> tokenTypes = new ArrayList<>();
        
        for (TokenType tt : values())
        {
            if (tt.getValueType() == null)
            {
                tokenTypes.add(tt);
            }
        }
        
        return tokenTypes;
    }
    
    @Override
    public String toString()
    {
        return this.name().toLowerCase().replaceAll("_", " ");
    }
}
