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
package pl.rcebula.preprocessor;

/**
 *
 * @author robert
 */
public class PreprocessorError extends Exception
{
    public PreprocessorError(String message)
    {
        super(message);
    }
    
    public PreprocessorError(String file, String message, int line)
    {
        super("[file: " + file + ", line: " + line + "]: " + message);
    }
    
    public PreprocessorError(String file, String message, int line, int ch)
    {
        super("[file: " + file + ", line: " + line + ", character: " + ch + "]: " + message);
    }
}
