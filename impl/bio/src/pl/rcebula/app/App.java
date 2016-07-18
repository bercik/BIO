/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.app;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import pl.rcebula.intermediate_code.IntermediateCode;
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
            // init logger
            initLogger();

            // time profiler
            TimeProfiler timeProfiler = new TimeProfiler();
            timeProfiler.startTotal();
            
            if (args.length < 1)
            {
                System.out.println("Usage: java -jar bio.jar input_file");
                return;
            }
            String path = args[0];
            
            // read intermediate code
            IntermediateCode ic = new IntermediateCode(path);
            
            System.out.println("INTERMEDIATE CODE");
            System.out.println("-------------------------");
            System.out.println(ic.toStringWithLineNumbers());
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
        FileHandler fh = new FileHandler(decodedPath + "/log.txt");
        logger.addHandler(fh);
        SimpleFormatter sf = new SimpleFormatter();
        fh.setFormatter(sf);

        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINE);

        logger.info("Init logger");
    }
}
