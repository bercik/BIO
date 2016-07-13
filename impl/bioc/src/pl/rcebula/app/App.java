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
import pl.rcebula.code_generation.intermediate.IntermediateCode;
import pl.rcebula.code_generation.optimization.CodeOptimizationError;
import pl.rcebula.code_generation.optimization.CodeOptimizer;
import pl.rcebula.preprocessor.Preprocessor;
import pl.rcebula.preprocessor.PreprocessorError;
import pl.rcebula.utils.Opts;
import pl.rcebula.utils.OptsError;
import pl.rcebula.utils.Statistics;

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
            // statistics tool
            Statistics statistic = new Statistics();

            // opts
            Opts opts = new Opts(args);

            // preprocessor
            Preprocessor preprocessor = new Preprocessor(opts.getInputFilePath());
            if (opts.isVerbose())
            {
                // print
                System.out.println("SOURCE CODE");
                System.out.println("-------------------------");
                System.out.println(preprocessor.getInput());
            }
            
            // lexer
            Lexer lexer = new Lexer(preprocessor.getInput());
            List<Token<?>> tokens = lexer.getTokens();

            // parser
            Parser parser = new Parser(tokens);
            List<Integer> steps = parser.getSteps();

            // program tree creator
            ProgramTreeCreator ptc = new ProgramTreeCreator(tokens, steps);
            ProgramTree pt = ptc.getProgramTree();
            if (opts.isVerbose())
            {
                // print
                System.out.println("PARSE TREE");
                System.out.println("-------------------------");
                System.out.println(pt);
            }

            // builtin functions parser
            BuiltinFunctionsParser bfp = new BuiltinFunctionsParser("/pl/rcebula/res/builtin_functions.xml", true);
            List<BuiltinFunction> builtinFunctions = bfp.getBuiltinFunctions();

            // semantic checker
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions);

            // intermediate code generator
            CodeGenerator cg = new CodeGenerator(pt, builtinFunctions);
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
            CodeOptimizer co = new CodeOptimizer(ic, statistic);
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
                ic.writeToBinaryFile(opts.getOutputFilePath());
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
