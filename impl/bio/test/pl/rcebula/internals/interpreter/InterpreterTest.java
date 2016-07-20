/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.Constants;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code.ParamType;
import pl.rcebula.intermediate_code.Param;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.intermediate_code.line.CallLine;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.intermediate_code.line.PopLine;
import pl.rcebula.intermediate_code.line.PushLine;
import pl.rcebula.modules.BuiltinFunctions;
import pl.rcebula.tools.NullProfiler;
import pl.rcebula.utils.TimeProfiler;

/**
 *
 * @author robert
 */
public class InterpreterTest
{
    private static final BuiltinFunctions bf = new BuiltinFunctions();
    
    public InterpreterTest()
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
    public void testSimpleAssign()
    {
        Map<String, UserFunction> userFunctions = new HashMap<>();
        UserFunction uf = new UserFunction("onSTART", 0, 0);
        Line line = new PushLine(InterpreterFunction.PUSH, new Param(ParamType.ID, "x"), 1, 1);
        uf.addLine(line);
        line = new PushLine(InterpreterFunction.PUSH, new Param(ParamType.INT, 10), 2, 2);
        uf.addLine(line);
        line = new PopLine(InterpreterFunction.POP, 2, 3, 3);
        uf.addLine(line);
        line = new CallLine(InterpreterFunction.CALL_LOC, "ASSIGN_LOCAL", 4, 4);
        uf.addLine(line);
        line = new PopLine(InterpreterFunction.POP, 1, 5, 5);
        uf.addLine(line);
        line = new CallLine(InterpreterFunction.CALL_LOC, Constants.returnFunctionName, 6, 6);
        uf.addLine(line);
        
        userFunctions.put("onSTART", uf);
        
        TimeProfiler tp = new TimeProfiler();
        tp.startTotal();
        Interpreter interpreter = new Interpreter(new String[0], userFunctions, bf, tp, new NullProfiler());
        tp.stopTotal();
        
        System.out.println(tp.toString());
    }
}
