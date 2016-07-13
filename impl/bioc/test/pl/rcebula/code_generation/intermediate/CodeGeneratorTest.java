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
                add(new BuiltinFunction("CALL2", true, ParamType.CALL, ParamType.CALL));
                add(new BuiltinFunction("DN", true));
                add(new BuiltinFunction("IF", true, ParamType.ALL, ParamType.CALL, ParamType.CALL));
                add(new BuiltinFunction("BREAK", true));
                add(new BuiltinFunction("CONTINUE", true));

                add(new BuiltinFunction("LS", false, ParamType.ALL, ParamType.ALL));
                add(new BuiltinFunction("GT", false, ParamType.ALL, ParamType.ALL));
                add(new BuiltinFunction("LE", false, ParamType.ALL, ParamType.ALL));
                add(new BuiltinFunction("ASSIGN_LOCAL", false, ParamType.ID, ParamType.ALL));
                add(new BuiltinFunction("PRINT", false, ParamType.ALL));
                add(new BuiltinFunction("DEC", false, ParamType.ID));
                add(new BuiltinFunction("INC", false, ParamType.ID));
                add(new BuiltinFunction("RETURN", false, ParamType.ALL));
                add(new BuiltinFunction("MUL", false, ParamType.ALL, ParamType.ALL));
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

        String expected = "foo,10,4,1\n"
                + "onSTART,3,1,1\n"
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
                DEC(p),
                DN())
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

        // DN()
        call2 = new Call("DN", call, 9, 1);
        call.addCallParam(call2);

        uf.addCall(call);

        // RETURN(sum)
        call = new Call("RETURN", null, 10, 1);
        icp = new IdCallParam("sum", 10, 2);
        call.addCallParam(icp);
        uf.addCall(call);

        pt.addUserFunction(uf);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions);
        IntermediateCode ic = cg.getIc();

        String expected = "foo,15,4,1\n"
                + "onSTART,3,1,1\n"
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
                + "jmp_if_false,34,5,1\n"
                + "push,id:p,8,2\n"
                + "pop,1\n"
                + "call_loc,DEC,8,1\n"
                + "popc,1\n"
                + "push,none:,-1,-1\n"
                + "popc,1\n"
                + "jmp,21,5,1\n"
                + "push,none:,-1,-1\n"
                + "clear_stack\n"
                + "push,var:sum,10,2\n"
                + "pop,1\n"
                + "call_loc,RETURN,10,1\n"
                + "\n";

        assertEquals(expected, ic.toString());
    }

    @Test
    public void testNestedForLoops()
    {
        /*
        def onSTART()
            FOR(
                ASSIGN_LOCAL(i, 0),
                LS(i, 10),
                CALL2(
                    FOR(
                        ASSIGN_LOCAL(j, 0),
                        LS(j, 10),
                        CALL2(
                            PRINT(MUL(i, j)),
                            INC(j)), % CALL2
                        DN()
                           ), % FOR
                    INC(i)), % CALL2
                DN()
                ) % FOR
        end
         */

        ProgramTree pt = new ProgramTree();

        // def onSTART()
        UserFunction uf = new UserFunction("onSTART", 1, 1);
        pt.addUserFunction(uf);

        // FOR
        Call callFor = new Call("FOR", null, 2, 1);
        uf.addCall(callFor);

        // ASSIGN_LOCAL(i, 0),
        Call call1 = new Call("ASSIGN_LOCAL", callFor, 3, 1);
        callFor.addCallParam(call1);
        call1.addCallParam(new IdCallParam("i", 3, 2));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 0, 3, 3), 3, 3));

        // LS(i, 10),
        call1 = new Call("LS", callFor, 4, 1);
        callFor.addCallParam(call1);
        call1.addCallParam(new IdCallParam("i", 4, 2));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, 4, 3), 4, 3));

        // CALL2(
        Call call2 = new Call("CALL2", callFor, 5, 1);
        callFor.addCallParam(call2);

        // FOR(
        Call insideFor = new Call("FOR", call2, 6, 1);
        call2.addCallParam(insideFor);

        // ASSIGN_LOCAL(j, 0),
        call1 = new Call("ASSIGN_LOCAL", insideFor, 7, 1);
        insideFor.addCallParam(call1);
        call1.addCallParam(new IdCallParam("j", 7, 2));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 0, 7, 3), 7, 3));

        // LS(j, 10),
        call1 = new Call("LS", insideFor, 8, 1);
        insideFor.addCallParam(call1);
        call1.addCallParam(new IdCallParam("j", 8, 2));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, 8, 3), 8, 3));

        // CALL2(
        Call insideCall2 = new Call("CALL2", insideFor, 9, 1);
        insideFor.addCallParam(insideCall2);

        // PRINT(MUL(i, j)),
        call1 = new Call("PRINT", insideCall2, 10, 1);
        insideCall2.addCallParam(call1);
        Call call22 = new Call("MUL", call1, 10, 2);
        call1.addCallParam(call22);
        call22.addCallParam(new IdCallParam("i", 10, 3));
        call22.addCallParam(new IdCallParam("j", 10, 4));

        // INC(j)
        call1 = new Call("INC", insideCall2, 11, 1);
        insideCall2.addCallParam(call1);
        call1.addCallParam(new IdCallParam("j", 11, 2));

        // DN()
        call1 = new Call("DN", insideFor, 12, 1);
        insideFor.addCallParam(call1);

        //  INC(i)
        call1 = new Call("INC", call2, 13, 1);
        call2.addCallParam(call1);
        call1.addCallParam(new IdCallParam("i", 13, 2));

        // DN()
        call1 = new Call("DN", callFor, 14, 1);
        callFor.addCallParam(call1);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions);
        IntermediateCode ic = cg.getIc();

        String expected = "onSTART,2,1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,id:i,3,2\n"
                + "push,int:0,3,3\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,3,1\n"
                + "popc,1\n"
                + "push,var:i,4,2\n"
                + "push,int:10,4,3\n"
                + "pop,2\n"
                + "call_loc,LS,4,1\n"
                + "pop,1\n"
                + "jmp_if_false,48,2,1\n"
                + "push,id:j,7,2\n"
                + "push,int:0,7,3\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,7,1\n"
                + "popc,1\n"
                + "push,var:j,8,2\n"
                + "push,int:10,8,3\n"
                + "pop,2\n"
                + "call_loc,LS,8,1\n"
                + "pop,1\n"
                + "jmp_if_false,39,6,1\n"
                + "push,var:i,10,3\n"
                + "push,var:j,10,4\n"
                + "pop,2\n"
                + "call_loc,MUL,10,2\n"
                + "pop,1\n"
                + "call_loc,PRINT,10,1\n"
                + "popc,1\n"
                + "push,id:j,11,2\n"
                + "pop,1\n"
                + "call_loc,INC,11,1\n"
                + "popc,1\n"
                + "push,none:,-1,-1\n"
                + "popc,1\n"
                + "jmp,19,6,1\n"
                + "push,none:,-1,-1\n"
                + "popc,1\n"
                + "push,id:i,13,2\n"
                + "pop,1\n"
                + "call_loc,INC,13,1\n"
                + "popc,1\n"
                + "push,none:,-1,-1\n"
                + "popc,1\n"
                + "jmp,8,2,1\n"
                + "push,none:,-1,-1\n"
                + "\n";

        assertEquals(expected, ic.toString());
    }

    @Test
    public void testBreak()
    {
        /*
        def onSTART()
            ASSIGN_LOCAL(i, 0),
            FOR(
                DN(),
                true,
                CALL2(
                    PRINT(INC(i)),
                    IF(GT(i, 9), BREAK(), CONTINUE())
                    ), % CALL2
                DN()
                ) % FOR
        end
         */

        ProgramTree pt = new ProgramTree();

        // onSTART()
        UserFunction uf = new UserFunction("onSTART", 1, 1);
        pt.addUserFunction(uf);

        // ASSIGN_LOCAL(i, 0),
        Call call1 = new Call("ASSIGN_LOCAL", null, 2, 1);
        uf.addCall(call1);
        call1.addCallParam(new IdCallParam("i", 2, 2));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 0, 2, 3), 2, 3));

        // FOR(
        Call forCall = new Call("FOR", null, 3, 1);
        uf.addCall(forCall);

        // DN(),
        call1 = new Call("DN", forCall, 4, 1);
        forCall.addCallParam(call1);

        // true,
        forCall.addCallParam(new ConstCallParam(new Token(TokenType.BOOL, true, 5, 1), 5, 1));

        // CALL2(
        Call call2 = new Call("CALL2", forCall, 6, 1);
        forCall.addCallParam(call2);

        // PRINT(INC(i)),
        call1 = new Call("PRINT", call2, 7, 1);
        call2.addCallParam(call1);
        Call call22 = new Call("INC", call1, 7, 2);
        call1.addCallParam(call22);
        call22.addCallParam(new IdCallParam("i", 7, 3));

        // IF(GT(i, 9), BREAK(), CONTINUE())
        Call ifCall = new Call("IF", call2, 8, 1);
        call2.addCallParam(ifCall);
        // GT(i, 9)
        call1 = new Call("GT", ifCall, 8, 2);
        ifCall.addCallParam(call1);
        call1.addCallParam(new IdCallParam("i", 8, 3));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 9, 8, 4), 8, 4));
        // BREAK()
        call1 = new Call("BREAK", ifCall, 8, 5);
        ifCall.addCallParam(call1);
        // CONTINUE()
        call1 = new Call("CONTINUE", ifCall, 8, 6);
        ifCall.addCallParam(call1);

        // DN()
        call1 = new Call("DN", forCall, 9, 1);
        forCall.addCallParam(call1);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions);
        IntermediateCode ic = cg.getIc();

        String expected = "onSTART,2,1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,id:i,2,2\n"
                + "push,int:0,2,3\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,2,1\n"
                + "clear_stack\n"
                + "push,none:,-1,-1\n"
                + "popc,1\n"
                + "push,bool:true,5,1\n"
                + "pop,1\n"
                + "jmp_if_false,35,3,1\n"
                + "push,id:i,7,3\n"
                + "pop,1\n"
                + "call_loc,INC,7,2\n"
                + "pop,1\n"
                + "call_loc,PRINT,7,1\n"
                + "popc,1\n"
                + "push,var:i,8,3\n"
                + "push,int:9,8,4\n"
                + "pop,2\n"
                + "call_loc,GT,8,2\n"
                + "pop,1\n"
                + "jmp_if_false,28,8,1\n"
                + "jmp,35,8,5\n"
                + "popc,1\n"
                + "jmp,30,8,1\n"
                + "jmp,32,8,6\n"
                + "popc,1\n"
                + "push,none:,-1,-1\n"
                + "popc,1\n"
                + "push,none:,-1,-1\n"
                + "popc,1\n"
                + "jmp,10,3,1\n"
                + "push,none:,-1,-1\n"
                + "\n";

        assertEquals(expected, ic.toString());
    }
}
