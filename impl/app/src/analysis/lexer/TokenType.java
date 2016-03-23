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
package analysis.lexer;

/**
 *
 * @author robert
 */
public enum TokenType
{
    INT (Integer.class),
    FLOAT (Float.class),
    STRING (String.class),
    BOOL (Boolean.class),
    NONE (null),
    OPEN_BRACKET (null),
    CLOSE_BRACKET (null),
    COMMA (null),
    ID (String.class),
    END (null),
    KEYWORD (String.class);
    
    private final Class valueType;

    private TokenType(Class type)
    {
        this.valueType = type;
    }

    public Class getValueType()
    {
        return valueType;
    }
}
