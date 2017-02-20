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
import pl.rcebula.Constants;

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
    private String[] passedArgs;

    public Opts(String[] args)
            throws OptsError
    {
//        Logger logger = Logger.getGlobal();
//        logger.info("Opts");

        this.args = args;

        analyse();
    }

    private void analyse()
            throws OptsError
    {
        if (args.length < 1)
        {
            String message = constructHelp();
            throw new OptsError(message);
        }

        // opcje
        int i = 0;
        boolean readInputFile = false;
        boolean runOption = false;
        while (i < args.length)
        {
            String opt = args[i];

            if (opt.startsWith("-"))
            {
                if (opt.equals("-d"))
                {
                    disassemble = true;
                    if (!runOption)
                    {
                        run = false;
                    }
                }
                else if (opt.equals("-t"))
                {
                    times = true;
                    if (!runOption)
                    {
                        run = false;
                    }
                }
                else if (opt.equals("-p"))
                {
                    profiler = true;
                    run = true;
                    runOption = true;
                }
                else if (opt.equals("-r"))
                {
                    run = true;
                    runOption = true;
                }
                else if (opt.equals("-h") || opt.equals("--help"))
                {
                    throw new OptsError(constructHelp());
                }
                else if (opt.equals("--version"))
                {
                    String message = "BIO interpreter version " + Constants.VERSION_STRING;
                    throw new OptsError(message);
                }
                else
                {
                    String message = "Unrecognized option " + opt + "\n";
                    message += constructHelp();
                    
                    throw new OptsError(message);
                }
            }
            else
            {
                readInputFile = true;
                inputFilePath = opt;
                ++i;
                int argsCount = args.length - i;
                passedArgs = new String[argsCount];

                int j = 0;
                for (; i < args.length; ++i)
                {
                    passedArgs[j++] = args[i];
                }
            }

            ++i;
        }

        if (!readInputFile)
        {
            throw new OptsError("You need to specify input file");
        }
    }

    private String constructHelp()
    {
        String message = "usage: bio [options] input_file [args]\n";
        message += "using any options causes code to not run except -p and -r\n";
        message += "options:\n";
        message += "  -d disassemble, print compiled code in readable form\n";
        message += "  -h --help, show this text\n";
        message += "  -p profiler, turns on profiler which measures time spent in each function\n";
        message += "  -r run, runs code instead of any option\n";
        message += "  -t times, print times spent on each module\n";
        message += "  --version, print current interpreter version\n";
        
        return message;
    }

    public String[] getPassedArgs()
    {
        return passedArgs;
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
