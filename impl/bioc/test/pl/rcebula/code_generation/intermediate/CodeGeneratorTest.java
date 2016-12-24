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

import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.error_report.ErrorInfo;
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
import pl.rcebula.error_report.MyFiles;

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
                add(new BuiltinFunction("FOR", null, true, ParamType.CALL, ParamType.ALL, ParamType.CALL));
                add(new BuiltinFunction("CALL", null, true,
                        Arrays.asList(ParamType.CALL, ParamType.CALL),
                        Arrays.asList(false, true)));
                add(new BuiltinFunction("DN", null, true));
                add(new BuiltinFunction("IF", null, true, ParamType.ALL, ParamType.CALL, ParamType.CALL));
                add(new BuiltinFunction("BREAK", null, true));
                add(new BuiltinFunction("CONTINUE", null, true));

                add(new BuiltinFunction("LS", null, false, ParamType.ALL, ParamType.ALL));
                add(new BuiltinFunction("GT", null, false, ParamType.ALL, ParamType.ALL));
                add(new BuiltinFunction("LE", null, false, ParamType.ALL, ParamType.ALL));
                add(new BuiltinFunction("ASSIGN_LOCAL", null, false, ParamType.ID, ParamType.ALL));
                add(new BuiltinFunction("PRINT", null, false, ParamType.ALL));
                add(new BuiltinFunction("DEC", null, false, ParamType.ID));
                add(new BuiltinFunction("INC", null, false, ParamType.ID));
                add(new BuiltinFunction("RETURN", null, false, ParamType.ALL));
                add(new BuiltinFunction("MUL", null, false, ParamType.ALL, ParamType.ALL));
                add(new BuiltinFunction("FOO", null, false,
                        Arrays.asList(ParamType.ID, ParamType.ID, ParamType.ALL, ParamType.ALL),
                        Arrays.asList(false, true, true, false)));
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

    private ErrorInfo generateErrorInfo(int lineNum, int chNum)
    {
        return new ErrorInfo(lineNum, chNum, new MyFiles.File(1, "test"));
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
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 1));
        Call call = new Call("foo", null, generateErrorInfo(2, 1));
        Call insideCall = new Call("foo", call, generateErrorInfo(2, 6));
        insideCall.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(2, 8)),
                generateErrorInfo(2, 10)));
        call.addCallParam(insideCall);
        uf.addCall(call);
        pt.addUserFunction(uf);

        uf = new UserFunction("foo", generateErrorInfo(4, 1));
        uf.addParam(new Param("x", generateErrorInfo(5, 1)));
        pt.addUserFunction(uf);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions, new MyFiles());
        IntermediateCode ic = cg.getIc();

        String expected = "foo,10,4,1,1\n"
                + "onSTART,3,1,1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,int:10,2,10,1\n"
                + "pop,1\n"
                + "call,foo,2,6,1\n"
                + "pop,1\n"
                + "call,foo,2,1,1\n"
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
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 1));

        // ASSIGN_LOCAL(x, foo(10))
        Call call = new Call("ASSIGN_LOCAL", null, generateErrorInfo(2, 1));
        IdCallParam icp = new IdCallParam("x", generateErrorInfo(2, 2));
        Call call2 = new Call("foo", call, generateErrorInfo(2, 3));
        ConstCallParam ccp = new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(2, 4)),
                generateErrorInfo(2, 4));
        call2.addCallParam(ccp);
        call.addCallParam(icp);
        call.addCallParam(call2);
        uf.addCall(call);

        // PRINT(x)
        call = new Call("PRINT", null, generateErrorInfo(3, 1));
        icp = new IdCallParam("x", generateErrorInfo(3, 2));
        call.addCallParam(icp);
        uf.addCall(call);

        pt.addUserFunction(uf);

        // def foo(p)
        uf = new UserFunction("foo", generateErrorInfo(4, 1));
        uf.addParam(new Param("p", generateErrorInfo(4, 2)));

        /*
        FOR(
                ASSIGN_LOCAL(sum, 0),
                LE(p, 0),
                DEC(p))
         */
        call = new Call("FOR", null, generateErrorInfo(5, 1));
        call.setRepeatCycles(1);

        // ASSIGN_LOCAL(sum, 0)
        call2 = new Call("ASSIGN_LOCAL", call, generateErrorInfo(6, 1));
        icp = new IdCallParam("sum", generateErrorInfo(6, 2));
        call2.addCallParam(icp);
        ccp = new ConstCallParam(new Token(TokenType.INT, 0, generateErrorInfo(6, 3)),
                generateErrorInfo(6, 3));
        call2.addCallParam(ccp);
        call.addCallParam(call2);

        // LE(p, 0),
        call2 = new Call("LE", call, generateErrorInfo(7, 1));
        icp = new IdCallParam("p", generateErrorInfo(7, 2));
        call2.addCallParam(icp);
        ccp = new ConstCallParam(new Token(TokenType.INT, 0, generateErrorInfo(7, 3)),
                generateErrorInfo(7, 3));
        call2.addCallParam(ccp);
        call.addCallParam(call2);

        // DEC(p)
        call2 = new Call("DEC", call, generateErrorInfo(8, 1));
        icp = new IdCallParam("p", generateErrorInfo(8, 2));
        call2.addCallParam(icp);
        call.addCallParam(call2);

        // DN()
        call2 = new Call("DN", call, generateErrorInfo(9, 1));
        call.addCallParam(call2);

        uf.addCall(call);

        // RETURN(sum)
        call = new Call("RETURN", null, generateErrorInfo(10, 1));
        icp = new IdCallParam("sum", generateErrorInfo(10, 2));
        call.addCallParam(icp);
        uf.addCall(call);

        pt.addUserFunction(uf);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions, new MyFiles());
        IntermediateCode ic = cg.getIc();

        String expected = "foo,15,4,1,1\n"
                + "onSTART,3,1,1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,id:x,2,2,1\n"
                + "push,int:10,2,4,1\n"
                + "pop,1\n"
                + "call,foo,2,3,1\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,2,1,1\n"
                + "clear_stack\n"
                + "push,var:x,3,2,1\n"
                + "pop,1\n"
                + "call_loc,PRINT,3,1,1\n"
                + "\n"
                + "foo,p\n"
                + "push,id:sum,6,2,1\n"
                + "push,int:0,6,3,1\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,6,1,1\n"
                + "popc,1\n"
                + "push,var:p,7,2,1\n"
                + "push,int:0,7,3,1\n"
                + "pop,2\n"
                + "call_loc,LE,7,1,1\n"
                + "pop,1\n"
                + "jmp_if_not_bool,35,5,1,1\n"
                + "jmp_if_false,37,5,1,1\n"
                + "push,id:p,8,2,1\n"
                + "pop,1\n"
                + "call_loc,DEC,8,1,1\n"
                + "popc,1\n"
                + "push,none:,-1,-1,-1\n"
                + "popc,1\n"
                + "jmp,21,5,1,1\n"
                + "push_error_bad_parameter_type_not_bool,FOR,2\n"
                + "jmp,38,5,1,1\n"
                + "push,none:,-1,-1,-1\n"
                + "clear_stack\n"
                + "push,var:sum,10,2,1\n"
                + "pop,1\n"
                + "call_loc,RETURN,10,1,1\n"
                + "\n";

        String res = ic.toString();
        assertEquals(expected, res);
    }

    @Test
    public void testNestedForLoops()
    {
        /*
        def onSTART()
            FOR(
                ASSIGN_LOCAL(i, 0),
                LS(i, 10),
                CALL(
                    FOR(
                        ASSIGN_LOCAL(j, 0),
                        LS(j, 10),
                        CALL(
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
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 1));
        pt.addUserFunction(uf);

        // FOR
        Call callFor = new Call("FOR", null, generateErrorInfo(2, 1));
        callFor.setRepeatCycles(1);
        uf.addCall(callFor);

        // ASSIGN_LOCAL(i, 0),
        Call call1 = new Call("ASSIGN_LOCAL", callFor, generateErrorInfo(3, 1));
        callFor.addCallParam(call1);
        call1.addCallParam(new IdCallParam("i", generateErrorInfo(3, 2)));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 0, generateErrorInfo(3, 3)),
                generateErrorInfo(3, 3)));

        // LS(i, 10),
        call1 = new Call("LS", callFor, generateErrorInfo(4, 1));
        callFor.addCallParam(call1);
        call1.addCallParam(new IdCallParam("i", generateErrorInfo(4, 2)));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(4, 3)),
                generateErrorInfo(4, 3)));

        // CALL2(
        Call call2 = new Call("CALL", callFor, generateErrorInfo(5, 1));
        callFor.addCallParam(call2);

        // FOR(
        Call insideFor = new Call("FOR", call2, generateErrorInfo(6, 1));
        insideFor.setRepeatCycles(1);
        call2.addCallParam(insideFor);

        // ASSIGN_LOCAL(j, 0),
        call1 = new Call("ASSIGN_LOCAL", insideFor, generateErrorInfo(7, 1));
        insideFor.addCallParam(call1);
        call1.addCallParam(new IdCallParam("j", generateErrorInfo(7, 2)));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 0, generateErrorInfo(7, 3)),
                generateErrorInfo(7, 3)));

        // LS(j, 10),
        call1 = new Call("LS", insideFor, generateErrorInfo(8, 1));
        insideFor.addCallParam(call1);
        call1.addCallParam(new IdCallParam("j", generateErrorInfo(8, 2)));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(8, 3)),
                generateErrorInfo(8, 3)));

        // CALL(
        Call insideCall2 = new Call("CALL", insideFor, generateErrorInfo(9, 1));
        insideFor.addCallParam(insideCall2);

        // PRINT(MUL(i, j)),
        call1 = new Call("PRINT", insideCall2, generateErrorInfo(10, 1));
        insideCall2.addCallParam(call1);
        Call call22 = new Call("MUL", call1, generateErrorInfo(10, 2));
        call1.addCallParam(call22);
        call22.addCallParam(new IdCallParam("i", generateErrorInfo(10, 3)));
        call22.addCallParam(new IdCallParam("j", generateErrorInfo(10, 4)));

        // INC(j)
        call1 = new Call("INC", insideCall2, generateErrorInfo(11, 1));
        insideCall2.addCallParam(call1);
        call1.addCallParam(new IdCallParam("j", generateErrorInfo(11, 2)));

        // DN()
        call1 = new Call("DN", insideFor, generateErrorInfo(12, 1));
        insideFor.addCallParam(call1);

        //  INC(i)
        call1 = new Call("INC", call2, generateErrorInfo(13, 1));
        call2.addCallParam(call1);
        call1.addCallParam(new IdCallParam("i", generateErrorInfo(13, 2)));

        // DN()
        call1 = new Call("DN", callFor, generateErrorInfo(14, 1));
        callFor.addCallParam(call1);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions, new MyFiles());
        IntermediateCode ic = cg.getIc();

        String expected = "onSTART,2,1,1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,id:i,3,2,1\n"
                + "push,int:0,3,3,1\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,3,1,1\n"
                + "popc,1\n"
                + "push,var:i,4,2,1\n"
                + "push,int:10,4,3,1\n"
                + "pop,2\n"
                + "call_loc,LS,4,1,1\n"
                + "pop,1\n"
                + "jmp_if_not_bool,52,2,1,1\n"
                + "jmp_if_false,54,2,1,1\n"
                + "push,id:j,7,2,1\n"
                + "push,int:0,7,3,1\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,7,1,1\n"
                + "popc,1\n"
                + "push,var:j,8,2,1\n"
                + "push,int:10,8,3,1\n"
                + "pop,2\n"
                + "call_loc,LS,8,1,1\n"
                + "pop,1\n"
                + "jmp_if_not_bool,41,6,1,1\n"
                + "jmp_if_false,43,6,1,1\n"
                + "push,var:i,10,3,1\n"
                + "push,var:j,10,4,1\n"
                + "pop,2\n"
                + "call_loc,MUL,10,2,1\n"
                + "pop,1\n"
                + "call_loc,PRINT,10,1,1\n"
                + "popc,1\n"
                + "push,id:j,11,2,1\n"
                + "pop,1\n"
                + "call_loc,INC,11,1,1\n"
                + "popc,1\n"
                + "push,none:,-1,-1,-1\n"
                + "popc,1\n"
                + "jmp,20,6,1,1\n"
                + "push_error_bad_parameter_type_not_bool,FOR,2\n"
                + "jmp,44,6,1,1\n"
                + "push,none:,-1,-1,-1\n"
                + "popc,1\n"
                + "push,id:i,13,2,1\n"
                + "pop,1\n"
                + "call_loc,INC,13,1,1\n"
                + "popc,1\n"
                + "push,none:,-1,-1,-1\n"
                + "popc,1\n"
                + "jmp,8,2,1,1\n"
                + "push_error_bad_parameter_type_not_bool,FOR,2\n"
                + "jmp,55,2,1,1\n"
                + "push,none:,-1,-1,-1\n"
                + "\n";

        String res = ic.toString();
        assertEquals(expected, res);
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
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 1));
        pt.addUserFunction(uf);

        // ASSIGN_LOCAL(i, 0),
        Call call1 = new Call("ASSIGN_LOCAL", null, generateErrorInfo(2, 1));
        uf.addCall(call1);
        call1.addCallParam(new IdCallParam("i", generateErrorInfo(2, 2)));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 0, generateErrorInfo(2, 3)),
                generateErrorInfo(2, 3)));

        // FOR(
        Call forCall = new Call("FOR", null, generateErrorInfo(3, 1));
        forCall.setRepeatCycles(1);
        uf.addCall(forCall);

        // DN(),
        call1 = new Call("DN", forCall, generateErrorInfo(4, 1));
        forCall.addCallParam(call1);

        // true,
        forCall.addCallParam(new ConstCallParam(new Token(TokenType.BOOL, true, generateErrorInfo(5, 1)),
                generateErrorInfo(5, 1)));

        // CALL2(
        Call call2 = new Call("CALL", forCall, generateErrorInfo(6, 1));
        forCall.addCallParam(call2);

        // PRINT(INC(i)),
        call1 = new Call("PRINT", call2, generateErrorInfo(7, 1));
        call2.addCallParam(call1);
        Call call22 = new Call("INC", call1, generateErrorInfo(7, 2));
        call1.addCallParam(call22);
        call22.addCallParam(new IdCallParam("i", generateErrorInfo(7, 3)));

        // IF(GT(i, 9), BREAK(), CONTINUE())
        Call ifCall = new Call("IF", call2, generateErrorInfo(8, 1));
        call2.addCallParam(ifCall);
        // GT(i, 9)
        call1 = new Call("GT", ifCall, generateErrorInfo(8, 2));
        ifCall.addCallParam(call1);
        call1.addCallParam(new IdCallParam("i", generateErrorInfo(8, 3)));
        call1.addCallParam(new ConstCallParam(new Token(TokenType.INT, 9, generateErrorInfo(8, 4)),
                generateErrorInfo(8, 4)));
        // BREAK()
        call1 = new Call("BREAK", ifCall, generateErrorInfo(8, 5));
        ifCall.addCallParam(call1);
        // CONTINUE()
        call1 = new Call("CONTINUE", ifCall, generateErrorInfo(8, 6));
        ifCall.addCallParam(call1);
        ifCall.setRepeatCycles(1);

        // DN()
        call1 = new Call("DN", forCall, generateErrorInfo(9, 1));
        forCall.addCallParam(call1);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions, new MyFiles());
        IntermediateCode ic = cg.getIc();

        String expected = "onSTART,2,1,1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,id:i,2,2,1\n"
                + "push,int:0,2,3,1\n"
                + "pop,2\n"
                + "call_loc,ASSIGN_LOCAL,2,1,1\n"
                + "clear_stack\n"
                + "push,none:,-1,-1,-1\n"
                + "popc,1\n"
                + "push,bool:true,5,1,1\n"
                + "pop,1\n"
                + "jmp_if_not_bool,39,3,1,1\n"
                + "jmp_if_false,41,3,1,1\n"
                + "push,id:i,7,3,1\n"
                + "pop,1\n"
                + "call_loc,INC,7,2,1\n"
                + "pop,1\n"
                + "call_loc,PRINT,7,1,1\n"
                + "popc,1\n"
                + "push,var:i,8,3,1\n"
                + "push,int:9,8,4,1\n"
                + "pop,2\n"
                + "call_loc,GT,8,2,1\n"
                + "pop,1\n"
                + "jmp_if_not_bool,34,8,1,1\n"
                + "jmp_if_false,30,8,1,1\n"
                + "jmp,41,8,5,1\n"
                + "popc,1\n"
                + "jmp,32,8,1,1\n"
                + "jmp,36,8,6,1\n"
                + "popc,1\n"
                + "push,none:,-1,-1,-1\n"
                + "jmp,35,8,1,1\n"
                + "push_error_bad_parameter_type_not_bool,IF,1\n"
                + "popc,1\n"
                + "push,none:,-1,-1,-1\n"
                + "popc,1\n"
                + "jmp,10,3,1,1\n"
                + "push_error_bad_parameter_type_not_bool,FOR,2\n"
                + "jmp,42,3,1,1\n"
                + "push,none:,-1,-1,-1\n"
                + "\n";
        
        String res = ic.toString();
        assertEquals(expected, res);
    }

    @Test
    public void testMultipleArgumentsBuiltinFunction()
    {
        System.out.println("testMultipleArgumentsBuiltinFunction()");

        /*
        def onSTART()
            FOO(x, y, 10, z, DN(), true)
        end
         */
        ProgramTree pt = new ProgramTree();

        // onSTART
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(-1, -1));
        Call call = new Call("FOO", null, generateErrorInfo(-1, -1));
        call.addCallParam(new IdCallParam("x", generateErrorInfo(-1, -1)));
        call.addCallParam(new IdCallParam("y", generateErrorInfo(-1, -1)));
        call.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(-1, -1)),
                generateErrorInfo(-1, -1)));
        call.addCallParam(new IdCallParam("z", generateErrorInfo(-1, -1)));
        call.addCallParam(new Call(SpecialFunctionsName.doNothingFunctionName, call, generateErrorInfo(-1, -1)));
        call.addCallParam(new ConstCallParam(new Token(TokenType.BOOL, true, generateErrorInfo(-1, -1)),
                generateErrorInfo(-1, -1)));
        call.setRepeatCycles(2);
        uf.addCall(call);
        pt.addUserFunction(uf);

        CodeGenerator cg = new CodeGenerator(pt, builtinFunctions, new MyFiles());
        IntermediateCode ic = cg.getIc();

        String expected = "onSTART,2,-1,-1,1\n"
                + "\n"
                + "onSTART\n"
                + "push,id:x,-1,-1,1\n"
                + "push,id:y,-1,-1,1\n"
                + "push,int:10,-1,-1,1\n"
                + "push,id:z,-1,-1,1\n"
                + "push,none:,-1,-1,-1\n"
                + "push,bool:true,-1,-1,1\n"
                + "pop,6\n"
                + "call_loc,FOO,-1,-1,1\n\n";

        assertEquals(expected, ic.toString());
    }
}
