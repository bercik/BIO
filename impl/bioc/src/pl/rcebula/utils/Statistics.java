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

import java.text.DecimalFormat;

/**
 *
 * @author robert
 */
public class Statistics
{
    private int redundantJumpsRemoved;
    private int pushSequencesRemoved;
    private int pushBoolJmpSequencesRemoved;
    private int unusedCodeBlocksLinesRemoved;
            
    private int linesBeforeOptimization;
    private int linesAfterOptimization;

    public Statistics()
    {
        redundantJumpsRemoved = 0;
        pushBoolJmpSequencesRemoved = 0;
        pushSequencesRemoved = 0;
        unusedCodeBlocksLinesRemoved = 0;
        
        linesAfterOptimization = 0;
        linesBeforeOptimization = 0;
    }

    public int getPushSequencesRemoved()
    {
        return pushSequencesRemoved;
    }

    public int getPushBoolJmpSequencesRemoved()
    {
        return pushBoolJmpSequencesRemoved;
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

    public int getUnusedCodeBlocksLinesRemoved()
    {
        return unusedCodeBlocksLinesRemoved;
    }
    
    public void addRedundantJumpRemoved()
    {
        ++redundantJumpsRemoved;
    }
    
    public void addPushSequenceRemoved()
    {
        ++pushSequencesRemoved;
    }
    
    public void addPushBoolSequenceRemoved()
    {
        ++pushBoolJmpSequencesRemoved;
    }
    
    public void addUnusedCodeBlocksLinesRemoved()
    {
        ++unusedCodeBlocksLinesRemoved;
    }
    
    public void removePushBoolSequenceRemoved()
    {
        --pushBoolJmpSequencesRemoved;
    }

    public void setLinesBeforeOptimization(int linesBeforeOptimization)
    {
        this.linesBeforeOptimization = linesBeforeOptimization;
    }

    public void setLinesAfterOptimization(int linesAfterOptimization)
    {
        this.linesAfterOptimization = linesAfterOptimization;
    }

    private float relativeDiffrence(float a, float b)
    {
        return (a - b) * 100.0f / a;
    }
    
    @Override
    public String toString()
    {
        String result = "";
        
        float lbo = (float)linesBeforeOptimization;
        float lao = (float)linesAfterOptimization;
        
        float percentOpt = relativeDiffrence(lbo, lao);
        float pushSequencesOpt = relativeDiffrence(lbo, lbo - (float)pushSequencesRemoved);
        float pushBoolJmpSequencesOpt = relativeDiffrence(lbo, lbo - (float)pushBoolJmpSequencesRemoved);
        float unusedCodeBlocksLinesRemovedOpt = relativeDiffrence(lbo, lbo - (float)unusedCodeBlocksLinesRemoved);
        
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        
        System.out.println("Removed lines of code: " + df.format(percentOpt) + "% (" 
                + (linesBeforeOptimization - linesAfterOptimization) + ")");
        System.out.println("  Removed push sequences: " + df.format(pushSequencesOpt) + "% (" 
                + pushSequencesRemoved + ")");
        System.out.println("  Removed push bool jmp sequences: " + df.format(pushBoolJmpSequencesOpt) + 
                "% (" + pushBoolJmpSequencesRemoved + ")");
        System.out.println("  Removed unused code blocks lines: " + 
                df.format(unusedCodeBlocksLinesRemovedOpt) + "% (" + unusedCodeBlocksLinesRemoved + ")");
        System.out.println("Removed redundant jumps: " + redundantJumpsRemoved);
        
        return result;
    }
}
