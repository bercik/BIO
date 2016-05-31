/*
 * Copyright (C) 2016 robert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.rcebula.analysis.lexer;

import pl.rcebula.analysis.lexer.LexerError;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.lexer.Lexer;
import pl.rcebula.analysis.lexer.Token;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author robert
 */
public class LexerTest
{
    
    public LexerTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testReadInternal()
            throws Exception
    {
        System.out.println("testReadInternal");
        
        Lexer lexer = new Lexer("/pl/rcebula/res/ex1.bio", true);
    }
    
    @Test
    public void testHelloWorld()
            throws Exception
    {
        System.out.println("testHelloWorld");
        
        String input = "def onSTART()\n" +
                               "    PRINT(\"Hello World!\\n\")\n" +
                               "end";
        
        List<Token<?>> expectedTokens = new ArrayList<Token<?>>()
        {{
                    add(new Token<>(TokenType.KEYWORD, "def", 1, 1));
                    add(new Token<>(TokenType.ID, "onSTART", 1, 5));
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 1, 12));
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 1, 13));
                    add(new Token<>(TokenType.ID, "PRINT", 2, 5));
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 2, 10));
                    add(new Token<>(TokenType.STRING, "Hello World!\n", 2, 11));
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 2, 27));
                    add(new Token<>(TokenType.KEYWORD, "end", 3, 1));
                    add(new Token<>(TokenType.END, null, 4, 1));
        }};
        
        Lexer lexer = new Lexer(input);
        List<Token<?>> tokens = lexer.getTokens();
        assertThat(tokens, is(expectedTokens));
    }
    
    /*
    def SOME_EVENT(input)
    end

    def FOO(param)
       PRINT(param)
    end

    def onSTART()
       ATTACH_TO_EVENT(SOME_EVENT, FOO)
       SOME_EVENT("test") % zostanie wypisane na ekran "test"
    end
    */
    @Test
    public void testEvents()
            throws Exception
    {
        System.out.println("testEvents");
        
        String input = "def SOME_EVENT(input)\n" +
                               "end\n" + 
                               "def FOO(param)\n" + 
                               "    PRINT(param)\n" +
                               "end\n" + 
                               "def onSTART()\n" + 
                               "    ATTACH_TO_EVENT(SOME_EVENT, FOO)\n" + 
                               "    SOME_EVENT(\"test\") % zostanie wypisane na ekran \"test\"\n" +
                               "end";
        
        List<Token<?>> expectedTokens = new ArrayList<Token<?>>()
        {{
                    // def SOME_EVENT(input)
                    add(new Token<>(TokenType.KEYWORD, "def", 1, 1));
                    add(new Token<>(TokenType.ID, "SOME_EVENT", 1, 5));
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 1, 15));
                    add(new Token<>(TokenType.ID, "input", 1, 16));
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 1, 21));
                    
                    // end
                    add(new Token<>(TokenType.KEYWORD, "end", 2, 1));
                    
                    // def FOO(param)
                    add(new Token<>(TokenType.KEYWORD, "def", 3, 1));
                    add(new Token<>(TokenType.ID, "FOO", 3, 5));
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 3, 8));
                    add(new Token<>(TokenType.ID, "param", 3, 9));
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 3, 14));
                    
                    //     PRINT(param)
                    add(new Token<>(TokenType.ID, "PRINT", 4, 5));
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 4, 10));
                    add(new Token<>(TokenType.ID, "param", 4, 11));
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 4, 16));
                    
                    // end
                    add(new Token<>(TokenType.KEYWORD, "end", 5, 1));
                    
                    // def onSTART()
                    add(new Token<>(TokenType.KEYWORD, "def", 6, 1));
                    add(new Token<>(TokenType.ID, "onSTART", 6, 5));
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 6, 12));
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 6, 13));
                    
                    //     ATTACH_TO_EVENT(SOME_EVENT, FOO)
                    add(new Token<>(TokenType.ID, "ATTACH_TO_EVENT", 7, 5));
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 7, 20));
                    add(new Token<>(TokenType.ID, "SOME_EVENT", 7, 21));
                    add(new Token<>(TokenType.COMMA, null, 7, 31));
                    add(new Token<>(TokenType.ID, "FOO", 7, 33));
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 7, 36));
                    
                    //     SOME_EVENT(\"test\")
                    add(new Token<>(TokenType.ID, "SOME_EVENT", 8, 5));
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 8, 15));
                    add(new Token<>(TokenType.STRING, "test", 8, 16));
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 8, 22));
                    
                    // end
                    add(new Token<>(TokenType.KEYWORD, "end", 9, 1));
                    
                    // EOF
                    add(new Token<>(TokenType.END, null, 10, 1));
                    
        }};
        
        Lexer lexer = new Lexer(input);
        List<Token<?>> tokens = lexer.getTokens();
        assertThat(tokens, is(expectedTokens));
    }
    
    @Test
    public void unclosedStringTest()
            throws Exception
    {
        System.out.println("unclosedStringTest");
        
        String input = "\"Hello world\n\n";
        
        boolean catched = false;
        try
        {
            Lexer lexer = new Lexer(input);
        }
        catch (LexerError ex)
        {
            catched = true;
            
            System.out.println(ex.getMessage());
        }
        
        assertEquals(true, catched);
    }
}
