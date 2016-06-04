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
package pl.rcebula.code_generation.intermediate;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.semantic.BuiltinFunction;
import pl.rcebula.analysis.semantic.ParamType;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.IdCallParam;
import pl.rcebula.analysis.tree.Param;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.UserFunction;

/**
 *
 * @author robert
 */
public class CodeGeneratorTest
{
    private static List<BuiltinFunction> builtinFunctions;

    public CodeGeneratorTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
        builtinFunctions = new ArrayList<BuiltinFunction>()
        {
            {
                add(new BuiltinFunction("FOR", true, ParamType.CALL, ParamType.ALL, ParamType.CALL));
                add(new BuiltinFunction("ASSIGN_LOCAL", false, ParamType.ID, ParamType.ALL));
                add(new BuiltinFunction("LE", false, ParamType.ALL, ParamType.ALL));
                add(new BuiltinFunction("PRINT", false, ParamType.ALL));
                add(new BuiltinFunction("DEC", false, ParamType.ALL));
                add(new BuiltinFunction("RETURN", false, ParamType.ALL));
            }
        };
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
    public void testSimpleUserFunctionCall()
    {
        System.out.println("testSimpleUserFunctionCall()");

        /*
        def onSTART()
            foo(foo(10))
        end
        
        def foo(x)
        end
         */
        ProgramTree pt = new ProgramTree();
        UserFunction uf = new UserFunction("onSTART", 1, 1);
        Call call = new Call("foo", null, 2, 1);
        Call insideCall = new Call("foo", call, 2, 6);
        insideCall.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, 2, 8), 2, 10));
        call.addCallParam(insideCall);
        uf.addCall(call);
        pt.addUserFunction(uf);

        uf = new UserFunction("foo", 4, 1);
        uf.addParam(new Param("x", 5, 1));
        pt.addUserFunction(uf);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions);
        IntermediateCode ic = cg.getIc();

        String expected = "foo,11,4,1\n"
                + "onSTART,4,1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,int:10,2,10\n"
                + "pop,1\n"
                + "call,foo,2,6\n"
                + "pop,1\n"
                + "call,foo,2,1\n"
                + "\n"
                + "foo,x\n\n";

        assertEquals(expected, ic.toString());
    }

    @Test
    public void testForLoop()
    {
        System.out.println("testForLoop()");

        /*
        def onSTART()
            ASSIGN_LOCAL(x, foo(10))
            PRINT(x)
        end
        
        def foo(p)
            FOR(
                ASSIGN_LOCAL(sum, 0),
                LE(p, 0),
                DEC(p))
            RETURN(sum)
        end
         */
        ProgramTree pt = new ProgramTree();

        // def onSTART()
        UserFunction uf = new UserFunction("onSTART", 1, 1);

        // ASSIGN_LOCAL(x, foo(10))
        Call call = new Call("ASSIGN_LOCAL", null, 2, 1);
        IdCallParam icp = new IdCallParam("x", 2, 2);
        Call call2 = new Call("foo", call, 2, 3);
        ConstCallParam ccp = new ConstCallParam(new Token(TokenType.INT, 10, 2, 4), 2, 4);
        call2.addCallParam(ccp);
        call.addCallParam(icp);
        call.addCallParam(call2);
        uf.addCall(call);

        // PRINT(x)
        call = new Call("PRINT", null, 3, 1);
        icp = new IdCallParam("x", 3, 2);
        call.addCallParam(icp);
        uf.addCall(call);

        pt.addUserFunction(uf);

        // def foo(p)
        uf = new UserFunction("foo", 4, 1);
        uf.addParam(new Param("p", 4, 2));

        /*
        FOR(
                ASSIGN_LOCAL(sum, 0),
                LE(p, 0),
                DEC(p))
         */
        call = new Call("FOR", null, 5, 1);

        // ASSIGN_LOCAL(sum, 0)
        call2 = new Call("ASSIGN_LOCAL", call, 6, 1);
        icp = new IdCallParam("sum", 6, 2);
        call2.addCallParam(icp);
        ccp = new ConstCallParam(new Token(TokenType.INT, 0, 6, 3), 6, 3);
        call2.addCallParam(ccp);
        call.addCallParam(call2);

        // LE(p, 0),
        call2 = new Call("LE", call, 7, 1);
        icp = new IdCallParam("p", 7, 2);
        call2.addCallParam(icp);
        ccp = new ConstCallParam(new Token(TokenType.INT, 0, 7, 3), 7, 3);
        call2.addCallParam(ccp);
        call.addCallParam(call2);

        // DEC(p)
        call2 = new Call("DEC", call, 8, 1);
        icp = new IdCallParam("p", 8, 2);
        call2.addCallParam(icp);
        call.addCallParam(call2);

        uf.addCall(call);

        // RETURN(sum)
        call = new Call("RETURN", null, 9, 1);
        icp = new IdCallParam("sum", 9, 2);
        call.addCallParam(icp);
        uf.addCall(call);

        pt.addUserFunction(uf);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions);
        IntermediateCode ic = cg.getIc();

        String expected = "foo,16,4,1\n"
                + "onSTART,4,1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,id:x,2,2\n"
                + "push,int:10,2,4\n"
                + "pop,1\n"
                + "call,foo,2,3\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,2,1\n"
                + "clear_stack\n"
                + "push,var:x,3,2\n"
                + "pop,1\n"
                + "call_loc,PRINT,3,1\n"
                + "\n"
                + "foo,p\n"
                + "push,id:sum,6,2\n"
                + "push,int:0,6,3\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,6,1\n"
                + "popc,1\n"
                + "push,var:p,7,2\n"
                + "push,int:0,7,3\n"
                + "pop,2\n"
                + "call_loc,LE,7,1\n"
                + "pop,1\n"
                + "jmp_if_false,33\n"
                + "push,var:p,8,2\n"
                + "pop,1\n"
                + "call_loc,DEC,8,1\n"
                + "popc,1\n"
                + "jmp,22\n"
                + "push,none:,-1,-1\n"
                + "clear_stack\n"
                + "push,var:sum,9,2\n"
                + "pop,1\n"
                + "call_loc,RETURN,9,1\n"
                + "\n";
                
                assertEquals(expected, ic.toString());
    }
}
