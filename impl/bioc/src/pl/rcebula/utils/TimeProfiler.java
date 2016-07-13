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
import java.util.ArrayList;
import java.util.List;
import pl.rcebula.analysis.lexer.Pair;

/**
 * Pozwala na mierzenie rzeczywistego czasu jaki upłynął między wywołaniem metod start i stop
 * 
 * @author robert
 */
public class TimeProfiler
{
    private final List<Pair<String, Double>> times = new ArrayList<>();
    private double totalTime;
    
    private long startTime;
    private long startTotalTime;
    private String name;
    
    public void startTotal()
    {
        startTotalTime = System.nanoTime();
    }
    
    public void stopTotal()
    {
        totalTime = durationInMs(startTotalTime, System.nanoTime());
    }
    
    public void start(String name)
    {
        this.name = name;
        this.startTime = System.nanoTime();
    }
    
    public void stop()
    {
        double duration = durationInMs(startTime, System.nanoTime());
        
        times.add(new Pair<String, Double>(name, duration));
    }
    
    private double durationInMs(long start, long end)
    {
        long duration = (end - start);
        return ((double)(duration) / 1000000.0);
    }

    @Override
    public String toString()
    {
        String str = "";
        str += "total time: " + totalTime + " ms\n";
        
        for (Pair<String, Double> t : times)
        {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            double time = t.getRight();
            double percentTime = (time * 100.0 / totalTime);
            
            str += t.getLeft() + ": " + t.getRight().toString() + " ms (" + df.format(percentTime) + "%)\n";
        }
        
        return str;
    }
}
