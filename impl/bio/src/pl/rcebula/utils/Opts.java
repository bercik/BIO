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

import java.util.logging.Logger;

/**
 *
 * @author robert
 */
public class Opts
{
    private final String[] args;
    private boolean disassemble = false;
    private boolean times = false;
    private boolean run = true;
    private boolean profiler = false;
    private String inputFilePath;

    public Opts(String[] args)
            throws OptsError
    {
        Logger logger = Logger.getGlobal();
        logger.info("Opts");
        
        this.args = args;
        
        analyse();
    }
    
    private void analyse()
            throws OptsError
    {
        if (args.length < 1 || args[0].equals("--help") || args[0].equals("-h"))
        {
            String message = "usage: java -jar bio.jar input_file [options]\n";
            message += "using any options causes code to not run except -p and -r\n";
            message += "options:\n";
            message += "  -d disassemble, print compiled code in readable form\n";
            message += "  -h --help, show this text\n";
            message += "  -p profiler, turns on profiler which measures time spent in each function\n";
            message += "  -r run, runs code instead of any option\n";
            message += "  -t times, print times spent on each module\n";
            throw new OptsError(message);
        }
        
        // pierwszy argument to ścieżka do pliku wejściowego
        inputFilePath = args[0];
        
        // opcje
        for (int i = 1; i < args.length; ++i)
        {
            String opt = args[i];
            
            if (opt.equals("-d"))
            {
                disassemble = true;
                run = false;
            }
            else if (opt.equals("-t"))
            {
                times = true;
                run = false;
            }
            else if (opt.equals("-p"))
            {
                profiler = true;
                run = true;
            }
            else if (opt.equals("-r"))
            {
                run = true;
            }
            else
            {
                String message = "Unrecognized option " + opt;
                throw new OptsError(message);
            }
        }
    }

    public boolean isTimes()
    {
        return times;
    }
    
    public boolean isDisassemble()
    {
        return disassemble;
    }

    public boolean isProfiler()
    {
        return profiler;
    }

    public boolean isRun()
    {
        return run;
    }

    public String getInputFilePath()
    {
        return inputFilePath;
    }
}
