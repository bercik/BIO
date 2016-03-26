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
package analysis.lexer;

import java.io.IOException;
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
        
        Lexer lexer = new Lexer("/res/ex1.bio", true);
    }
    
    @Test
    public void testHelloWorld()
            throws Exception
    {
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
        }};
        
        Lexer lexer = new Lexer(input);
        List<Token<?>> tokens = lexer.getTokens();
        assertThat(tokens, is(expectedTokens));
    }
}
