package pl.rcebula.analysis.lexer;
import pl.rcebula.utils.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class Lexer
{
    // separatory po których następuje rozpoznanie tokenu
    public static final Character[] separators = new Character[]
    {
        '(', ')', ' ', '\t', '\n', '@', ',', '='
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

    public static final String eofMarker = "<EOF>";
    
    private final List<Token<?>> tokens;
    private final MyFiles files;

    public Lexer(String input, MyFiles files)
            throws LexerError
    {
//        Logger logger = Logger.getGlobal();
//        logger.info("Lexer");
        
        this.files = files;
        
        tokens = analyse(input);
    }

    public List<Token<?>> getTokens()
    {
        return tokens;
    }

    private List<Token<?>> analyse(String input)
            throws LexerError
    {
        List<Token<?>> tokens = new ArrayList<>();
        FiniteStateAutomata fsa = new FiniteStateAutomata(files);
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
            ErrorInfo ei = fsa.generateErrorInfoWithCurrentToken();
            String s = fsa.getTokenValue().substring(0, 1);
            throw new LexerError(ei, "Unexpected end of file. "
                    + "Unclosed " + s);
        }
        
        return tokens;
    }
}
