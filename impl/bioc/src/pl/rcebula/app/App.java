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
        if (args.length < 1)
        {
            System.out.println("Usage: java -jar app.jar path_to_script");
            return;
        }
        // TODO code application logic here
        try
        {
            // lexer
            Lexer lexer = new Lexer(args[0], false);
            List<Token<?>> tokens = lexer.getTokens();
            
            // parser
            Parser parser = new Parser(tokens);
            List<Integer> steps = parser.getSteps();
            
            // program tree creator
            ProgramTreeCreator ptc = new ProgramTreeCreator(tokens, steps);
            ProgramTree pt = ptc.getProgramTree();
            // print
            System.out.println(pt);
            
            // builtin functions parser
            BuiltinFunctionsParser bfp = new BuiltinFunctionsParser("/pl/rcebula/res/builtin_functions.xml", true);
            List<BuiltinFunction> builtinFunctions = bfp.getBuiltinFunctions();
            
            // semantic checker
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions);
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
        catch (IOException ex)
        {
            System.err.println("IOException: " + ex.getMessage());
        }
    }
}
