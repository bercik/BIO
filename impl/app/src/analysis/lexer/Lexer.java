package analysis.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 *
 * @author robert
 */
public class Lexer
{
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
