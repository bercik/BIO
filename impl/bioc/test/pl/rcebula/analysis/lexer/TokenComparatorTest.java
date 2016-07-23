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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class TokenComparatorTest
{
    
    public TokenComparatorTest()
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
        return new ErrorInfo(lineNum, chNum, new MyFiles.File(1, "test"));
    }
    
    /**
     * Test of compare method, of class TokenComparator.
     */
    @Test
    public void testCompare()
    {
        System.out.println("compare");
        
        TokenComparator instance = new TokenComparator();
        
        Token o1 = new Token(TokenType.KEYWORD, "def", generateErrorInfo(0, 0));
        Token o2 = new Token(TokenType.KEYWORD, "def", generateErrorInfo(10, 20));
        int expResult = 0;
        int result = instance.compare(o1, o2);
        assertEquals(expResult, result);
        
        o1 = new Token(TokenType.STRING, "def", generateErrorInfo(0, 0));
        o2 = new Token(TokenType.KEYWORD, "def", generateErrorInfo(0, 0));
        expResult = -1;
        result = instance.compare(o1, o2);
        assertEquals(expResult, result);
        
        o1 = new Token(TokenType.INT, 10, generateErrorInfo(0, 0));
        o2 = new Token(TokenType.INT, 20, generateErrorInfo(0, 0));
        expResult = -1;
        result = instance.compare(o1, o2);
        assertEquals(expResult, result);
    }
    
}
