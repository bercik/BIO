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

/**
 *
 * @author robert
 */
public class IntStringField extends StringField
{
    private final int number;

    public IntStringField(int number)
    {
        super(Integer.toString(number));
        this.number = number;
    }

    public IntStringField(int number, String str)
    {
        super(str);
        this.number = number;
    }

    @Override
    public void writeToBinaryFile(DataOutputStream dos) throws IOException
    {
        dos.writeInt(number);
    }

    public int getNumber()
    {
        return number;
    }
}
