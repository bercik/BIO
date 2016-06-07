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

/**
 *
 * @author robert
 */
public class Statistics
{
    private int redundantJumpsRemoved;
    private int linesBeforeOptimization;
    private int linesAfterOptimization;

    public Statistics()
    {
        redundantJumpsRemoved = 0;
        linesAfterOptimization = 0;
        linesBeforeOptimization = 0;
    }

    public int getRedundantJumpsRemoved()
    {
        return redundantJumpsRemoved;
    }

    public int getLinesBeforeOptimization()
    {
        return linesBeforeOptimization;
    }

    public int getLinesAfterOptimization()
    {
        return linesAfterOptimization;
    }
    
    public void addRedundantJumpRemoved()
    {
        ++redundantJumpsRemoved;
    }

    public void setLinesBeforeOptimization(int linesBeforeOptimization)
    {
        this.linesBeforeOptimization = linesBeforeOptimization;
    }

    public void setLinesAfterOptimization(int linesAfterOptimization)
    {
        this.linesAfterOptimization = linesAfterOptimization;
    }

    @Override
    public String toString()
    {
        String result = "";
        
        float percentOpt = ((float)linesBeforeOptimization - (float)linesAfterOptimization) * 100.0f
                / (float)linesBeforeOptimization;
            System.out.println("Removed lines of code: " + percentOpt + "%");
            System.out.println("Removed redundant jumps: " + redundantJumpsRemoved);
        
        return result;
    }
}
