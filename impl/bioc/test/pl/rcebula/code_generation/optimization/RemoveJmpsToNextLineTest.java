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
package pl.rcebula.code_generation.optimization;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.code_generation.intermediate.InterpreterFunctionsGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.utils.OptimizationStatistics;

/**
 *
 * @author robert
 */
public class RemoveJmpsToNextLineTest
{
    private static final InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();
    private static final ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, new MyFiles.File(1, "test"));

    public RemoveJmpsToNextLineTest()
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
    public void test()
    {
        System.out.println("RemoveJmpsToNextLineTest.test()");

        IntermediateCode ic = new IntermediateCode();

        Label l3 = new Label();
        Label l6 = new Label();
        
        // 0
        ic.appendLine(generatePushBool(true));
        // 1
        ic.appendLine(generateJmp(l3));
        // 2
        ic.appendLine(generateJmp(l3));
        // 3
        Line line = generatePushBool(false);
        line.addLabel(l3);
        ic.appendLine(line);
        // 4
        ic.appendLine(generatePop1());
        // 5
        ic.appendLine(generateJmpIfFalse(l6));
        // 6
        line = generatePushBool(true);
        line.addLabel(l6);
        ic.appendLine(line);
        // 7
        ic.appendLine(new Line());
        
        OptimizationStatistics statistics = new OptimizationStatistics();
        new RemoveJmpsToNextLine(ic, statistics);
        
        assertEquals(statistics.getJumpsToNextLineRemoved(), 3);
        
        String expected = "push,bool:true,-1,-1,1\n"
                + "push,bool:false,-1,-1,1\n"
                + "popc,1\n"
                + "push,bool:true,-1,-1,1\n"
                + "\n";
        
        assertEquals(expected, ic.toString());
    }

    private Line generateJmp(Label l)
    {
        return ifg.generateJmp(l, mockErrorInfo);
    }

    private Line generateJmpIfFalse(Label l)
    {
        return ifg.generateJmpIfFalse(l, mockErrorInfo);
    }
    
    private Line generatePushBool(boolean val)
    {
        return ifg.generatePush(new ConstCallParam(new Token(TokenType.BOOL, val, mockErrorInfo), 
                mockErrorInfo));
    }

    private Line generatePop1()
    {
        return ifg.generatePop(1);
    }
}
