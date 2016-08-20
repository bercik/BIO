/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.rcebula.Constants;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.Datas;
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
        putFunction(new AssertsOn());
        putFunction(new AssertsOff());
        putFunction(new AssertTrueFunction());
        putFunction(new AssertFalseFunction());
        putFunction(new AssertEqFunction());
        putFunction(new AssertNeqFunction());
        putFunction(new AssignLocalReturnIfError());
    }

    private class AssignLocalReturnIfError implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSIGN_LOCAL_RETURN_IF_ERROR";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <id, all>+
            for (int i = 0; i < params.size(); i += 2)
            {
                Data id = params.get(i);
                Data val = params.get(i + 1);
                
                // sprawdź czy val to nie error
                if (val.getDataType().equals(DataType.ERROR))
                {
                    // wywołaj funkcję RETURN z tą wartością i zwróc to co ona zwraca
                    return interpreter.getBuiltinFunctions().callFunction(Constants.returnFunctionName, 
                            Arrays.asList(val), currentFrame, interpreter, val.getErrorInfo());
                }
                
                // wywołaj funkcję ASSIGN_LOCAL
                interpreter.getBuiltinFunctions().callFunction("ASSIGN_LOCAL", Arrays.asList(id, val), currentFrame, 
                        interpreter, id.getErrorInfo());
            }
            
            return Data.createNoneData();
        }
    }
    
    private static boolean assertsOn = true;
    
    private enum AssertOperation
    {
        TRUE,
        FALSE,
        EQ,
        NEQ;
    }
    
    private class AssertNeqFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSERT_NEQ";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new AssertFunction().perform(params, interpreter, this, AssertOperation.NEQ);
        }
    }
    
    private class AssertEqFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSERT_EQ";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new AssertFunction().perform(params, interpreter, this, AssertOperation.EQ);
        }
    }
    
    private class AssertFalseFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSERT_FALSE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new AssertFunction().perform(params, interpreter, this, AssertOperation.FALSE);
        }
    }
    
    private class AssertTrueFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSERT_TRUE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new AssertFunction().perform(params, interpreter, this, AssertOperation.TRUE);
        }
    }
    
    private class AssertFunction
    {
        private String constructMessage(String funName, Data expected, Data actual)
        {
            String message = actual.getErrorInfo().toString();
            
            message += ": In function " + funName + " expected " + expected.getDataType().toString() + 
                    ": " + Datas.toStr(expected) + ", got " + actual.getDataType().toString() + ": " +
                    Datas.toStr(actual);
            
            return message;
        }
        
        private void printMessageExitProgram(Interpreter interpreter, String funName, 
                Data expected, Data actual)
        {
            // wypisz na ekran
            String message = constructMessage(funName, expected, actual);
            Data msg = Data.createStringData(message);
            interpreter.getBuiltinFunctions().callFunction("PRINTLN", Arrays.asList(msg), 
                    interpreter.getCurrentFrame(), interpreter, actual.getErrorInfo());
            // zakończ program
            interpreter.getBuiltinFunctions().callFunction("EXIT", new ArrayList<Data>(), 
                    interpreter.getCurrentFrame(), interpreter, actual.getErrorInfo());
        }
        
        private Data perform(List<Data> params, Interpreter interpreter, IFunction function, AssertOperation op)
        {
            // jeżeli asercję są włączone
            if (ErrorsModule.assertsOn)
            {
                if (op.equals(AssertOperation.TRUE) || op.equals(AssertOperation.FALSE))
                {
                    // parametr: <all>
                    Data par = params.get(0);

                    // sprawdź czy typu bool
                    TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, 
                            DataType.BOOL);
                    if (tc.isError())
                    {
                        return tc.getError();
                    }

                    boolean val = (boolean)par.getValue();
                    if (op.equals(AssertOperation.TRUE))
                    {
                        if (val != true)
                        {
                            // wypisz błąd i zakończ program
                            printMessageExitProgram(interpreter, function.getName(), Data.createBoolData(true), par);
                        }
                    }
                    else
                    {
                        if (val != false)
                        {
                            // wypisz błąd i zakończ program
                            printMessageExitProgram(interpreter, function.getName(), Data.createBoolData(false), par);
                        }
                    }
                }
                else if (op.equals(AssertOperation.EQ) || op.equals(AssertOperation.NEQ))
                {
                    // parametry: <all, all>
                    Data par1 = params.get(0);
                    Data par2 = params.get(1);
                    
                    String funName;
                    if (op.equals(AssertOperation.EQ))
                    {
                        funName = "EQ";
                    }
                    else
                    {
                        funName = "NEQ";
                    }
                    // wywołaj funkcję EQ lub NEQ
                    Data res = interpreter.getBuiltinFunctions().callFunction(funName, params, 
                            interpreter.getCurrentFrame(), interpreter, par1.getErrorInfo());
                    
                    // sprawdź czy nie error, jeżeli tak to zwróć
                    if (res.getDataType().equals(DataType.ERROR))
                    {
                        return res;
                    }
                    
                    // sprawdź czy false, jeżeli tak to wypisz wiadomość i przerwij program
                    boolean bval = (boolean)res.getValue();
                    if (!bval)
                    {
                        printMessageExitProgram(interpreter, function.getName(), par1, par2);
                    }
                }
            }
            
            return Data.createNoneData();
        }
    }
    
    private class AssertsOn implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSERTS_ON";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // bez parametrów
            ErrorsModule.assertsOn = true;
            
            return Data.createNoneData();
        }
    }
    
    private class AssertsOff implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSERTS_OFF";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // bez parametrów
            ErrorsModule.assertsOn = false;
            
            return Data.createNoneData();
        }
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
