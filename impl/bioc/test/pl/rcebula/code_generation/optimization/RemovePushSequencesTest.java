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
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.InterpreterFunctionsGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.utils.OptimizationStatistics;

/**
 *
 * @author robert
 */
public class RemovePushSequencesTest
{
    private static final InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();

    public RemovePushSequencesTest()
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
        System.out.println("RemovePushSequencesTest.test()");
        
        IntermediateCode ic = new IntermediateCode();

        ic.appendLine(generateCallLine());
        ic.appendLine(generatePopcLine(1));
        ic.appendLine(generatePushNoneLine());
        ic.appendLine(generatePushNoneLine());
        ic.appendLine(generatePopcLine(1));
        ic.appendLine(generateCallLine());
        ic.appendLine(generateClearStackLine());
        ic.appendLine(generatePushNoneLine());
        ic.appendLine(generatePushNoneLine());
        ic.appendLine(generatePopcLine(3));
        ic.appendLine(generatePushNoneLine());
        ic.appendLine(generatePushNoneLine());
        ic.appendLine(generateClearStackLine());
        ic.appendLine(new Line());
        ic.appendLine(generatePushNoneLine());
        ic.appendLine(generatePopcLine(2));

        new RemovePushPopcSequences(ic, new OptimizationStatistics());

        String expected = "call,foo,-1,-1\n"
                + "popc,1\n"
                + "push,none:,-1,-1\n"
                + "call,foo,-1,-1\n"
                + "clear_stack\n"
                + "clear_stack\n"
                + "\n"
                + "push,none:,-1,-1\n"
                + "popc,2\n";
        
        assertEquals(expected, ic.toString());
    }

    private Line generateCallLine()
    {
        return ifg.generateCall("foo", -1, -1);
    }

    private Line generatePushNoneLine()
    {
        return ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, -1, -1), -1, -1));
    }

    private Line generatePopcLine(int pops)
    {
        return ifg.generatePopc(pops);
    }

    private Line generatePopLine(int pops)
    {
        return ifg.generatePop(pops);
    }

    private Line generateClearStackLine()
    {
        return ifg.generateClearStack();
    }
}
