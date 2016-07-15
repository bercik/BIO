package pl.rcebula.app;

import pl.rcebula.analysis.lexer.Lexer;
import pl.rcebula.analysis.lexer.LexerError;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.parser.Parser;
import pl.rcebula.analysis.parser.ParserError;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.ProgramTreeCreator;
import java.io.IOException;
import java.util.List;
import pl.rcebula.analysis.semantic.BuiltinFunction;
import pl.rcebula.analysis.semantic.BuiltinFunctionsParser;
import pl.rcebula.analysis.semantic.SemanticChecker;
import pl.rcebula.analysis.semantic.SemanticError;
import pl.rcebula.code_generation.intermediate.CodeGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.optimization.CodeOptimizationError;
import pl.rcebula.code_generation.optimization.CodeOptimizer;
import pl.rcebula.preprocessor.Preprocessor;
import pl.rcebula.preprocessor.PreprocessorError;
import pl.rcebula.utils.Opts;
import pl.rcebula.utils.OptsError;
import pl.rcebula.utils.OptimizationStatistics;
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
            Preprocessor preprocessor = new Preprocessor(opts.getInputFilePath());
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
            Lexer lexer = new Lexer(preprocessor.getInput());
            timeProfiler.stop();
            List<Token<?>> tokens = lexer.getTokens();

            // parser
            timeProfiler.start("Parser");
            Parser parser = new Parser(tokens);
            timeProfiler.stop();
            List<Integer> steps = parser.getSteps();

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
            BuiltinFunctionsParser bfp = new BuiltinFunctionsParser(true, 
                    "/pl/rcebula/res/arrays.xml",
                    "/pl/rcebula/res/basic.xml",
                    "/pl/rcebula/res/compare.xml",
                    "/pl/rcebula/res/conversion.xml",
                    "/pl/rcebula/res/ints.xml",
                    "/pl/rcebula/res/io.xml",
                    "/pl/rcebula/res/logic.xml",
                    "/pl/rcebula/res/math.xml",
                    "/pl/rcebula/res/observer.xml",
                    "/pl/rcebula/res/special.xml",
                    "/pl/rcebula/res/strings.xml");
            timeProfiler.stop();
            List<BuiltinFunction> builtinFunctions = bfp.getBuiltinFunctions();

            // semantic checker
            timeProfiler.start("SemanticChecker");
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions);
            timeProfiler.stop();

            // intermediate code generator
            timeProfiler.start("CodeGenerator");
            CodeGenerator cg = new CodeGenerator(pt, builtinFunctions);
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
            CodeOptimizer co = new CodeOptimizer(ic, statistic);
            timeProfiler.stop();
            statistic.setLinesAfterOptimization(ic.numberOfLines());

            if (opts.isVerbose())
            {
                System.out.println("");
                System.out.println("AFTER OPTIMIZATIONS");
                System.out.println("-------------------------");
                System.out.println(ic.toStringWithLinesNumber());
            }

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
            
            if (!opts.isNotWrite())
            {
                timeProfiler.start("WriteToBinaryFile");
                ic.writeToBinaryFile(opts.getOutputFilePath());
                timeProfiler.stop();
            }
            
            timeProfiler.stopTotal();
            if (opts.isTimes())
            {
                System.out.println("TIMES");
                System.out.println("-------------------------");
                System.out.println(timeProfiler.toString());
            }
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
        catch (OptsError ex)
        {
            System.err.println("Options error: " + ex.getMessage());
        }
        catch (PreprocessorError ex)
        {
            System.err.println("Preprocessor error: " + ex.getMessage());
        }
        catch (IOException ex)
        {
            System.err.println("IOException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
