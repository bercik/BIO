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
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.modules.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class ErrorsModule extends Module
{
    @Override
    public String getName()
    {
        return "errors";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new GetErrorFullInformationFunction());
    }
    
    private class GetErrorFullInformationFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_ERROR_FULL_INFO";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <error>
            Data err = params.get(0);
            TypeChecker tc = new TypeChecker(err, getName(), 0, err.getErrorInfo(), interpreter, DataType.ERROR);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            MyError myError = (MyError)err.getValue();
            String str = myError.getFullInfo();
            
            return Data.createStringData(str);
        }
    }
}
