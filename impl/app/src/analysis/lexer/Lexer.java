package analysis.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

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

    public Lexer(String path) throws IOException
    {
        String content = readFile(path, "UTF-8");

        for (int i = 0; i < content.length(); ++i)
        {
            char ch = content.charAt(i);

            if (ch == '\n')
            {
                int a = 10;
            }
        }
    }

    private String readFile(String path, String encoding)
            throws IOException
    {
        InputStream is = getClass().getResourceAsStream(path);
        try (Scanner s = new Scanner(is, encoding))
        {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }
}
