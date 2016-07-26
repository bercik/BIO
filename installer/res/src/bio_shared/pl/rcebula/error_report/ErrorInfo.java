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
package pl.rcebula.error_report;

import java.util.List;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.error_report.MyFiles.File;

/**
 *
 * @author robert
 */
public class ErrorInfo
{
    private final int lineNum;
    private final int chNum;
    private final MyFiles.File file;

    public ErrorInfo(int lineNum, int chNum, MyFiles.File file)
    {
        this.lineNum = lineNum;
        this.chNum = chNum;
        this.file = file;
    }

    public MyFiles.File getFile()
    {
        return file;
    }
    
    public int getLineNum()
    {
        return lineNum;
    }

    public int getChNum()
    {
        return chNum;
    }

    @Override
    public String toString()
    {
        String str = "[";
        if (file.getFromList().size() == 0)
        {
            str += "In file " + file.getName() + ", ";
        }
        else
        {
            List<File> from = file.getFromList();
            for (int i = from.size() - 1; i >= 0; --i)
            {
                if (i == from.size() - 1)
                {
                    str += "In file " + file.getName() + "\n  included from ";
                }
                else
                {
                    str += "  included from ";
                }
                
                File f = from.get(i);
                str += f.getName();
                str += "\n";
            }
            
            str += "    ";
        }
        
        return str + "line: " + lineNum + ", character: " + chNum + "]";
    }
    
    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 19 * hash + this.lineNum;
        hash = 19 * hash + this.chNum;
        hash = 19 * hash + this.file.getNum();
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ErrorInfo other = (ErrorInfo)obj;
        if (this.lineNum != other.lineNum)
        {
            return false;
        }
        if (this.chNum != other.chNum)
        {
            return false;
        }
        if (this.file.getNum() != other.file.getNum())
        {
            return false;
        }
        return true;
    }
}
