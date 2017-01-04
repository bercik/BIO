/*
 * Copyright (C) 2017 robert
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

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.ErrorInfoError;
import pl.rcebula.preprocessor.FilesContent;

/**
 *
 * @author robert
 */
public class ShowErrorLine
{
    
    
    public static void show(ErrorInfoError err, FilesContent filesContent)
    {
        // sprawdzamy czy istnieje error info i czy numer pliku to nie -1 (czyli nie jest to kod wygenerowany 
        // przez kompilator)
        if (err.getErrorInfo() != null && err.getErrorInfo().getFile().getNum() != -1)
        {
            ErrorInfo ei = err.getErrorInfo();
            int lineNum = ei.getLineNum();
            int chNum = ei.getChNum();

            // lineNum - 1, bo linie w error info numerowane od 1, a w filesContent od 0
            String line = filesContent.getFileContentLine(ei.getFile().getNum(), lineNum - 1);

            System.out.println();
            System.out.println(line);
            // chNum - 1, bo na miejscu chNum chcemy wyświetlić znak "^"
            System.out.println(indent(chNum - 1) + "^");
        }
    }
    
    private static String indent(int num)
    {
        StringBuilder sb = new StringBuilder();
        while (num-- > 0)
        {
            sb.append(" ");
        }
        
        return sb.toString();
    }
}
