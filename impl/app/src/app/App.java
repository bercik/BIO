package app;

import analysis.lexer.Lexer;

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
        Lexer lexer = new Lexer(args[0], false);
    }

}
