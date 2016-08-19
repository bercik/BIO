/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.Arrays;
import java.util.List;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.type_checker.TypeChecker;

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
        putFunction(new PrintStackTraceFunction());
        putFunction(new CreateErrorFunction());
        putFunction(new GetErrorMessage());
        putFunction(new GetErrorObject());
        putFunction(new GetErrorCause());
        putFunction(new GetErrorLine());
        putFunction(new GetErrorCh());
    }
    
    private class GetErrorCh implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_ERROR_CH";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data err = params.get(0);
            
            // sprawdź czy typu error
            TypeChecker tc = new TypeChecker(err, getName(), 0, err.getErrorInfo(), interpreter, DataType.ERROR);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz error
            MyError me = (MyError)err.getValue();
            // pobierz line
            Data chNum = Data.createIntData(me.getErrorInfo().getChNum());
            
            // zwróć
            return chNum;
        }
    }
    
    private class GetErrorLine implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_ERROR_LINE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data err = params.get(0);
            
            // sprawdź czy typu error
            TypeChecker tc = new TypeChecker(err, getName(), 0, err.getErrorInfo(), interpreter, DataType.ERROR);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz error
            MyError me = (MyError)err.getValue();
            // pobierz line
            Data line = Data.createIntData(me.getErrorInfo().getLineNum());
            
            // zwróć
            return line;
        }
    }
    
    private class GetErrorCause implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_ERROR_CAUSE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data err = params.get(0);
            
            // sprawdź czy typu error
            TypeChecker tc = new TypeChecker(err, getName(), 0, err.getErrorInfo(), interpreter, DataType.ERROR);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz error
            MyError me = (MyError)err.getValue();
            // pobierz cause
            Data cause = Data.createErrorData(me.getCause());
            
            // zwróć
            return cause;
        }
    }
    
    private class GetErrorObject implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_ERROR_OBJECT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data err = params.get(0);
            
            // sprawdź czy typu error
            TypeChecker tc = new TypeChecker(err, getName(), 0, err.getErrorInfo(), interpreter, DataType.ERROR);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz error
            MyError me = (MyError)err.getValue();
            // pobierz obj
            Data obj = me.getObject();
            
            // zwróć
            return obj;
        }
    }
    
    private class GetErrorMessage implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_ERROR_MESSAGE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data err = params.get(0);
            
            // sprawdź czy typu error
            TypeChecker tc = new TypeChecker(err, getName(), 0, err.getErrorInfo(), interpreter, DataType.ERROR);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz error
            MyError me = (MyError)err.getValue();
            // pobierz msg
            String msg = me.getMessage();
            
            // zwróć
            return Data.createStringData(msg);
        }
    }
    
    private class PrintStackTraceFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "PRINT_STACK_TRACE";
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
            String str = myError.getFirstCauseStackTrace();
            Data dstr = Data.createStringData(str);
            
            interpreter.getBuiltinFunctions().callFunction("PRINTLN", Arrays.asList(dstr), currentFrame, interpreter, 
                    err.getErrorInfo());
            
            return dstr;
        }
    }
    
    private class CreateErrorFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CREATE_ERROR";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, all>
            Data msg = params.get(0);
            Data obj = params.get(1);
            Data cause = params.get(2);
            
            // sprawdź czy msg to string
            TypeChecker tc = new TypeChecker(msg, getName(), 0, msg.getErrorInfo(), interpreter, 
                    DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // sprawdź czy obj to nie error
            tc = new TypeChecker(obj, getName(), 1, obj.getErrorInfo(), interpreter, DataType.ARRAY, 
                    DataType.BOOL, DataType.DICT, DataType.FLOAT, DataType.INT, DataType.NONE, 
                    DataType.STRING, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // sprawdź czy cause to error lub none
            tc = new TypeChecker(cause, getName(), 2, cause.getErrorInfo(), interpreter, DataType.ERROR, 
                    DataType.NONE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz wartości
            String smsg = (String)msg.getValue();
            MyError meCause = null;
            // jeżeli cause to nie none to pobieramy wartość
            if (!cause.getDataType().equals(DataType.NONE))
            {
                meCause = (MyError)cause.getValue();
            }
            
            MyError error = new MyError(smsg, obj, meCause, interpreter);
            return Data.createErrorData(error);
        }
    }
}
