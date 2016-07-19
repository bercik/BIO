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

import pl.rcebula.analysis.lexer.FiniteStateAutomata;
import pl.rcebula.analysis.lexer.LexerError;
import pl.rcebula.utils.Pair;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.lexer.Lexer;
import pl.rcebula.analysis.lexer.Token;
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
public class FiniteStateAutomataTest
{

    public FiniteStateAutomataTest()
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
    public void testCreation()
    {
        FiniteStateAutomata fsa = new FiniteStateAutomata();
    }

    @Test
    public void testPutIllegalChar() throws Exception
    {
        System.out.println("putIllegalChar");
        FiniteStateAutomata instance = new FiniteStateAutomata();

        boolean catched = false;

        catched = putIllegalChar(instance, ';');
        instance.fullReset();
        assertEquals(true, catched);

        instance.putChar(' ');
        catched = putIllegalChar(instance, ';');
        instance.fullReset();
        assertEquals(true, catched);

        instance.putChar(' ');
        instance.putChar('\t');
        instance.putChar('\n');
        catched = putIllegalChar(instance, ';');
        instance.fullReset();
        assertEquals(true, catched);

        instance.putChar(')');
        catched = putIllegalChar(instance, ';');
        instance.fullReset();
        assertEquals(true, catched);

        instance.putChar('0');
        catched = putIllegalChar(instance, '0');
        instance.fullReset();
        assertEquals(true, catched);

        instance.putChar('1');
        catched = putIllegalChar(instance, 'a');
        instance.fullReset();
        assertEquals(true, catched);
    }

    private boolean putIllegalChar(FiniteStateAutomata fsa, char ch)
    {
        try
        {
            fsa.putChar(ch);
        }
        catch (LexerError ex)
        {
            System.err.println(ex.getMessage());
            return true;
        }

        return false;
    }

