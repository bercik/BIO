/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.List;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.Tuple;
import pl.rcebula.internals.interpreter.Interpreter;

/**
 *
 * @author robert
 */
public class ArraysModule extends Module
{
    @Override
    public String getName()
    {
        return "arrays";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new CreateTupleFunction());
    }
    
    private class CreateTupleFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CREATE_TUPLE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return Data.createTupleData(new Tuple(params.toArray(new Data[0])));
        }
    }
}
