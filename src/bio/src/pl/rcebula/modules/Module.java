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
    private final Map<String, IFunction> functionsMap = new HashMap<>();
    private final Map<String, IEvent> eventsMap = new HashMap<>();
    
    protected void putFunction(IFunction fun)
    {
        functionsMap.put(fun.getName(), fun);
    }
    
    protected void putEvent(IEvent event)
    {
        eventsMap.put(event.getName(), event);
    }

    @Override
    public Map<String, IFunction> getFunctions()
    {
        return functionsMap;
    }

    @Override
    public Map<String, IEvent> getEvents()
    {
        return eventsMap;
    }
}
