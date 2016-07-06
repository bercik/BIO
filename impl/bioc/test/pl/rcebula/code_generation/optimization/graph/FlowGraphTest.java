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

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
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

/**
 *
 * @author robert
 */
public class FlowGraphTest
{
    private final InterpreterFunctionsGenerator ifg = new InterpreterFunctionsGenerator();
    
    public FlowGraphTest()
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
        System.out.println("FlowGraphTest.test()");
        
        IntermediateCode ic = new IntermediateCode();
        Label l0 = new Label();
        Label l2 = new Label();
        Label l6 = new Label();
        Label l9 = new Label();
        
        // 0
        Line l = generatePushNone();
        l.addLabel(l0);
        ic.appendLine(l);
        
        // 1
        ic.appendLine(generatePushNone());
        
        // 2
        l = generatePushNone();
        l.addLabel(l2);
        ic.appendLine(l);
        
        // 3
        l = generateJmpIfFalse(l6);
        ic.appendLine(l);
        
        // 4 
        l = generateJmp(l2);
        ic.appendLine(l);
        
        // 5
        l = generateJmp(l9);
        ic.appendLine(l);
        
        // 6
        l = generatePushNone();
        l.addLabel(l6);
        ic.appendLine(l);
        
        // 7
        ic.appendLine(generateCallLoc());
        
        // 8
        l = generateJmpIfFalse(l0);
        ic.appendLine(l);
        
        // 9 
        l = generatePushNone();
        l.addLabel(l9);
        ic.appendLine(l);
        
        // 10
        l = generateCallLocReturn();
        ic.appendLine(l);
        
        FlowGraph fg = new FlowGraph(ic, 0, ic.numberOfLines());
        
        List<CodeBlock> codeBlocks = fg.getCodeBlocks();
        
        List<CodeBlock> expectedCodeBlocks = new ArrayList<>();
        CodeBlock cb1 = new CodeBlock(0, 1);
        CodeBlock cb2 = new CodeBlock(2, 3);
        CodeBlock cb3 = new CodeBlock(4, 4);
        CodeBlock cb4 = new CodeBlock(5, 5);
        CodeBlock cb5 = new CodeBlock(6, 8);
        CodeBlock cb6 = new CodeBlock(9, 10);
        
        cb1.addOutCodeBlock(cb2);
        cb2.addOutCodeBlock(cb5);
        cb2.addOutCodeBlock(cb3);
        cb3.addOutCodeBlock(cb2);
        cb4.addOutCodeBlock(cb6);
        cb5.addOutCodeBlock(cb1);
        cb5.addOutCodeBlock(cb6);
        
        expectedCodeBlocks.add(cb1);
        expectedCodeBlocks.add(cb2);
        expectedCodeBlocks.add(cb3);
        expectedCodeBlocks.add(cb4);
        expectedCodeBlocks.add(cb5);
        expectedCodeBlocks.add(cb6);
        
        assertThat(codeBlocks, is(expectedCodeBlocks));
    }
    
    private Line generateCallLoc()
    {
        return ifg.generateCallLoc("foo", -1, -1);
    }
    
    private Line generateCallLocReturn()
    {
        return ifg.generateCallLoc(Constants.returnFunctionName, -1, -1);
    }
    
    private Line generatePushNone()
    {
        return ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, -1, -1), -1, -1));
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
