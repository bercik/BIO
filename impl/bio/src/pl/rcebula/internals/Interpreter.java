/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.modules.BuiltinFunctions;
import pl.rcebula.tools.IProfiler;
import pl.rcebula.utils.TimeProfiler;

/**
 *
 * @author robert
 */
public class Interpreter
{
    // logger
    Logger logger = Logger.getGlobal();
    
    // time profiler
    private final TimeProfiler timeProfiler;
    // profiler
    private final IProfiler profiler;
    // stos ramek wywołań
    private final Stack<CallFrame> frameStack = new Stack<>();
    // zmienne globalne
    private final Map<String, Data> globalVariables = new HashMap<>();
    // funkcje użytkownika 
    private final Map<String, UserFunction> userFunctions;
    // funkcje wbudowane
    private final BuiltinFunctions builtinFunctions;

    public Interpreter(Map<String, UserFunction> userFunctions, BuiltinFunctions builtinFunctions, 
            TimeProfiler timeProfiler, IProfiler profiler)
    {
        logger.info("Interpreter");
        
        this.userFunctions = userFunctions;
        this.builtinFunctions = builtinFunctions;
        this.timeProfiler = timeProfiler;
        this.profiler = profiler;
    }

    public Map<String, Data> getGlobalVariables()
    {
        return globalVariables;
    }

    public Stack<CallFrame> getFrameStack()
    {
        return frameStack;
    }

    public BuiltinFunctions getBuiltinFunctions()
    {
        return builtinFunctions;
    }
}
