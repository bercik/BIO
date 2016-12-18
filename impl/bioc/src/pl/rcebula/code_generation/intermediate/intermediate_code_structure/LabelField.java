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
public class LabelField implements IField
{
    private Label label;
    
    // fields for diffrence betweend unoptimized and optimized code
    private boolean isFrozeForOptimization = false;
    private Label labelBeforeOptimization;

    public LabelField(Label label)
    {
        this.label = label;
    }

    public Label getLabel()
    {
        return label;
    }

    public void setLabel(Label label)
    {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return label.toString();
    }

    @Override
    public void writeToBinaryFile(DataOutputStream dos) throws IOException
    {
        dos.writeInt(label.getLine());
    }

    @Override
    public void frozeForOptimization()
    {
        if (!isFrozeForOptimization)
        {
            isFrozeForOptimization = true;
            labelBeforeOptimization = new Label(label);
        }
    }

    @Override
    public String toStringOptimizationDiffrence()
    {
        return labelBeforeOptimization.toString();
    }

    public Label getLabelBeforeOptimization()
    {
        return labelBeforeOptimization;
    }
    
    @Override
    public void moveBeforeOptimization(int shift, int origLine)
    {
        if (labelBeforeOptimization.getLine() >= origLine)
        {
            labelBeforeOptimization.move(shift);
        }
    }
}
