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
package pl.rcebula.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author robert
 */
public class StringUtils
{
    public static Map<Character, Character> specialCharacters
            = new HashMap<Character, Character>()
    {
        {
            put('n', '\n');
            put('r', '\r');
            put('"', '"');
            put('f', '\f');
            put('b', '\b');
            put('\\', '\\');
            put('t', '\t');
        }
    };

    public static String addSlashToSpecialCharacters(String s)
    {
        for (Character key : specialCharacters.keySet())
        {
            Character value = specialCharacters.get(key);
            if (value.equals('\\'))
            {
                s = s.replaceAll("\\\\", "\\\\\\\\");
            }
            else
            {
                s = s.replaceAll(value.toString(), "\\\\" + key.toString());
            }
        }
        
        return s;
    }
}
