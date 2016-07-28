/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.List;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.data_types.Tuple;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.modules.utils.Collections;
import pl.rcebula.modules.utils.ErrorCodes;
import pl.rcebula.modules.utils.type_checker.TypeChecker;

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
        putFunction(new GetFunction());
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
    
    private class GetFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);
            
            // sprawdź czy pierwszy parametr to kolekcja
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // sprawdź czy drugi parametr jest typu int
            tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            Data[] datas = Collections.getDatas(par1);
            int index = (int)par2.getValue();
            // sprawdź czy index nie wykracza poza granicę datas
            if (index < 0 || index >= datas.length)
            {
                String message = "Index " + index + " out of collection bounds";
                MyError error = new MyError(message, ErrorCodes.INDEX_OUT_OF_BOUNDS.getCode(), null, 
                        par2.getErrorInfo(), interpreter);
                return Data.createErrorData(error);
            }
            // zwróc element o indeksie index z kolekcji datas
            return datas[index];
        }
    }
}
