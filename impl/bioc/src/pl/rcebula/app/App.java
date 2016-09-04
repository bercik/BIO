package pl.rcebula.app;

import pl.rcebula.analysis.lexer.Lexer;
import pl.rcebula.analysis.lexer.LexerError;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.parser.Parser;
import pl.rcebula.analysis.parser.ParserError;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.ProgramTreeCreator;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import pl.rcebula.analysis.semantic.BuiltinFunction;
import pl.rcebula.analysis.semantic.BuiltinFunctionsParser;
import pl.rcebula.analysis.semantic.SemanticChecker;
import pl.rcebula.analysis.semantic.SemanticError;
import pl.rcebula.code_generation.final_steps.AddInformationsAboutFiles;
import pl.rcebula.code_generation.final_steps.AddInformationsAboutModules;
import pl.rcebula.code_generation.intermediate.CodeGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.optimization.CodeOptimizationError;
import pl.rcebula.code_generation.optimization.CodeOptimizer;
import pl.rcebula.preprocessor.Preprocessor;
import pl.rcebula.preprocessor.PreprocessorError;
import pl.rcebula.utils.Opts;
import pl.rcebula.utils.OptsError;
import pl.rcebula.code_generation.optimization.OptimizationStatistics;
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
            throws Exception
    {
        try
        {
            // init logger
            initLogger();

            // time profiler
            TimeProfiler timeProfiler = new TimeProfiler();
            timeProfiler.startTotal();

            // statistics tool
            OptimizationStatistics statistic = new OptimizationStatistics();

            // opts
            timeProfiler.start("Opts");
            Opts opts = new Opts(args);
            timeProfiler.stop();

            // preprocessor
            timeProfiler.start("Preprocessor");
            Preprocessor preprocessor = new Preprocessor(opts.getInputFilePath(), opts.isDebugInfo());
            timeProfiler.stop();
            if (opts.isVerbose())
            {
                // print
                System.out.println("SOURCE CODE");
                System.out.println("-------------------------");
                System.out.println(preprocessor.getInput());
            }

            // lexer
            timeProfiler.start("Lexer");
            Lexer lexer = new Lexer(preprocessor.getInput(), preprocessor.getFiles());
            timeProfiler.stop();
            List<Token<?>> tokens = lexer.getTokens();
            if (opts.isVerbose())
            {
                // print
                System.out.println("TOKENS");
                System.out.println("-------------------------");
                printTokens(tokens);
            }

            // parser
            timeProfiler.start("Parser");
            Parser parser = new Parser(tokens);
            timeProfiler.stop();
            List<Integer> steps = parser.getSteps();
            if (opts.isVerbose())
            {
                // print
                System.out.println("PARSE STEPS");
                System.out.println("-------------------------");
                printSteps(steps);
            }

            // program tree creator
            timeProfiler.start("ProgramTreeCreator");
            ProgramTreeCreator ptc = new ProgramTreeCreator(tokens, steps);
            timeProfiler.stop();
            ProgramTree pt = ptc.getProgramTree();
            if (opts.isVerbose())
            {
                // print
                System.out.println("PARSE TREE");
                System.out.println("-------------------------");
                System.out.println(pt);
            }

            // builtin functions parser
            timeProfiler.start("BuiltinFunctionsParser");
            BuiltinFunctionsParser bfp = new BuiltinFunctionsParser(preprocessor.getModules());
            timeProfiler.stop();
            List<BuiltinFunction> builtinFunctions = bfp.getBuiltinFunctions();

            // semantic checker
            timeProfiler.start("SemanticChecker");
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, preprocessor.getFiles());
            timeProfiler.stop();

            // intermediate code generator
            timeProfiler.start("CodeGenerator");
            CodeGenerator cg = new CodeGenerator(pt, builtinFunctions, preprocessor.getFiles());
            timeProfiler.stop();
            IntermediateCode ic = cg.getIc();
            statistic.setLinesBeforeOptimization(ic.numberOfLines());

            if (opts.isVerbose())
            {
                // print
                System.out.println("");
                System.out.println("INTERMEDIATE CODE");
                System.out.println("-------------------------");
                System.out.println(ic.toStringWithLinesNumber());
            }

            // optimizations
            timeProfiler.start("CodeOptimizer");
            new CodeOptimizer(ic, statistic, preprocessor.getFiles());
            timeProfiler.stop();
            statistic.setLinesAfterOptimization(ic.numberOfLines());
            
            if (opts.isVerbose())
            {
                System.out.println("");
                System.out.println("AFTER OPTIMIZATIONS");
                System.out.println("-------------------------");
                System.out.println(ic.toStringWithLinesNumber());
            }

            // add informations about files to intermediate code
            timeProfiler.start("AddInformationsAboutFiles");
            new AddInformationsAboutFiles(ic, preprocessor.getFiles());
            timeProfiler.stop();
            
            // add informations about modules to intermediate code
            timeProfiler.start("AddInformationsAboutModules");
            new AddInformationsAboutModules(ic, bfp.getModulesName());
            timeProfiler.stop();

            if (opts.isStatistics() || opts.isVerbose())
            {
                System.out.println("");
                System.out.println("OPTIMIZATION STATISTICS");
                System.out.println("-------------------------");
                System.out.println(statistic);
            }

            if (opts.isDisassemble() && !opts.isVerbose())
            {
                System.out.println("DISASSEMBLE CODE");
                System.out.println("-------------------------");
                System.out.println(ic.toStringWithLinesNumber());
            }

            timeProfiler.start("WriteToBinaryFile");
            ic.writeToBinaryFile(opts.getOutputFilePath());
            timeProfiler.stop();

            timeProfiler.stopTotal();
            if (opts.isTimes() || opts.isVerbose())
            {
                System.out.println("TIMES");
                System.out.println("-------------------------");
                System.out.println(timeProfiler.toString());
            }
        }
        catch (OptsError ex)
        {
            System.err.println("Options error: " + ex.getMessage());
        }
        catch (PreprocessorError ex)
        {
            System.err.println("Preprocessor error: " + ex.getMessage());
        }
        catch (LexerError ex)
        {
            System.err.println("Lexer error: " + ex.getMessage());
        }
        catch (ParserError ex)
        {
            System.err.println("Parser error: " + ex.getMessage());
        }
        catch (SemanticError ex)
        {
            System.err.println("Semantic error: " + ex.getMessage());
        }
        catch (CodeOptimizationError ex)
        {
            System.err.println("Code optimization error: " + ex.getMessage());
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
        
        FileHandler fh = new FileHandler(logsDir + "/bioc.txt");
        logger.addHandler(fh);
        SimpleFormatter sf = new SimpleFormatter();
        fh.setFormatter(sf);

        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);

        logger.info("Init logger");
    }
    
    private static void printTokens(List<Token<?>> tokens)
    {
        for (Token<?> t : tokens)
        {
            System.out.println(t);
        }
    }
    
    private static void printSteps(List<Integer> steps)
    {
        for (Integer step : steps)
        {
            System.out.println(step);
        }
    }
}
