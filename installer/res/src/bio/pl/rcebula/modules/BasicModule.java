/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.List;
import pl.rcebula.Constants;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.ErrorCodes;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.MyError;

/**
 *
 * @author robert
 */
public class BasicModule extends Module
{
    @Override
    public String getName()
    {
        return "basic";
    }
    
    public BasicModule()
    {
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new AssignLocalFunction());
        putFunction(new AssignGlobalFunction());
        putFunction(new IsLocalFunction());
        putFunction(new IsGlobalFunction());
        putFunction(new GetGlobalFunction());
        putFunction(new ReturnFunction());
        
        putEvent(new OnUnhandledErrorEvent());
    }

    private class AssignLocalFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSIGN_LOCAL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            Data var = null;
            // parametry <id, var>+
            for (int i = 0; i < params.size(); i += 2)
            {
                String id = (String)params.get(i).getValue();
                var = params.get(i + 1);

                currentFrame.getLocalVariables().put(id, var);
            }

            return var;
        }
    }

    private class AssignGlobalFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSIGN_GLOBAL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            Data var = null;
            // parametry <id, var>+
            for (int i = 0; i < params.size(); i += 2)
            {
                String id = (String)params.get(i).getValue();
                var = params.get(i + 1);

                interpreter.getGlobalVariables().put(id, var);
            }

            return var;
        }
    }

    private class IsLocalFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_LOCAL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr id
            String id = (String)params.get(0).getValue();
            boolean isLocal = currentFrame.getLocalVariables().containsKey(id);
            return Data.createDataBool(isLocal);
        }
    }

    private class IsGlobalFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_GLOBAL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr id
            String id = (String)params.get(0).getValue();
            boolean isGlobal = interpreter.getGlobalVariables().containsKey(id);
            return Data.createDataBool(isGlobal);
        }
    }

    private class GetGlobalFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_GLOBAL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr id
            Data did = params.get(0);
            String id = (String)did.getValue();
            Data var = interpreter.getGlobalVariables().get(id);
            if (var == null)
            {
                String message = "there is no global variable " + id;
                MyError error = new MyError(getName(), message, ErrorCodes.NO_GLOBAL_VARIABLE.getCode(),
                        null, did.getErrorInfo(), interpreter);
                return Data.createDataError(error);
            }
            return new Data(var);
        }
    }

    private class ReturnFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return Constants.returnFunctionName;
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr var
            Data var = params.get(0);
            CallFrame cf = interpreter.popFrame();
            // w przypadku gdy wartość zwracana nie ma zostać zapisana na stosie wartości ramki poprzedniej
            // zwracamy wartość null
            if (cf.isReturnToCaller())
            {
                return var;
            }
            else
            {
                return null;
            }
        }
    }
    
    private class OnUnhandledErrorEvent implements IEvent
    {
        @Override
        public String getName()
        {
            return Constants.unhandledErrorFunctionName;
        }

        @Override
        public int getNumberOfParameters()
        {
            return 1;
        }
    }
}
