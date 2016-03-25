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
public class TokenTest
{
    private static Token<?> token;
    private final static Integer goodValue = 20;
    private final static String badValue = "20";
    private final static Integer ch = 5;
    private final static Integer line = 50;
    private final static TokenType tokenType = TokenType.INT;
    
    public TokenTest()
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
        token = new Token<>(tokenType, goodValue, line, ch);
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getTokenType method, of class Token.
     */
    @Test
    public void testGetTokenType()
    {
        System.out.println("getTokenType");
        TokenType expResult = tokenType;
        TokenType result = token.getTokenType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class Token.
     */
    @Test
    public void testGetValue()
    {
        System.out.println("getValue");
        Object expResult = goodValue;
        Object result = token.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLine method, of class Token.
     */
    @Test
    public void testGetLine()
    {
        System.out.println("getLine");
        int expResult = line;
        int result = token.getLine();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCh method, of class Token.
     */
    @Test
    public void testGetCh()
    {
        System.out.println("getCh");
        int expResult = ch;
        int result = token.getChNum();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testBadValue()
    {
        System.out.println("testBadValue()");
        boolean catched = false;
        try
        {
            token = new Token<>(TokenType.INT, badValue, ch, line);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            catched = true;
        }
        
        assertEquals(true, catched);
    }
    
    @Test
    public void testTokenWithNullValueType()
    {
        System.out.println("testTokenWithNullValueType()");
        token = new Token<>(TokenType.CLOSE_BRACKET, "unnecessary info", 0, 0);
    }
    
    @Test
    public void testCheatedValueType()
    {
        System.out.println("testCheatedValueType()");
        boolean catched = false;
        try
        {
            token = new Token<Integer>(TokenType.BOOL, 1, 0, 0);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            catched = true;
        }
        
        assertEquals(true, catched);
    }
}
