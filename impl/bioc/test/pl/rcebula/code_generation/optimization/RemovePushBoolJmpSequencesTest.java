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
import pl.rcebula.analysis.ErrorInfo;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.InterpreterFunctionsGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.preprocessor.MyFiles;
import pl.rcebula.utils.OptimizationStatistics;

/**
 *
 * @author robert
 */
public class RemovePushBoolJmpSequencesTest
{
    private static final InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();
    
    private final MyFiles files = new MyFiles();
    private final ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, files.addFile("test"));

    public RemovePushBoolJmpSequencesTest()
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
        System.out.println("RemovePushBoolPopJmpIfFalseSequencesTest.test()");

        IntermediateCode ic = new IntermediateCode();

        /*
        [1] call, foo
        [2] push, bool:false
        [3] pop, 1
        [4] jmp_if_false, 1
        [5] clear_stack
        [6] push, bool:true
        [7] pop, 1
        [8] jmp_if_false, 6
        [9] call, foo
        [10] 
         */
        Label l1 = new Label();
        Label l6 = new Label();

        Line line = generateCall();
        line.addLabel(l1);
        ic.appendLine(line);

        ic.appendLine(generatePushBool(false));
        ic.appendLine(generatePop1());
        ic.appendLine(generateJmpIfFalse(l1));
        ic.appendLine(generateClearStack());

        line = generatePushBool(true);
        line.addLabel(l6);
        ic.appendLine(line);

        ic.appendLine(generatePop1());
        ic.appendLine(generateJmpIfFalse(l6));
        ic.appendLine(generateCall());
        ic.appendLine(new Line());

        
        RemovePushBoolJmpSequences rpbpjifs = 
                new RemovePushBoolJmpSequences(ic, new OptimizationStatistics(), files);

        String expected = "call,foo,-1,-1,1\n"
                + "jmp,0,-1,-1,1\n"
                + "clear_stack\n"
                + "call,foo,-1,-1,1\n\n";

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

    private Line generateClearStack()
    {
        return ifg.generateClearStack();
    }

    private Line generateCall()
    {
        return ifg.generateCall("foo", mockErrorInfo);
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
