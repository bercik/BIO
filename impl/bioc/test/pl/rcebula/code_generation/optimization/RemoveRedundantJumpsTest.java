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
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.InterpreterFunctionsGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class RemoveRedundantJumpsTest
{
    private static final InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();
    private static final ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, new MyFiles.File(1, "test"));

    public RemoveRedundantJumpsTest()
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
            throws Exception
    {
        System.out.println("RemoveRedundantJumpsTest.test()");

        IntermediateCode ic = new IntermediateCode();

        Label l1 = new Label();
        Label l2 = new Label();
        Label l3 = new Label();
        Label l4 = new Label();
        Label l5 = new Label();
        Label l6 = new Label();

        ic.appendLine(generateCall()); // 0

        Line line = generateJmp(l2); // 1
        line.addLabel(l1);
        ic.appendLine(line);

        line = generateJmp(l5); // 2
        line.addLabel(l2);
        ic.appendLine(line);

        line = generateJmp(l6); // 3
        line.addLabel(l3);
        ic.appendLine(line);

        line = generateJmp(l1); // 4
        line.addLabel(l4);
        ic.appendLine(line);

        line = generateJmpIfFalse(l3); // 5
        line.addLabel(l5);
        ic.appendLine(line);

        line = generateClearStack();
        line.addLabel(l6);
        ic.appendLine(line); // 6

        RemoveRedundantJumps rrj = new RemoveRedundantJumps(ic, new OptimizationStatistics());

        String expected = "call,foo,-1,-1,1\n"
                + "jmp,5,-1,-1,1\n"
                + "jmp,5,-1,-1,1\n"
                + "jmp,6,-1,-1,1\n"
                + "jmp,5,-1,-1,1\n"
                + "jmp_if_false,6,-1,-1,1\n"
                + "clear_stack\n";

        assertEquals(expected, ic.toString());
    }

    @Test
    public void testInfiniteLoop()
    {
        System.out.println("RemoveRedundantJumpsTest.testInfiniteLoop()");

        IntermediateCode ic = new IntermediateCode();

        Label l1 = new Label();
        Label l2 = new Label();
        Label l3 = new Label();

        Line line = generateJmp(l2); // 1
        line.addLabel(l1);
        ic.appendLine(line);

        line = generateJmp(l3); // 2
        line.addLabel(l2);
        ic.appendLine(line);

        line = generateJmpIfFalse(l1); // 3
        line.addLabel(l3);
        ic.appendLine(line);

        RemoveRedundantJumps rrj = new RemoveRedundantJumps(ic, new OptimizationStatistics());
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
}
