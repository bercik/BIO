/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.ErrorCodes;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.MyError;

/**
 *
 * @author robert
 */
public class BuiltinFunctions
{
    private final Map<String, IFunction> functions = new HashMap<>();
    private final List<IModule> modules = new ArrayList<>();

    public BuiltinFunctions()
    {
        modules.add(new BasicModule());
        modules.add(new IoModule());
        
        getFunctionsFromModules();
    }
    
    private void getFunctionsFromModules()
    {
        for (IModule m : modules)
        {
            functions.putAll(m.getFunctions());
        }
    }
    
    public Data callFunction(String name, List<Data> params, CallFrame currentFrame, Interpreter interpreter,
            int line, int chNum)
    {
        IFunction fun = functions.get(name);
        if (fun != null)
        {
            return fun.call(params, currentFrame, interpreter);
        }
        else
        {
            String message = "Builtin function " + name + " is not implemented. Please contact interpreter creator";
            MyError myError = new MyError(message, 
                    ErrorCodes.BUILTIN_FUNCTION_NOT_IMPLEMENTED.getCode(), null, line, chNum, interpreter);
            return Data.createDataError(myError);
        }
    }
}
