package app;

import analysis.lexer.Lexer;
import analysis.lexer.LexerError;
import analysis.lexer.Token;
import analysis.parser.Parser;
import analysis.parser.ParserError;
import java.io.IOException;
import java.util.List;

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
            Lexer lexer = new Lexer(args[0], false);
            List<Token<?>> tokens = lexer.getTokens();
            Parser parser = new Parser(tokens);
        }
        catch (LexerError ex)
        {
            System.err.println("Lexer error: " + ex.getMessage());
        }
        catch (ParserError ex)
        {
            System.err.println("Parser error: " + ex.getMessage());
        }
        catch (IOException ex)
        {
            System.err.println("IOException: " + ex.getMessage());
        }
    }

}
