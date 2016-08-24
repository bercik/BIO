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
import pl.rcebula.code_generation.intermediate.InterpreterFunctionsGenerator;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;

/**
 *
 * @author robert
 */
public class RemoveClearStackAtBeginningTest
{
    private static final InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();
    
    public RemoveClearStackAtBeginningTest()
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

    /**
     * Test of isOptimized method, of class RemoveClearStackAtBeginning.
     */
    @Test
    public void test()
    {
        System.out.println("RemoveClearStackAtBeginningTest.test()");
        
        IntermediateCode ic = new IntermediateCode();

        /*
        [0] 
        [1] pop, 1
        [2] clear_stack
        [3] pop, 1
        [4] clear_stack
        [5] 
        [6] pop, 1
        [7] pop, 1
        [8] clear_stack 
        */
        
        // 0
        ic.appendLine(new Line());
        // 1
        ic.appendLine(generatePop1());
        // 2
        ic.appendLine(generateClearStack());
        // 3
        ic.appendLine(generatePop1());
        // 4
        ic.appendLine(generateClearStack());
        // 5
        ic.appendLine(new Line());
        // 6
        ic.appendLine(generatePop1());
        // 7
        ic.appendLine(generatePop1());
        // 8
        ic.appendLine(generateClearStack());
        
        new RemoveClearStackAtBeginning(ic, new OptimizationStatistics());
        
        String expected = "\n" +
                "pop,1\n" +
                "pop,1\n" +
                "clear_stack\n" +
                "\n" +
                "pop,1\n" +
                "pop,1\n" +
                "clear_stack\n";
        
        assertEquals(expected, ic.toString());
    }
    
    private Line generateClearStack()
    {
        return ifg.generateClearStack();
    }
    
    private Line generatePop1()
    {
        return ifg.generatePop(1);
    }
}
