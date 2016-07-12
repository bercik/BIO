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
package pl.rcebula.code_generation.optimization.graph;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.Constants;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.code_generation.intermediate.IntermediateCode;
import pl.rcebula.code_generation.intermediate.InterpreterFunctionsGenerator;
import pl.rcebula.code_generation.intermediate.Label;
import pl.rcebula.code_generation.intermediate.Line;
import pl.rcebula.code_generation.optimization.CodeOptimizationError;
import pl.rcebula.utils.Statistics;

/**
 *
 * @author robert
 */
public class FlowGraphAnalyseTest
{
    private final InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();
    
    public FlowGraphAnalyseTest()
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
    public void testGoodCode()
            throws Exception
    {
        IntermediateCode ic = new IntermediateCode();
        
        /*
        [1] call, foo
        [2] 
        [3] call, foo
        [4] call, foo
        [5] jmp_if_false, 3
        [6] call, foo
        [7] jmp_if_false, 5
        [8] call_loc, return
        [9] 
        */
        
        Label l4 = new Label();
        Label l6 = new Label();
        
        ic.appendLine(generateCall());
        ic.appendLine(new Line());
        ic.appendLine(generateCall());
        
        Line line = generateCall();
        line.addLabel(l4);
        ic.appendLine(line);
        
        ic.appendLine(generateJmpIfFalse(l4));
        
        line = generateCall();
        line.addLabel(l6);
        ic.appendLine(line);
        
        ic.appendLine(generateJmpIfFalse(l6));
        ic.appendLine(generateCallLocReturn());
        ic.appendLine(new Line());
        
        Statistics statistics = new Statistics();
        FlowGraphAnalyse fga = new FlowGraphAnalyse(ic, statistics);
        
        assertTrue(statistics.getUnusedCodeBlocksLinesRemoved() == 0);
    }
    
    @Test
    public void testUnusedCode()
            throws Exception
    {
        /*
        [0] push, none
        [1] 
        [2] push, none % function header
        [3] push, none % function start
        [4] jmp_if_false, 7
        [5] jmp, 3
        [6] jmp,10
        [7] push, none
        [8] call_loc, add
        [9] jmp_if_false, 3
        [10] push, none
        [11] call_loc, return % function end
        [12] 
        */
        
        IntermediateCode ic = new IntermediateCode();
        Label l3 = new Label();
        Label l7 = new Label();
        Label l10 = new Label();
        
        // 0
        ic.appendLine(generatePushNone());
        // 1
        ic.appendLine(new Line());
        // 2
        ic.appendLine(generatePushNone());
        // 3
        Line l = generatePushNone();
        l.addLabel(l3);
        ic.appendLine(l);
        // 4
        l = generateJmpIfFalse(l7);
        ic.appendLine(l);
        // 5
        l = generateJmp(l3);
        ic.appendLine(l);
        // 6
        l = generateJmp(l10);
        ic.appendLine(l);
        // 7
        l = generatePushNone();
        l.addLabel(l7);
        ic.appendLine(l);
        // 8
        ic.appendLine(generateCallLoc());
        // 9
        l = generateJmpIfFalse(l3);
        ic.appendLine(l);
        // 10
        l = generatePushNone();
        l.addLabel(l10);
        ic.appendLine(l);
        // 11
        l = generateCallLocReturn();
        ic.appendLine(l);
        // 12
        ic.appendLine(new Line());
        
        Statistics statistics = new Statistics();
        FlowGraphAnalyse fga = new FlowGraphAnalyse(ic, statistics);
        
        assertTrue(statistics.getUnusedCodeBlocksLinesRemoved() == 1);
    }
    
    @Test
    public void testInfiniteLoop()
            throws Exception
    {
        /*
        [0] push, none
        [1] 
        [2] push, none %header
        [3] push, none %bs
        [4] jmp_if_false, 6 
        [5] call_loc, return %end
        [6] jmp_if_false, 8 %b1
        [7] push, none %b2
        [8] push, none %b3
        [9] jmp_if_false, 7 %b4
        [10] push, none, -1, -1 %b5
        [11] push, none, 10, 12
        [12] jmp, 6
        [13]
        */
        
        IntermediateCode ic = new IntermediateCode();
        
        Label l6 = new Label();
        Label l7 = new Label();
        Label l8 = new Label();
        
        // 0
        ic.appendLine(generatePushNone());
        // 1
        ic.appendLine(new Line());
        // 2
        ic.appendLine(generatePushNone());
        // 3
        ic.appendLine(generatePushNone());
        // 4
        ic.appendLine(generateJmpIfFalse(l6));
        // 5
        ic.appendLine(generateCallLocReturn());
        // 6
        Line line = generateJmpIfFalse(l8);
        line.addLabel(l6);
        ic.appendLine(line);
        // 7
        line = generatePushNone();
        line.addLabel(l7);
        ic.appendLine(line);
        // 8
        line = generatePushNone();
        line.addLabel(l8);
        ic.appendLine(line);
        // 9
        ic.appendLine(generateJmpIfFalse(l7));
        // 10
        ic.appendLine(generatePushNone());
        // 11
        ic.appendLine(generatePushNone(10, 12));
        // 12
        ic.appendLine(generateJmp(l6));
        // 13
        ic.appendLine(new Line());
        
        Statistics statistics = new Statistics();
        boolean catched = false;
        try
        {
            FlowGraphAnalyse fga = new FlowGraphAnalyse(ic, statistics);
        }
        catch (CodeOptimizationError er)
        {
            System.err.println(er.getMessage());
            catched = true;
        }
        
        assertTrue(catched);
    }
    
    private Line generatePushNone()
    {
        return ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, -1, -1), -1, -1));
    }
    
    private Line generatePushNone(int line, int chNum)
    {
        return ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, line, chNum), line, chNum));
    }
    
    private Line generateCallLoc()
    {
        return ifg.generateCallLoc("foo", -1, -1);
    }
    
     private Line generateCall()
    {
        return ifg.generateCall("foo", -1, -1);
    }
     
     private Line generateCallLocReturn()
    {
        return ifg.generateCallLoc(Constants.returnFunctionName, -1, -1);
    }
     
     private Line generateJmp(Label l)
    {
        return ifg.generateJmp(l, -1, -1);
    }
     
     private Line generateJmpIfFalse(Label l)
    {
        return ifg.generateJmpIfFalse(l, -1, -1);
    }
}
