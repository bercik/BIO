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
package pl.rcebula.code;

/**
 *
 * @author robert
 */
public enum ValueType
{
    // UWAGA! Upewnij się, że wartości opcode są różne od wartości w IdValueType
    INT(Integer.class, (byte)3),
    FLOAT(Float.class, (byte)4),
    STRING(String.class, (byte)5),
    BOOL(Boolean.class, (byte)6),
    NONE(null, (byte)7);
    
    private final Class type;
    private final byte opcode;

    private ValueType(Class type, byte opcode)
    {
        this.type = type;
        this.opcode = opcode;
    }

    public byte getOpcode()
    {
        return opcode;
    }

    public Class getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return this.name().toLowerCase().replace("_", " ");
    }
}
