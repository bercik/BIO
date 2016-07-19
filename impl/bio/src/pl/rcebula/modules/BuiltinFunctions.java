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
import pl.rcebula.internals.Interpreter;
import pl.rcebula.internals.data_types.Data;

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
        
        getFunctionsFromModules();
    }
    
    private void getFunctionsFromModules()
    {
        for (IModule m : modules)
        {
            functions.putAll(m.getFunctions());
        }
    }
    
    public IFunction getFunction(String name)
    {
        return functions.get(name);
    }
    
    public Data callFunction(String name, List<Data> params, CallFrame currentFrame, Interpreter interpreter)
    {
        IFunction fun = getFunction(name);
        return fun.call(params, currentFrame, interpreter);
    }
}
