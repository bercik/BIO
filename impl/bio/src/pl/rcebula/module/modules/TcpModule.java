/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.List;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;

/**
 *
 * @author robert
 */
public class TcpModule extends Module
{
    @Override
    public String getName()
    {
        return "tcp";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private class TcpListenFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TCP_LISTEN";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
