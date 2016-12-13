/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author robert
 */
public class Profiler implements IProfiler
{
    private final Stack<String> stack = new Stack<>();
    private final HashMap<String, Entry> times = new HashMap<>();

    long start;

    private double durationInMs(long start, long end)
    {
        long duration = (end - start);
        return ((double)(duration) / 1000000.0);
    }

    @Override
    public void enter(String name)
    {
        // jeżeli nie ma takiej wartości w mapie to dodajemy
        Entry e = times.get(name);
        if (e == null)
        {
            e = new Entry();
            times.put(name, e);
        }
        
        // dodajemy wejście
        e.addEntry();
        
        // jeżeli pierwsza wartość na stosie
        if (stack.size() == 0)
        {
            stack.push(name);
            start = System.nanoTime();
        }
        else
        {
            doLogic(true, name);
        }
    }

    @Override
    public void exit()
    {
        doLogic(false, "");
    }

    private void doLogic(boolean push, String pushValue)
    {
        long end = System.nanoTime();
        double duration = durationInMs(start, end);

        // pobierz wpis z wierzchołka stosu
        String topsName = stack.peek();
        Entry tops = times.get(topsName);
        // dodaj do czasu rzeczywistego
        tops.addRealTime(duration);
        // pobierz wszystkie elementy ze stosu i dodaj do ich czasu ogólnego
        Iterator<String> it = stack.iterator();
        while (it.hasNext())
        {
            String str = it.next();
            Entry e = times.get(str);
            e.addGeneralTime(duration);
        }

        // jeżeli push to wrzuć wartość na stos
        if (push)
        {
            stack.push(pushValue);
        }
        // inaczej pop
        else
        {
            stack.pop();
        }

        start = System.nanoTime();
    }

    @Override
    public String toString()
    {
        // stwórz listę
        List<FullEntry> fes = new ArrayList<>();

        // wypełnij
        for (Map.Entry<String, Entry> e : times.entrySet())
        {
            fes.add(new FullEntry(e.getKey(), e.getValue().getRealTime(), e.getValue().getGeneralTime(),
                    e.getValue().getNumberOfEntrys()));
        }

        // posortuj
        Collections.sort(fes);

        String str = String.format("%-20.20s  %-20.20s %-20.20s %-25.25s %-25.25s%n", 
                "Function name", "General time", "Real time", "Avg general time", "Avg real time");
        // wyświetl
        for (FullEntry fe : fes)
        {
            str += fe.toString() + "\n";
        }

        return str;
    }

    private class FullEntry implements Comparable<FullEntry>
    {
        private final String name;
        private final Double realTime;
        private final Double generalTime;
        private final Integer numberOfEntrys;

        public FullEntry(String name, Double realTime, Double generalTime, Integer numberOfEntrys)
        {
            this.name = name;
            this.realTime = realTime;
            this.generalTime = generalTime;
            this.numberOfEntrys = numberOfEntrys;
        }

        public Integer getNumberOfEntrys()
        {
            return numberOfEntrys;
        }

        public String getName()
        {
            return name;
        }

        public Double getRealTime()
        {
            return realTime;
        }

        public Double getGeneralTime()
        {
            return generalTime;
        }

        @Override
        public String toString()
        {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(8);
            df.setMinimumFractionDigits(8);

            String col1 = name;
            String col2 = df.format(generalTime) + " ms";
            String col3 = df.format(realTime) + " ms";
            
            double avgRealTime = getAvg(realTime, numberOfEntrys);
            String col5 = df.format(avgRealTime) + " ms (" + numberOfEntrys + ")";
            double avgGeneralTime = getAvg(generalTime, numberOfEntrys);
            String col4 = df.format(avgGeneralTime) + " ms (" + numberOfEntrys + ")";
            
            return String.format("%-20.20s  %-20.20s %-20.20s %-25.25s %-25.25s", col1, col2, col3, col4, col5);
        }
        
        public Double getAvg(double time, int numbers)
        {
            return time / (double)numbers;
        }

        @Override
        public int compareTo(FullEntry o)
        {  
            double avgRealTime1 = getAvg(realTime, numberOfEntrys);
            double avgRealTime2 = getAvg(o.realTime, o.numberOfEntrys);
            
            if (avgRealTime1 < avgRealTime2)
            {
                return 1;
            }
            else if (avgRealTime1 > avgRealTime2)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }

    private class Entry
    {
        // prawdziwy czas spędzony w funkcji, czyli ten kiedy była ona na wierzchołku stosu
        private double realTime = 0.0;
        // ogólny czas spędzony w funkcji czyli ten kiedy była ona na stosie w ogóle
        private double generalTime = 0.0;
        // ilość wejść
        int numberOfEntrys = 0;

        public Double getRealTime()
        {
            return realTime;
        }

        public Double getGeneralTime()
        {
            return generalTime;
        }

        public int getNumberOfEntrys()
        {
            return numberOfEntrys;
        }

        public void addEntry()
        {
            ++numberOfEntrys;
        }

        public void addRealTime(double time)
        {
            realTime += time;
        }

        public void addGeneralTime(double time)
        {
            generalTime += time;
        }
    }
}
