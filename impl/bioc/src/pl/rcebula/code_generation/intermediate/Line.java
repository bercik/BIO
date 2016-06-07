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
package pl.rcebula.code_generation.intermediate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.rcebula.Constants;

/**
 *
 * @author robert
 */
public class Line
{  
    private final List<IField> fields = new ArrayList<>();
    private final List<Label> labels = new ArrayList<>();
    private int line = 0;
    
    public Line()
    {
    }
    
    public Line(IField... fields)
    {
        this.fields.addAll(Arrays.asList(fields));
    }
    
    public Line(List<IField> fields)
    {
        this.fields.addAll(fields);
    }

    public int getLine()
    {
        return line;
    }

    public void setLine(int line)
    {
        this.line = line;
        
        for (Label l : labels)
        {
            l.setLine(line);
        }
    }
    
    public IField getField(int index)
    {
        return fields.get(index);
    }
    
    public int numberOfFields()
    {
        return fields.size();
    }
    
    public void addLabel(Label label)
    {
        label.setLine(line);
        labels.add(label);
    }
    
    public void addLabels(List<Label> labels)
    {
        for (Label l : labels)
        {
            l.setLine(line);
        }
        
        this.labels.addAll(labels);
    }
    
    public List<Label> getLabels()
    {
        return labels;
    }
    
    public void move(int shift)
    {
        for (Label l : labels)
        {
            l.move(shift);
        }
        
        line += shift;
    }

    @Override
    public String toString()
    {
        String result = "";
        
        for (IField f : fields)
        {
            result += f.toString() + Constants.fieldsSeparator;
        }
        
        if (fields.size() > 0)
        {
            result = result.substring(0, result.length() - 1);
        }
        
        return result;
    }
    
    public boolean isEmptyLine()
    {
        return numberOfFields() == 0;
    }
}
