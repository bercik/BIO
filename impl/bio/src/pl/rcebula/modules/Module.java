/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author robert
 */
public abstract class Module implements IModule
{
    private final Map<String, IFunction> map = new HashMap<>();
    
    protected void putFunction(IFunction fun)
    {
        map.put(fun.getName(), fun);
    }

    @Override
    public Map<String, IFunction> getFunctions()
    {
        return map;
    }
}