    @Test
    public void testKeywords() throws Exception
    {
        System.out.println("testKeywords()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testStringArray(fsa, Lexer.keywords, TokenType.KEYWORD, null);
    }

    @Test
    public void testTrues() throws Exception
    {
        System.out.println("testTrues()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testStringArray(fsa, Lexer.trues, TokenType.BOOL, true);
    }

    @Test
    public void testFalses() throws Exception
    {
        System.out.println("testFalses()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testStringArray(fsa, Lexer.falses, TokenType.BOOL, false);
    }

    @Test
    public void testNones() throws Exception
    {
        System.out.println("testNones()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testStringArray(fsa, Lexer.nones, TokenType.NONE, null, true);
    }

    @Test
    public void testIds() throws Exception
    {
        System.out.println("testIds()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        String[] ids = new String[]
        {
            "_a", "_012", "_a02", "a02a", "__b12", "LicZba", "num12", "_12dsa"
        };
        testStringArray(fsa, ids, TokenType.ID, null);
    }

    @Test
    public void testBracketsComma() throws Exception
    {
        System.out.println("testBracketsComma()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testToken(fsa, "identifier(", TokenType.OPEN_BRACKET, null, 11, 1, false);

        testToken(fsa, "num1\nidentifier)", TokenType.CLOSE_BRACKET, null, 11, 2, false);

        testToken(fsa, "al ( x,", TokenType.COMMA, null, 7, 1, false);
    }

    @Test
    public void testEnd() throws Exception
    {
        System.out.println("testEnd()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testToken(fsa, "_NUM1_\n<EOF>", TokenType.END, null, 1, 2, false);
    }

    @Test
    public void testInt() throws Exception
    {
        System.out.println("testInt()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testToken(fsa, "100)", TokenType.INT, 100, 1, 1, true);

        testToken(fsa, "+100 ", TokenType.INT, 100, 1, 1, true);

        testToken(fsa, "-2561 ", TokenType.INT, -2561, 1, 1, true);

        testToken(fsa, "0 ", TokenType.INT, 0, 1, 1, true);

        boolean catched = false;
        try
        {
            testToken(fsa, "011", TokenType.INT, 011, 1, 1, true);
        }
        catch (LexerError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testFloat() throws Exception
    {
        System.out.println("testFloat()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testToken(fsa, "100.54)", TokenType.FLOAT, 100.54f, 1, 1, true);

        testToken(fsa, "+100.023 ", TokenType.FLOAT, 100.023f, 1, 1, true);

        testToken(fsa, "-2561.555 ", TokenType.FLOAT, -2561.555f, 1, 1, true);

        testToken(fsa, "0.5 ", TokenType.FLOAT, 0.5f, 1, 1, true);

        testToken(fsa, "0.0 ", TokenType.FLOAT, 0.0f, 1, 1, true);
    }

    @Test
    public void testString() throws Exception
    {
        System.out.println("testString()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        String text = "Żółć jest gorzka\n \n i źle smakuje";
        testToken(fsa, "ADD(x, \"" + text + "\"", TokenType.STRING, text, 8, 1, false);
        
        testToken(fsa, "\"\\n \\\\ \\t \\r \\f \\b \\\" \"", TokenType.STRING, 
                                    "\n \\ \t \r \f \b \" ", 1, 1, false);
    }

    @Test
    public void testComment() throws Exception
    {
        System.out.println("testComment()");

        FiniteStateAutomata fsa = new FiniteStateAutomata();

        testComment(fsa, "_NUM1(l1, l2) % jakiś komentarz żśą \n");
    }

    private void testComment(FiniteStateAutomata fsa, String input) throws Exception
    {
        fsa.fullReset();

        for (int i = 0; i < input.length(); ++i)
        {
            Pair<Token<?>, Boolean> pair = fsa.putChar(input.charAt(i));

            // sprawdzamy czy ostatni znak nie został zwrócony
            if (pair.getRight() == true)
            {
                --i;
            }
            
            if (i == input.length() - 1)
            {
                assertEquals(null, pair.getLeft());
                assertEquals(false, pair.getRight());
            }
        }
    }

    private void testToken(FiniteStateAutomata fsa, String input,
            TokenType expectedTokenType, Object expectedValue,
            int expectedChNum, int expectedLine, boolean sepAtTheEnd) throws Exception
    {
        fsa.fullReset();

        for (int i = 0; i < input.length(); ++i)
        {
            Pair<Token<?>, Boolean> pair = fsa.putChar(input.charAt(i));

            if (i == input.length() - 1)
            {
                if (!sepAtTheEnd && pair.getRight())
                {
                    --i;
                    continue;
                }

                Token token = pair.getLeft();
                assertEquals(expectedTokenType, token.getTokenType());
                assertEquals(expectedValue, token.getValue());
                assertEquals(expectedChNum, token.getChNum());
                assertEquals(expectedLine, token.getLine());
            }
            // sprawdzamy czy ostatni znak nie został zwrócony
            else if (pair.getRight() == true)
            {
                --i;
            }
        }
    }

    private void testStringArray(FiniteStateAutomata fsa, String[] array,
            TokenType expectedTokenType, Object expectedValue) throws Exception
    {
        testStringArray(fsa, array, expectedTokenType, expectedValue, false);
    }

    private void testStringArray(FiniteStateAutomata fsa, String[] array,
            TokenType expectedTokenType, Object expectedValue, boolean expectNull) throws Exception
    {
        for (String word : array)
        {
            fsa.fullReset();

            for (int i = 0; i < word.length(); ++i)
            {
                Pair<Token<?>, Boolean> pair = fsa.putChar(word.charAt(i));

                assertEquals(null, pair.getLeft());
                assertEquals(false, pair.getRight());
            }

            // put some separator
            Pair<Token<?>, Boolean> pair = fsa.putChar(Lexer.separators[0]);

            assertEquals(expectedTokenType, pair.getLeft().getTokenType());
            if (expectNull)
            {
                assertEquals(null, pair.getLeft().getValue());
            }
            else if (expectedValue == null)
            {
                assertEquals(word, pair.getLeft().getValue());
            }
            else
            {
                assertEquals(expectedValue, pair.getLeft().getValue());
            }
            assertEquals(true, pair.getRight());
        }
    }
}
