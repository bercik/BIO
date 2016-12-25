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
import pl.rcebula.analysis.parser.ParserError;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class CommaTests
{

    public CommaTests()
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

    @Test(expected = ParserError.class)
    public void test()
            throws Exception
    {
        MyFiles files = new MyFiles(true);
        List<Token<?>> tokens = new ArrayList<>();
        ErrorInfo ei = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());

        tokens.add(new Token(TokenType.EXPRESSION, " MAX(2, (3, 4)) * (5, 10)", genErrorInfo(0, 0, files)));
        
        MathLogParser instance = new MathLogParser(tokens, files);
    }

    private ErrorInfo genErrorInfo(int chNum, int lineNum, MyFiles files)
    {
        return new ErrorInfo(lineNum, chNum, files.getFileGeneratedByCompiler());
    }
}
