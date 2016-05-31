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
package pl.rcebula.analysis.tree;

import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.UserFunction;
import pl.rcebula.analysis.tree.ProgramTreeCreator;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author robert
 */
public class ProgramTreeCreatorTest
{
    
    public ProgramTreeCreatorTest()
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
    public void testHelloWorld()
    {
        System.out.println("testHelloWorld");
        
        List<Token<?>> inputTokens = new ArrayList<Token<?>>()
        {{
                    add(new Token<>(TokenType.KEYWORD, "def", 1, 1)); // 0
                    add(new Token<>(TokenType.ID, "onSTART", 1, 5)); // 1
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 1, 12)); // 2
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 1, 13)); // 3
                    add(new Token<>(TokenType.ID, "PRINT", 2, 5)); // 4
                    add(new Token<>(TokenType.OPEN_BRACKET, null, 2, 10)); // 5
                    add(new Token<>(TokenType.STRING, "Hello World!\n", 2, 11)); // 6
                    add(new Token<>(TokenType.CLOSE_BRACKET, null, 2, 27)); // 7
                    add(new Token<>(TokenType.KEYWORD, "end", 3, 1)); // 8
                    add(new Token<>(TokenType.END, null, 4, 1)); // 9
        }};
        
        List<Integer> inputSteps = Arrays.asList(0, 3, 5, 9, 12, 13, 15, 19, 17, 11, 2);
        
        ProgramTree expectedProgramTree = new ProgramTree();
        UserFunction uf = new UserFunction("onSTART", 1, 5);
        Call c = new Call("PRINT", null, 2, 5);
        c.addCallParam(new ConstCallParam(inputTokens.get(6), 2, 11));
        uf.addCall(c);
        expectedProgramTree.addUserFunction(uf);
        
        ProgramTreeCreator programTreeCreator = new ProgramTreeCreator(inputTokens, inputSteps);
        ProgramTree pt = programTreeCreator.getProgramTree();
        
        assertEquals(expectedProgramTree, pt);
    }
    
    @Test
    public void testNestedFunctionsAndParams()
    {
        System.out.println("testNestedFunctionsAndParams");
        
        List<Token<?>> inputTokens = new ArrayList<Token<?>>()
        {
            {
                add(new Token<>(TokenType.KEYWORD, "def", 1, 1)); // 0
                add(new Token<>(TokenType.ID, "onSTART", 1, 5)); // 1
                add(new Token<>(TokenType.OPEN_BRACKET, null, 1, 12)); // 2
                add(new Token<>(TokenType.CLOSE_BRACKET, null, 1, 13)); // 3

                add(new Token<>(TokenType.ID, "CALL2", 2, 5)); // 4
                add(new Token<>(TokenType.OPEN_BRACKET, null, 2, 10)); // 5

                add(new Token<>(TokenType.ID, "PRINT", 3, 9)); // 6
                add(new Token<>(TokenType.OPEN_BRACKET, null, 3, 14)); // 7
                add(new Token<>(TokenType.ID, "ADD", 3, 15)); // 8
                add(new Token<>(TokenType.OPEN_BRACKET, null, 3, 18)); // 9
                add(new Token<>(TokenType.INT, 2, 3, 19)); // 10
                add(new Token<>(TokenType.COMMA, null, 3, 20)); // 11
                add(new Token<>(TokenType.INT, 4, 3, 21)); // 12
                add(new Token<>(TokenType.CLOSE_BRACKET, null, 3, 22)); // 13
                add(new Token<>(TokenType.CLOSE_BRACKET, null, 3, 23)); // 14
                add(new Token<>(TokenType.COMMA, null, 3, 24)); // 15

                add(new Token<>(TokenType.ID, "PRINT", 4, 9)); // 16
                add(new Token<>(TokenType.OPEN_BRACKET, null, 4, 14)); // 17
                add(new Token<>(TokenType.STRING, "\nwynik", 4, 15)); // 18
                add(new Token<>(TokenType.CLOSE_BRACKET, null, 4, 22)); // 19
                add(new Token<>(TokenType.CLOSE_BRACKET, null, 4, 23)); // 20

                add(new Token<>(TokenType.KEYWORD, "end", 5, 5)); // 21
                add(new Token<>(TokenType.END, null, 6, 1)); // 22
            }
        };

        List<Integer> inputSteps = Arrays.asList(0, 3, 5, 9, 12, 13, 15, 18, 20, 13, 15, 18, 20, 13, 15,
                19, 16, 15, 19, 17, 17, 16, 15, 18, 20, 13, 15, 19, 17, 17, 11, 2);
        
        ProgramTree expectedProgramTree = new ProgramTree();
        UserFunction uf = new UserFunction("onSTART", 1, 5);
        
        Call c = new Call("CALL2", null, 2, 5);
        Call c1 = new Call("PRINT", c, 3, 9);
        c.addCallParam(c1);
        Call c11 = new Call("ADD", c1, 3, 15);
        c1.addCallParam(c11);
        c11.addCallParam(new ConstCallParam(inputTokens.get(10), 3, 19));
        c11.addCallParam(new ConstCallParam(inputTokens.get(12), 3, 21));
        Call c2 = new Call("PRINT", c, 4, 9);
        c.addCallParam(c2);
        c2.addCallParam(new ConstCallParam(inputTokens.get(18), 4, 15));
        
        uf.addCall(c);
        expectedProgramTree.addUserFunction(uf);
        
        ProgramTreeCreator programTreeCreator = new ProgramTreeCreator(inputTokens, inputSteps);
        ProgramTree pt = programTreeCreator.getProgramTree();
        
        assertEquals(expectedProgramTree, pt);
    }
}
