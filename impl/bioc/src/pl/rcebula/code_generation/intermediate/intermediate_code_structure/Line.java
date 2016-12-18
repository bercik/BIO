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
    private final boolean empty;
    private int line = 0;
    
    // fields for showin diffrence between unoptimized and optimized code
    private boolean removed = false;
    private boolean added = false;
    private int lineBeforeOptimization = 0;
    
    public Line()
    {
        this.empty = true;
    }
    
    public Line(Line rhs)
    {
        this(rhs, rhs.getLineBeforeOptimization());
    }
    
    public Line(Line rhs, int lineBeforeOptimization)
    {
        this.removed = rhs.removed;
        this.added = rhs.added;
        this.empty = rhs.empty;
        this.line = rhs.line;
        this.lineBeforeOptimization = lineBeforeOptimization;
        
        for (IField field : rhs.fields)
        {
            // TODO 
            // maybe here should be deep copy of field
            field.frozeForOptimization();
            this.fields.add(field);
        }
        
        for (Label label : rhs.labels)
        {
            this.labels.add(new Label(label));
        }
    }
    
    public Line(IField... fields)
    {
        this.fields.addAll(Arrays.asList(fields));
        empty = this.fields.isEmpty();
    }
    
    public Line(List<IField> fields)
    {
        this.fields.addAll(fields);
        empty = this.fields.isEmpty();
    }
    
    private Line(IField field, boolean empty)
    {
        this.fields.add(field);
        this.empty = empty;
    }
    
    public static Line generateEmptyStringLine()
    {
        StringField sf = new StringField("");
        Line line = new Line(sf, true);
        return line;
    }
    
    public static Line generateEmptyByteLine()
    {
        ByteStringField bsf = new ByteStringField((byte)0, "");
        Line line = new Line(bsf, true);
        return line;
    }
    
    public static Line generateEmptyIntLine()
    {
        IntStringField isf = new IntStringField(0, "");
        Line line = new Line(isf, true);
        return line;
    }
    
    public void frozeForOptimization()
    {
        lineBeforeOptimization = line;
    }
    
    public void markAsAdded()
    {
        added = true;
    }

    public boolean isAdded()
    {
        return added;
    }
    
    public void markAsRemoved()
    {
        removed = true;
    }

    public boolean isRemoved()
    {
        return removed;
    }
    
    public void moveLineBeforeOptimization(int shift)
    {
        lineBeforeOptimization += shift;
    }
    
    public int getLineBeforeOptimization()
    {
        return lineBeforeOptimization;
    }

    public void setLineBeforeOptimization(int lineBeforeOptimization)
    {
        this.lineBeforeOptimization = lineBeforeOptimization;
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
    
    public void moveBeforeOptimization(int shift, int origLine)
    {
        for (IField field : fields)
        {
            field.moveBeforeOptimization(shift, origLine);
        }
    }

    @Override
    public String toString()
    {
        String result = "";
        
        int added = 0;
        for (IField f : fields)
        {
            if (!f.toString().equals(""))
            {
                result += f.toString() + Constants.fieldsSeparator;
                ++added;
            }
        }
        
        if (added > 0)
        {
            result = result.substring(0, result.length() - 1);
        }
        
        return result;
    }
    
    public String toStringOptimizationDiffrence()
    {
        String result = "";
        
        int added = 0;
        for (IField f : fields)
        {
            if (!f.toString().equals(""))
            {
                result += f.toStringOptimizationDiffrence() + Constants.fieldsSeparator;
                ++added;
            }
        }
        
        if (added > 0)
        {
            result = result.substring(0, result.length() - 1);
        }
        
        return result;
    }
    
    public boolean isEmptyLine()
    {
        return empty;
    }
    
    public void writeToBinaryFile(DataOutputStream dos) throws IOException
    {
        for (IField f : fields)
        {
            f.writeToBinaryFile(dos);
        }
    }
}
