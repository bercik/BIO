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

import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public class SemanticError extends Exception
{
    public SemanticError(String message)
    {
        super(message);
    }
    
    public SemanticError(ErrorInfo ei, String message)
    {
        super(ei.toString() + ": " + message);
    }
    
    public SemanticError(ErrorInfo ei1, String message1, 
            ErrorInfo ei2, String message2)
    {
        super(ei1.toString() + ": " + message1 + 
                ei2.toString() + ": " + message2);
    }
}
