/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.app;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import pl.rcebula.intermediate_code.IntermediateCode;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.BuiltinFunctions;
import pl.rcebula.tools.IProfiler;
import pl.rcebula.tools.NullProfiler;
import pl.rcebula.tools.Profiler;
import pl.rcebula.utils.Opts;
import pl.rcebula.utils.OptsError;
import pl.rcebula.utils.TimeProfiler;

/**
 *
 * @author robert
 */
public class App
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            // TODELETE
            // init logger
            initLogger();

            // Opts
            Opts opts = new Opts(args);
            
            // time profiler
            // włączamy jedynie gdy przekazano odpowiednią opcję
            TimeProfiler timeProfiler = new TimeProfiler(opts.isTimes(), opts.isTimes());
            timeProfiler.startTotal();

            // read intermediate code
            timeProfiler.start("ReadIntermediateCode");
            IntermediateCode ic = new IntermediateCode(opts.getInputFilePath());
            timeProfiler.stop();

            if (opts.isDisassemble())
            {
                System.out.println("DISASSEMBLE CODE");
                System.out.println("-------------------------");
                System.out.println(ic.toStringWithLineNumbers());
            }

            if (opts.isRun())
            {
                IProfiler profiler = new NullProfiler();
                if (opts.isProfiler())
                {
                    profiler = new Profiler();
                }

                // builtin functions
                timeProfiler.start("BuiltinFunctions");
                BuiltinFunctions builtinFunctions = new BuiltinFunctions(ic.getModulesName(), ic.getUserFunctions(),
                        ic.getFiles());
                timeProfiler.stop();
                // jeżeli jakaś opcja wyświetlająca tekst to dodaj linię oddzielającą
                // wyjście programu
                if (opts.isDisassemble() || opts.isProfiler() || opts.isTimes())
                {
                    System.out.println("OUTPUT");
                    System.out.println("-------------------------");
                }
                // uruchom interpreter
                Interpreter interpreter = new Interpreter(opts.getInputFilePath(), opts.getPassedArgs(), 
                        ic.getUserFunctions(), builtinFunctions, timeProfiler, profiler, ic.getFiles());

                // pokaż moduł z czasami
                timeProfiler.stopTotal();
                if (opts.isTimes())
                {
                    if (opts.isRun())
                    {
                        System.out.println();
                    }
                    System.out.println("TIMES");
                    System.out.println("-------------------------");
                    System.out.println(timeProfiler.toString());
                }

                // pokaż statystyki profilera
                if (opts.isProfiler())
                {
                    if (opts.isRun())
                    {
                        System.out.println();
                    }
                    System.out.println("PROFILER");
                    System.out.println("-------------------------");
                    System.out.println(profiler.toString());
                }
            }
            else
            {
                // pokaż moduł z czasami
                timeProfiler.stopTotal();
                if (opts.isTimes())
                {
                    System.out.println("TIMES");
                    System.out.println("-------------------------");
                    System.out.println(timeProfiler.toString());
                }
            }
        }
        catch (OptsError ex)
        {
            System.err.println(ex.getMessage());
        }
        catch (IOException ex)
        {
            System.err.println("IOException: " + ex.getMessage());
        }
    }

    private static void initLogger() throws IOException
    {
        Logger logger = Logger.getGlobal();

        String path = App.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("/"));

        // na windowsie musimy usunąć pierwszy znak którym jest /
        decodedPath = System.getProperty("os.name").contains("indow")
                ? decodedPath.substring(1) : decodedPath;

        // utwórz katalog /logs jeżeli nie istnieje
        String logsDir = decodedPath + "/logs";
        Path logsPath = Paths.get(logsDir);
        if (!Files.exists(logsPath))
        {
            Files.createDirectory(logsPath);
        }
        
        FileHandler fh = new FileHandler(logsDir + "/bio.txt");
        logger.addHandler(fh);
        SimpleFormatter sf = new SimpleFormatter();
        fh.setFormatter(sf);

        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);

        logger.info("Init logger");
    }
}
