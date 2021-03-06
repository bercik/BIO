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
package pl.rcebula.analysis.semantic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.Constants;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.IdCallParam;
import pl.rcebula.analysis.tree.Param;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.UserFunction;
import pl.rcebula.code_generation.intermediate.SpecialFunctionsName;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class SemanticCheckerTest
{

    public SemanticCheckerTest()
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

    private ErrorInfo generateErrorInfo(int lineNum, int chNum)
    {
        return new ErrorInfo(lineNum, chNum, new MyFiles.File(1, "test"));
    }

    @Test
    public void testMultipleParametersName()
            throws Exception
    {
        System.out.println("testMultipleParametersName()");

        UserFunction uf = new UserFunction("foo", generateErrorInfo(1, 1));
        uf.addParam(new Param("a", generateErrorInfo(1, 5)));
        uf.addParam(new Param("b", generateErrorInfo(1, 8)));
        uf.addParam(new Param("a", generateErrorInfo(1, 11)));

        ProgramTree pt = new ProgramTree();
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            catched = true;
            System.err.println(ex.getMessage());
        }

        assertEquals(true, catched);
    }

    @Test
    public void testUserFunctionRedeclared()
    {
        System.out.println("testUserFunctionRedeclared()");

        ProgramTree pt = new ProgramTree();

        Call c = new Call(SpecialFunctionsName.doNothingFunctionName, null, generateErrorInfo(2, 1));

        UserFunction uf = new UserFunction("foo", generateErrorInfo(1, 1));
        uf.addCall(c);
        pt.addUserFunction(uf);

        uf = new UserFunction("foo2", generateErrorInfo(2, 1));
        uf.addCall(c);
        pt.addUserFunction(uf);

        uf = new UserFunction("foo", generateErrorInfo(3, 1));
        uf.addCall(c);
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testBuiltinFunctionRedeclared()
    {
        System.out.println("testBuiltinFunctionRedeclared()");

        ProgramTree pt = new ProgramTree();

        Call c = new Call(SpecialFunctionsName.doNothingFunctionName, null, generateErrorInfo(2, 1));

        UserFunction uf = new UserFunction("foo", generateErrorInfo(1, 1));
        uf.addCall(c);
        pt.addUserFunction(uf);

        uf = new UserFunction("ASSIGN_LOCAL", generateErrorInfo(2, 1));
        uf.addCall(c);
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        BuiltinFunction bf = new BuiltinFunction("ASSIGN_LOCAL", null, false);
        builtinFunctions.add(bf);

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testMainFunctionExists()
    {
        System.out.println("testMainFunctionExists()");

        ProgramTree pt = new ProgramTree();

        Call c = new Call(SpecialFunctionsName.doNothingFunctionName, null, generateErrorInfo(2, 1));

        UserFunction uf = new UserFunction("foo", generateErrorInfo(1, 1));
        uf.addCall(c);
        pt.addUserFunction(uf);

        uf = new UserFunction("foo2", generateErrorInfo(2, 1));
        uf.addCall(c);
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testMainFunctionGoodNumberOfParameters()
    {
        System.out.println("testMainFunctionGoodNumberOfParameters()");

        ProgramTree pt = new ProgramTree();

        Call c = new Call(SpecialFunctionsName.doNothingFunctionName, null, generateErrorInfo(2, 1));

        UserFunction uf = new UserFunction("foo", generateErrorInfo(1, 1));
        uf.addCall(c);
        pt.addUserFunction(uf);

        uf = new UserFunction(Constants.mainFunctionName, generateErrorInfo(2, 1));
        uf.addCall(c);
        uf.addParam(new Param("a", generateErrorInfo(2, 10)));
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testFunctionDoesntExist()
    {
        System.out.println("testFunctionDoesntExist()");

        // def onSTART()
        //     foo2()
        // end
        ProgramTree pt = new ProgramTree();
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 1));
        Call call = new Call("foo2", null, generateErrorInfo(2, 1));
        uf.addCall(call);
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testPassingArgumentsToUserFunction()
    {
        System.out.println("testPassingArgumentsToUserFunction()");

        // def onSTART()
        //     foo(10)
        // end
        // def foo(p1, p2)
        // end
        ProgramTree pt = new ProgramTree();

        // onSTART
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 1));
        Call call = new Call("foo", null, generateErrorInfo(2, 1));
        call.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(2, 10)),
                generateErrorInfo(2, 10)));
        uf.addCall(call);
        pt.addUserFunction(uf);

        Call c = new Call(SpecialFunctionsName.doNothingFunctionName, null, generateErrorInfo(2, 1));

        // foo
        uf = new UserFunction("foo", generateErrorInfo(4, 1));
        uf.addCall(c);
        uf.addParam(new Param("p1", generateErrorInfo(4, 2)));
        uf.addParam(new Param("p2", generateErrorInfo(4, 5)));
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testPassingArgumentsToBuiltinFunction()
    {
        System.out.println("testPassingArgumentsToBuiltinFunction()");

        // def onSTART()
        //     FOR(DN(), flaga, 10)
        // end
        ProgramTree pt = new ProgramTree();

        // onSTART
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 1));
        Call call = new Call(SpecialFunctionsName.forLoopFunctionName, null, generateErrorInfo(2, 1));
        call.addCallParam(new Call(SpecialFunctionsName.doNothingFunctionName, call, generateErrorInfo(2, 5)));
        call.addCallParam(new IdCallParam("flaga", generateErrorInfo(2, 10)));
        call.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(2, 15)),
                generateErrorInfo(2, 15)));
        uf.addCall(call);
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        builtinFunctions.add(new BuiltinFunction(SpecialFunctionsName.forLoopFunctionName, null, true, 
                ParamType.CALL, ParamType.ALL, ParamType.CALL));
        builtinFunctions.add(new BuiltinFunction(SpecialFunctionsName.doNothingFunctionName, null, true));

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testBreakInsideForLoop()
    {
        System.out.println("testBreakInsideForLoop()");

        // def onSTART()
        //     FOR(DN(), flaga, 10)
        //     BREAK()
        // end
        ProgramTree pt = new ProgramTree();

        // onSTART
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(1, 1));
        Call call = new Call(SpecialFunctionsName.forLoopFunctionName, null, generateErrorInfo(2, 1));
        call.addCallParam(new Call(SpecialFunctionsName.doNothingFunctionName, call, generateErrorInfo(2, 5)));
        call.addCallParam(new IdCallParam("flaga", generateErrorInfo(2, 10)));
        call.addCallParam(new Call(SpecialFunctionsName.doNothingFunctionName, call, generateErrorInfo(2, 15)));
        uf.addCall(call);

        call = new Call(SpecialFunctionsName.breakFunctionName, null, generateErrorInfo(3, 1));
        uf.addCall(call);

        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        builtinFunctions.add(new BuiltinFunction(SpecialFunctionsName.forLoopFunctionName, null, true, 
                ParamType.CALL, ParamType.ALL, ParamType.CALL));
        builtinFunctions.add(new BuiltinFunction(SpecialFunctionsName.doNothingFunctionName, null, true));
        builtinFunctions.add(new BuiltinFunction(SpecialFunctionsName.breakFunctionName, null, true));

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testPassingMultipleArgumentsToBuiltinFunction()
    {
        System.out.println("testPassingMultipleArgumentsToBuiltinFunction()");

        // def onSTART()
        //     FOO(x, y, 10, 20, DN(), true)
        // end
        ProgramTree pt = new ProgramTree();

        // onSTART
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(-1, -1));
        Call call = new Call("FOO", null, generateErrorInfo(-1, -1));
        call.addCallParam(new IdCallParam("x", generateErrorInfo(-1, -1)));
        call.addCallParam(new IdCallParam("y", generateErrorInfo(-1, -1)));
        call.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(-1, -1)),
                generateErrorInfo(-1, -1)));
        call.addCallParam(new ConstCallParam(new Token(TokenType.INT, 20, generateErrorInfo(-1, -1)),
                generateErrorInfo(-1, -1)));
        call.addCallParam(new Call(SpecialFunctionsName.doNothingFunctionName, call, generateErrorInfo(-1, -1)));
        call.addCallParam(new ConstCallParam(new Token(TokenType.BOOL, true, generateErrorInfo(-1, -1)),
                generateErrorInfo(-1, -1)));
        uf.addCall(call);
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        builtinFunctions.add(new BuiltinFunction("FOO", null, false,
                Arrays.asList(ParamType.ID, ParamType.ID, ParamType.ALL, ParamType.ALL),
                Arrays.asList(false, true, true, false)));
        builtinFunctions.add(new BuiltinFunction(SpecialFunctionsName.doNothingFunctionName, null, true));

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }

    @Test
    public void testPassingMultipleArgumentsToBuiltinFunction2()
    {
        System.out.println("testPassingMultipleArgumentsToBuiltinFunction()");

        // def onSTART()
        //     FOO(x, y, 10, 20, z)
        // end
        ProgramTree pt = new ProgramTree();

        // onSTART
        UserFunction uf = new UserFunction("onSTART", generateErrorInfo(-1, -1));
        Call call = new Call("FOO", null, generateErrorInfo(-1, -1));
        call.addCallParam(new IdCallParam("x", generateErrorInfo(-1, -1)));
        call.addCallParam(new IdCallParam("y", generateErrorInfo(-1, -1)));
        call.addCallParam(new ConstCallParam(new Token(TokenType.INT, 10, generateErrorInfo(-1, -1)), 
                generateErrorInfo(-1, -1)));
        call.addCallParam(new ConstCallParam(new Token(TokenType.INT, 20, generateErrorInfo(-1, -1)), 
                generateErrorInfo(-1, -1)));
        call.addCallParam(new IdCallParam("z", generateErrorInfo(-1, -1)));
        uf.addCall(call);
        pt.addUserFunction(uf);

        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        builtinFunctions.add(new BuiltinFunction("FOO", null, false,
                Arrays.asList(ParamType.ID, ParamType.ID, ParamType.ALL, ParamType.ALL),
                Arrays.asList(false, true, true, false)));
        builtinFunctions.add(new BuiltinFunction(SpecialFunctionsName.doNothingFunctionName, null, true));

        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions, new MyFiles());
        }
        catch (SemanticError ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertEquals(true, catched);
    }
}
