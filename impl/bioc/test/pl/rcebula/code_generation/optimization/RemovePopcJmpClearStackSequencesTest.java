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
import pl.rcebula.code_generation.intermediate.InterpreterFunctionsGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class RemovePopcJmpClearStackSequencesTest
{
    private static final InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();
    
    private final MyFiles files = new MyFiles();
    private final ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, files.addFile("test"));
    
    public RemovePopcJmpClearStackSequencesTest()
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
        System.out.println("RemovePopcJmpClearStackSequencesTest.test()");
        
        IntermediateCode ic = new IntermediateCode();

        /*
        [1] push, bool:false
        [2] pop, 1
        [3] jmp, 7
        [4] popc,1
        [5] jmp, 7
        [6] push, bool:false
        [7] clear_stack
        [8] popc, 1
        [9] jmp_if_false, 7
         */
        Label l7 = new Label();

        ic.appendLine(generatePushBool(false));
        ic.appendLine(generatePop1());
        ic.appendLine(generateJmp(l7));
        ic.appendLine(generatePopc1());
        ic.appendLine(generateJmp(l7));
        ic.appendLine(generatePushBool(false));

        Line line = generateClearStack();
        line.addLabel(l7);
        ic.appendLine(line);

        ic.appendLine(generatePopc1());
        ic.appendLine(generateJmpIfFalse(l7));
        
        RemovePopcJmpClearStackSequences rpjcss = new RemovePopcJmpClearStackSequences(ic, 
                new OptimizationStatistics());

        String expected = "push,bool:false,-1,-1,1\n"
                + "pop,1\n"
                + "jmp,5,-1,-1,1\n"
                + "jmp,5,-1,-1,1\n"
                + "push,bool:false,-1,-1,1\n"
                + "clear_stack\n"
                + "popc,1\n"
                + "jmp_if_false,5,-1,-1,1\n";

        String str = ic.toString();
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

    private Line generatePushBool(boolean val)
    {
        return ifg.generatePush(new ConstCallParam(new Token(TokenType.BOOL, val, mockErrorInfo),
                mockErrorInfo));
    }

    private Line generatePop1()
    {
        return ifg.generatePop(1);
    }
    
    private Line generatePopc1()
    {
        return ifg.generatePopc(1);
    }
}
