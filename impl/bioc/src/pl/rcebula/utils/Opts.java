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
public class Opts
{
    private final String[] args;
    private boolean verbose = false;
    private boolean statistics = false;
    private boolean notWrite = false;
    private String inputFilePath;
    private String outputFilePath;

    public Opts(String[] args)
            throws OptsError
    {
        this.args = args;
        
        analyse();
    }
    
    private void analyse()
            throws OptsError
    {
        if (args.length < 2)
        {
            String message = "usage: java -jar bioc.jar input_file [options] output_file";
            throw new OptsError(message);
        }
        
        // pierwszy argument to ścieżka do pliku wejściowego
        inputFilePath = args[0];
        // ostatni argument to ścieżka do pliku wyjściowego
        outputFilePath = args[args.length - 1];
        
        // opcje
        for (int i = 1; i < args.length - 1; ++i)
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
            else if (opt.equals("-n"))
            {
                notWrite = true;
            }
            else
            {
                String message = "Unrecognized option " + opt;
                throw new OptsError(message);
            }
        }
    }

    public boolean isNotWrite()
    {
        return notWrite;
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
}
