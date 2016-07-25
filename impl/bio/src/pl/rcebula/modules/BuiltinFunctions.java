/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.rcebula.error_report.ErrorInfo;
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
    private final Map<String, IModule> modules = new HashMap<>();

    public BuiltinFunctions(List<String> modulesName)
    {
        putModule(new BasicModule());
        putModule(new IoModule());
        
        createFunctionsInModules(modulesName);
        getFunctionsFromModules();
    }
    
    private void putModule(IModule module)
    {
        modules.put(module.getName(), module);
    }
    
    private void createFunctionsInModules(List<String> modulesName)
    {
        for (String moduleName : modulesName)
        {
            IModule module = modules.get(moduleName);
            
            if (module != null)
            {
                module.createFunctions();
            }
            else
            {
                String message = "There is no implementation for module " + moduleName;
                throw new RuntimeException(message);
            }
        }
    }
    
    private void getFunctionsFromModules()
    {
        for (Map.Entry<String, IModule> set : modules.entrySet())
        {
            IModule m = set.getValue();
            functions.putAll(m.getFunctions());
        }
    }
    
    public Data callFunction(String name, List<Data> params, CallFrame currentFrame, Interpreter interpreter,
            ErrorInfo ei)
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
                    ErrorCodes.BUILTIN_FUNCTION_NOT_IMPLEMENTED.getCode(), null, ei, interpreter);
            return Data.createDataError(myError);
        }
    }
}
