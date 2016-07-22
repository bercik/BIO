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
package pl.rcebula.analysis.parser;

import pl.rcebula.analysis.parser.ParserError;
import pl.rcebula.analysis.parser.Parser;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.analysis.ErrorInfo;
import pl.rcebula.preprocessor.MyFiles;

/**
 *
 * @author robert
 */
public class ParserTest
{

    public ParserTest()
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
    
    private ErrorInfo generateErrorInfo(int lineNum, int chNum)
    {
        return new ErrorInfo(lineNum, chNum, new MyFiles.File(0, "test"));
    }

    /**
     * Test of getSteps method, of class Parser.
     */
    @Test
    public void testNoDefKeywordAtStart()
            throws Exception
    {
        System.out.println("testNoDefKeywordAtStart");

        List<Token<?>> inputTokens = new ArrayList<Token<?>>()
        {
            {
                add(new Token<>(TokenType.ID, "onSTART", generateErrorInfo(1, 5)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(1, 12)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(1, 13)));
                add(new Token<>(TokenType.ID, "PRINT", generateErrorInfo(2, 5)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(2, 10)));
                add(new Token<>(TokenType.STRING, "Hello World!\n", generateErrorInfo(2, 11)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(2, 27)));
                add(new Token<>(TokenType.KEYWORD, "end", generateErrorInfo(3, 1)));
                add(new Token<>(TokenType.END, null, generateErrorInfo(4, 1)));
            }
        };

        boolean catched = false;
        try
        {
            Parser parser = new Parser(inputTokens);
        }
        catch (ParserError ex)
        {
            catched = true;
            System.err.println(ex.getMessage());
        }

        assertEquals(true, catched);
    }

    @Test
    public void testHelloWorld() throws Exception
    {
        System.out.println("testHelloWorld");

        List<Token<?>> inputTokens = new ArrayList<Token<?>>()
        {
            {
                add(new Token<>(TokenType.KEYWORD, "def", generateErrorInfo(1, 1)));
                add(new Token<>(TokenType.ID, "onSTART", generateErrorInfo(1, 5)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(1, 12)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(1, 13)));
                add(new Token<>(TokenType.ID, "PRINT", generateErrorInfo(2, 5)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(2, 10)));
                add(new Token<>(TokenType.STRING, "Hello World!\n", generateErrorInfo(2, 11)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(2, 27)));
                add(new Token<>(TokenType.KEYWORD, "end", generateErrorInfo(3, 1)));
                add(new Token<>(TokenType.END, null, generateErrorInfo(4, 1)));
            }
        };

        List<Integer> expectedSteps = Arrays.asList(0, 3, 5, 9, 12, 13, 15, 19, 17, 11, 2);

        Parser parser = new Parser(inputTokens);
        List<Integer> steps = parser.getSteps();

        assertThat(steps, is(expectedSteps));
    }

    @Test
    public void testNestedFunctionsAndParams()
            throws ParserError
    {
        System.out.println("testNestedFunctionsAndParams");

        /*
            def onSTART()
                CALL2(
                    PRINT(ADD(2,4)),
                    PRINT("\nwynik"))
            end
         */
        List<Token<?>> inputTokens = new ArrayList<Token<?>>()
        {
            {
                add(new Token<>(TokenType.KEYWORD, "def", generateErrorInfo(1, 1)));
                add(new Token<>(TokenType.ID, "onSTART", generateErrorInfo(1, 5)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(1, 12)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(1, 13)));

                add(new Token<>(TokenType.ID, "CALL2", generateErrorInfo(2, 5)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(2, 10)));

                add(new Token<>(TokenType.ID, "PRINT", generateErrorInfo(3, 9)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(3, 14)));
                add(new Token<>(TokenType.ID, "ADD", generateErrorInfo(3, 15)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(3, 18)));
                add(new Token<>(TokenType.INT, 2, generateErrorInfo(3, 19)));
                add(new Token<>(TokenType.COMMA, null, generateErrorInfo(3, 20)));
                add(new Token<>(TokenType.INT, 4, generateErrorInfo(3, 21)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(3, 22)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(3, 23)));
                add(new Token<>(TokenType.COMMA, null, generateErrorInfo(3, 24)));

                add(new Token<>(TokenType.ID, "PRINT", generateErrorInfo(4, 9)));
                add(new Token<>(TokenType.OPEN_BRACKET, null, generateErrorInfo(4, 14)));
                add(new Token<>(TokenType.STRING, "\nwynik", generateErrorInfo(4, 15)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(4, 22)));
                add(new Token<>(TokenType.CLOSE_BRACKET, null, generateErrorInfo(4, 23)));

                add(new Token<>(TokenType.KEYWORD, "end", generateErrorInfo(5, 5)));
                add(new Token<>(TokenType.END, null, generateErrorInfo(6, 1)));
            }
        };

        List<Integer> expectedSteps = Arrays.asList(0, 3, 5, 9, 12, 13, 15, 18, 20, 13, 15, 18, 20, 13, 15,
                19, 16, 15, 19, 17, 17, 16, 15, 18, 20, 13, 15, 19, 17, 17, 11, 2);

        Parser parser = new Parser(inputTokens);
        List<Integer> steps = parser.getSteps();

        assertThat(steps, is(expectedSteps));
    }
}
