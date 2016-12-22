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
    private boolean verbose = false;
    private boolean statistics = false;
    private boolean disassemble = false;
    private boolean times = false;
    private boolean debugInfo = false;
    private boolean optimizations = true;
    private boolean optimizationsDiffrence = false;
    private String inputFilePath;
    private String outputFilePath;
    private static final String defaultOutputFile = "a.cbio";

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
        if (args.length < 1 || args[0].equals("-h") || args[0].equals("--help"))
        {
            throw new OptsError(constructHelp());
        }

        if (args[0].equals("--version"))
        {
            String message = constructVersion();
            throw new OptsError(message);
        }

        // pierwszy argument to ścieżka do pliku wejściowego
        inputFilePath = args[0];

        // opcje
        for (int i = 1; i < args.length; ++i)
        {
            String opt = args[i];

            if (opt.equals("-v"))
            {
                verbose = true;
            }
            else if (opt.equals("-s"))
            {
                statistics = true;
            }
            else if (opt.equals("-o"))
            {
                if (++i < args.length)
                {
                    outputFilePath = args[i];
                }
                else
                {
                    String message = "You must specify <file> for -o option";
                    throw new OptsError(message);
                }
            }
            else if (opt.equals("-d"))
            {
                disassemble = true;
            }
            else if (opt.equals("-t"))
            {
                times = true;
            }
            else if (opt.equals("-g"))
            {
                debugInfo = true;
            }
            else if (opt.equals("-c"))
            {
                optimizations = false;
            }
            else if (opt.equals("-h") || opt.equals("--help"))
            {
                throw new OptsError(constructHelp());
            }
            else if (opt.equals("--version"))
            {
                String message = constructVersion();
                throw new OptsError(message);
            }
            else if (opt.equals("-dd"))
            {
                optimizationsDiffrence = true;
            }
            else
            {
                String message = "Unrecognized option " + opt + "\n";
                message += constructHelp();
                throw new OptsError(message);
            }
        }

        // jeżeli nie podano pliku wyjściowego to przyjmujemy domyślny
        if (outputFilePath == null)
        {
            outputFilePath = defaultOutputFile;
        }
    }

    private String constructVersion()
    {
        return "BIO compiler version " + Constants.VERSION_STRING;
    }

    private String constructHelp()
    {
        String help = "usage: bioc input_file [options]\n";
        help += "options:\n";
        help += "  -c disable optimizations, turns off compiler code optimizations\n";
        help += "  -d disassemble, print compiled code in readable form\n";
        help += "  -dd optimization diffrence, show disassembled code with diffrences between "
                + "unoptimized and optimized version\n";
        help += "  -g debug info, add full files name to compiled file\n";
        help += "  -h --help, show this text\n";
        help += "  -o <file> place the output into <file>, if won't given compiled code will be saved in "
                + "default file (" + defaultOutputFile + ")\n";
        help += "  -s statistics, print optimization statistics\n";
        help += "  -t times, print times spent in each module\n";
        help += "  -v verbose, print all informations about compiling process\n";

        return help;
    }

    public boolean isDebugInfo()
    {
        return debugInfo;
    }

    public boolean isTimes()
    {
        return times;
    }

    public boolean isDisassemble()
    {
        return disassemble;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public boolean isStatistics()
    {
        return statistics;
    }

    public String getInputFilePath()
    {
        return inputFilePath;
    }

    public String getOutputFilePath()
    {
        return outputFilePath;
    }

    public boolean isOptimizations()
    {
        return optimizations;
    }

    public boolean isOptimizationsDiffrence()
    {
        return optimizationsDiffrence;
    }
}
