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
package pl.rcebula.analysis.math_log_parser;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class MathLogParserTest
{
    
    public MathLogParserTest()
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
    public void testLeftAssociative()
            throws Exception
    {
        MyFiles files = new MyFiles(true);
        List<Token<?>> tokens = new ArrayList<>();
        ErrorInfo ei = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());
        
        tokens.add(new Token(TokenType.ID, "BEGIN", ei));
        tokens.add(new Token(TokenType.EXPRESSION, " 1 + 2 - 3 ", genErrorInfo(0, 0, files)));
        tokens.add(new Token(TokenType.ID, "END", ei));
        
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(TokenType.ID, "BEGIN", ei));
        expected.add(new Token(TokenType.ID, "SUB", genErrorInfo(7, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.ID, "ADD", genErrorInfo(3, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.INT, 1, genErrorInfo(1, 0, files)));
        expected.add(new Token(TokenType.COMMA, null, ei));
        expected.add(new Token(TokenType.INT, 2, genErrorInfo(5, 0, files)));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        expected.add(new Token(TokenType.COMMA, null, ei));
        expected.add(new Token(TokenType.INT, 3, genErrorInfo(9, 0, files)));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        expected.add(new Token(TokenType.ID, "END", ei));
        
        MathLogParser instance = new MathLogParser(tokens, files);
        List<Token<?>> result = instance.getTokens();
        
        assertThat(result, is(expected));
    }
    
    @Test
    public void testRightAssociative()
            throws Exception
    {
        MyFiles files = new MyFiles(true);
        List<Token<?>> tokens = new ArrayList<>();
        ErrorInfo ei = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());
        
        tokens.add(new Token(TokenType.EXPRESSION, " 1 ^ 2 ^ 3 ", genErrorInfo(0, 0, files)));
        
        List<Token> expected = new ArrayList<>();
        // POW(1, POW(2, 3))
        expected.add(new Token(TokenType.ID, "POW", genErrorInfo(3, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.INT, 1, genErrorInfo(1, 0, files)));
        expected.add(new Token(TokenType.COMMA, null, ei));
        expected.add(new Token(TokenType.ID, "POW", genErrorInfo(7, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.INT, 2, genErrorInfo(5, 0, files)));
        expected.add(new Token(TokenType.COMMA, null, ei));
        expected.add(new Token(TokenType.INT, 3, genErrorInfo(9, 0, files)));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        
        MathLogParser instance = new MathLogParser(tokens, files);
        List<Token<?>> result = instance.getTokens();
        
        assertThat(result, is(expected));
    }
    
    @Test
    public void testFunCall()
            throws Exception
    {
        MyFiles files = new MyFiles(true);
        List<Token<?>> tokens = new ArrayList<>();
        ErrorInfo ei = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());
        
        tokens.add(new Token(TokenType.EXPRESSION, " foo(\"text\", foo2(2 * var)) ", genErrorInfo(0, 0, files)));
        
        List<Token> expected = new ArrayList<>();
        // foo("text", foo2(MUL(2, var)))
        expected.add(new Token(TokenType.ID, "foo", genErrorInfo(1, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.STRING, "text", genErrorInfo(5, 0, files)));
        expected.add(new Token(TokenType.COMMA, null, ei));
        expected.add(new Token(TokenType.ID, "foo2", genErrorInfo(13, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.ID, "MUL", genErrorInfo(20, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.INT, 2, genErrorInfo(18, 0, files)));
        expected.add(new Token(TokenType.COMMA, null, ei));
        expected.add(new Token(TokenType.ID, "var", genErrorInfo(22, 0, files)));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        
        MathLogParser instance = new MathLogParser(tokens, files);
        List<Token<?>> result = instance.getTokens();
        
        assertThat(result, is(expected));
    }
    
    @Test
    public void testBrackets()
            throws Exception
    {
        MyFiles files = new MyFiles(true);
        List<Token<?>> tokens = new ArrayList<>();
        ErrorInfo ei = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());
        
        tokens.add(new Token(TokenType.EXPRESSION, " (2 + 3) * 5", genErrorInfo(0, 0, files)));
        
        List<Token> expected = new ArrayList<>();
        // MUL(ADD(2, 3), 5)
        expected.add(new Token(TokenType.ID, "MUL", genErrorInfo(9, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.ID, "ADD", genErrorInfo(4, 0, files)));
        expected.add(new Token(TokenType.OPEN_BRACKET, null, ei));
        expected.add(new Token(TokenType.INT, 2, genErrorInfo(2, 0, files)));
        expected.add(new Token(TokenType.COMMA, null, ei));
        expected.add(new Token(TokenType.INT, 3, genErrorInfo(6, 0, files)));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        expected.add(new Token(TokenType.COMMA, null, ei));
        expected.add(new Token(TokenType.INT, 5, genErrorInfo(11, 0, files)));
        expected.add(new Token(TokenType.CLOSE_BRACKET, null, ei));
        
        MathLogParser instance = new MathLogParser(tokens, files);
        List<Token<?>> result = instance.getTokens();
        
        // MUL(null(ADD(2, 3)), 5)
        
        assertThat(result, is(expected));
    }
    
    private ErrorInfo genErrorInfo(int chNum, int lineNum, MyFiles files)
    {
        return new ErrorInfo(lineNum, chNum, files.getFileGeneratedByCompiler());
    }
}
