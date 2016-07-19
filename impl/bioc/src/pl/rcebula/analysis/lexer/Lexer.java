package pl.rcebula.analysis.lexer;
import pl.rcebula.utils.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author robert
 */
public class Lexer
{
    // separatory po których następuje rozpoznanie tokenu
    public static final Character[] separators = new Character[]
    {
        '(', ')', ' ', '\t', '\n', '%', ','
    };
    // słowo kluczowe def
    public static final String defKeyword = "def";
    // słowo kluczowe end
    public static final String endKeyword = "end";
    // słowa kluczowe
    public static final String[] keywords = new String[]
    {
        Lexer.defKeyword, Lexer.endKeyword
    };
    // wartości nic
    public static final String[] nones = new String[]
    {
        "none", "None", "NONE"
    };
    // wartości true
    public static final String[] trues = new String[]
    {
        "true", "True", "TRUE"
    };
    // wartości false
    public static final String[] falses = new String[]
    {
        "false", "False", "FALSE"
    };

    private final List<Token<?>> tokens;

    public Lexer(String input)
            throws LexerError
    {
        Logger logger = Logger.getGlobal();
        logger.info("Lexer");
        tokens = addEOFAndAnalyse(input);
    }

    private final List<Token<?>> addEOFAndAnalyse(String input)
            throws LexerError
    {
        // dodajemy na końcu znacznik <EOF>
        input += "\n<EOF>";

        return analyse(input);
    }

    public List<Token<?>> getTokens()
    {
        return tokens;
    }

    private List<Token<?>> analyse(String input)
            throws LexerError
    {
        List<Token<?>> tokens = new ArrayList<>();
        FiniteStateAutomata fsa = new FiniteStateAutomata();
        boolean endWithEndToken = false;

        for (int i = 0; i < input.length(); ++i)
        {
            Pair<Token<?>, Boolean> pair = fsa.putChar(input.charAt(i));

            /// sprawdzamy czy nie zwrócono nam znaku
            if (pair.getRight() == true)
            {
                --i;
            }
            Token<?> token = pair.getLeft();
            if (token != null)
            {
                // dodajemy do listy tokenów
                tokens.add(token);
                
                // jeżeli token END to przerywamy
                if (token.getTokenType().equals(TokenType.END))
                {
                    endWithEndToken = true;
                    break;
                }
            }
        }

        if (!endWithEndToken)
        {
            throw new LexerError("Unexpected end of file. "
                    + "Possibly missed string closing quotation mark");
        }
        
        return tokens;
    }
}
