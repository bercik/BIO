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
import java.util.HashMap;
import java.util.Map;

/**
 * Pozwala na mierzenie rzeczywistego czasu jaki upłynął między wywołaniem metod start i stop
 * można wielokrotnie wywoływać metody start i stop dla tej samej metody, czasy zostaną dodane
 * 
 * @author robert
 */
public class TimeProfiler
{
    private final boolean works;
    private final boolean averages;
    
    private final Map<String, Pair<Double, Integer>> times = new HashMap<>();
    private double totalTime;
    
    private boolean started = false;
    private long startTime;
    private long startTotalTime;
    private String name;

    public TimeProfiler()
    {
        this.works = true;
        this.averages = false;
    }

    public TimeProfiler(boolean works, boolean averages)
    {
        this.works = works;
        this.averages = averages;
    }
    
    public void startTotal()
    {
        if (works)
        {
            startTotalTime = System.nanoTime();
        }
    }
    
    public void stopTotal()
    {
        if (works)
        {
            totalTime = durationInMs(startTotalTime, System.nanoTime());
        }
    }
    
    public void start(String name)
    {
        if (works)
        {
            if (started)
            {
                throw new RuntimeException("You didn't stop after last start method call");
            }

            this.name = name;
            this.startTime = System.nanoTime();
            this.started = true;
        }
    }
    
    public void stop()
    {
        if (works)
        {
            if (!started)
            {
                throw new RuntimeException("You didn't start before you call stop method");
            }

            double duration = durationInMs(startTime, System.nanoTime());

            // jeżeli zawiera już taką nazwę to dodajemy wynik
            if (times.containsKey(name))
            {
                Pair<Double, Integer> p = times.get(name);
                
                Double time = p.getLeft();
                Integer count = p.getRight();
                
                Integer newCount = count + 1;
                Double newTime = time + duration;
                // zmieniamy wartości na nowe
                p.setLeft(newTime);
                p.setRight(newCount);
            }
            // inaczej dodajemy nowy wpis
            else
            {
                times.put(name, new Pair<>(duration, 1));
            }
            this.started = false;
        }
    }
    
    private double durationInMs(long start, long end)
    {
        long duration = (end - start);
        return ((double)(duration) / 1000000.0);
    }

    @Override
    public String toString()
    {
        if (works)
        {
            String str = "";
            str += "total time: " + totalTime + " ms\n";

            for (Map.Entry<String, Pair<Double, Integer>> e : times.entrySet())
            {
                DecimalFormat df2 = new DecimalFormat();
                df2.setMaximumFractionDigits(2);
                
                DecimalFormat df8 = new DecimalFormat();
                df8.setMaximumFractionDigits(8);
                
                Pair<Double, Integer> p = e.getValue();
                double time = p.getLeft();
                int count = p.getRight();
                String name = e.getKey();
                double percentTime = (time * 100.0 / totalTime);
                
                String col1 = name + ": " + df8.format(time) + " ms (" + df2.format(percentTime) + "%)";
                if (averages)
                {
                    double avgTime = (time / (double)count);
                    String col2 = " { avg: " + df8.format(avgTime) + " ms (" + count + ") }";
                    
                    str += String.format("%-50.50s  %-40.40s%n", col1, col2);
                }
                else
                {
                    str += col1 + "\n";
                }
            }

            return str;
        }
        else
        {
            return "TimeProfiler didn't work (you need to change this behavior when calling constructor)";
        }
    }
}
