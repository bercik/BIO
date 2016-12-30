/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.Arrays;
import java.util.List;
import pl.rcebula.Constants;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.module.utils.error_codes.ErrorCodes;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.data_types.Struct;
import pl.rcebula.module.IEvent;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;
import pl.rcebula.utils.Pair;
import pl.rcebula.utils.Triple;

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
        putFunction(new AssignStaticFunction());
        putFunction(new FirstAssignLocalFunction());
        putFunction(new FirstAssignGlobalFunction());
        putFunction(new FirstAssignStaticFunction());
        putFunction(new IsLocalFunction());
        putFunction(new IsGlobalFunction());
        putFunction(new IsStaticFunction());
        putFunction(new GetGlobalFunction());
        putFunction(new GetLocalFunction());
        putFunction(new GetStaticFunction());
        putFunction(new ClearStaticFunction());
        putFunction(new ReturnFunction());
        putFunction(new ExitFunction());

        putEvent(new OnUnhandledErrorEvent());
    }

    private class ClearStaticFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CLEAR_STATIC";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, ErrorInfo callErrorInfo)
        {
            // parametr: <id>?
            UserFunction uf = currentFrame.getUserFunction();

            // jeżeli przekazano opcjonalny parametr
            if (params.size() > 0)
            {
                Data dFunName = params.get(0);
                String funName = (String)dFunName.getValue();

                uf = interpreter.getUserFunctions().get(funName);
                if (uf == null)
                {
                    return ErrorConstruct.USER_FUNCTION_DOESNT_EXIST(getName(), dFunName.getErrorInfo(),
                            interpreter, funName);
                }
            }

            uf.getStaticVariables().clear();

            return Data.createNoneData();
        }
    }

    private class AssignStaticFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ASSIGN_STATIC";
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

                Pair<String, Data> p = Struct.create(id, var, currentFrame.getUserFunction().getStaticVariables());
                id = p.getLeft();
                var = p.getRight();

                currentFrame.getUserFunction().getStaticVariables().put(id, var);
            }

            return Data.createNoneData();
        }
    }

    private class FirstAssignLocalFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "FIRST_ASSIGN_LOCAL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, 
                ErrorInfo callErrorInfo)
        {
            return FirstAssign.perform(params, currentFrame, interpreter, FirstAssign.Scope.LOCAL);
        }
    }
    
    private class FirstAssignGlobalFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "FIRST_ASSIGN_GLOBAL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, 
                ErrorInfo callErrorInfo)
        {
            return FirstAssign.perform(params, currentFrame, interpreter, FirstAssign.Scope.GLOBAL);
        }
    }
    
    private class FirstAssignStaticFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "FIRST_ASSIGN_STATIC";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, 
                ErrorInfo callErrorInfo)
        {
            return FirstAssign.perform(params, currentFrame, interpreter, FirstAssign.Scope.STATIC);
        }
    }
    
    private static class FirstAssign
    {
        private enum Scope
        {
            STATIC,
            LOCAL,
            GLOBAL;
        }

        private static Data perform(List<Data> params, CallFrame currentFrame, Interpreter interpreter, 
                Scope scope)
        {
            String isFunName;
            String assignFunName;

            switch (scope)
            {
                case GLOBAL:
                    isFunName = "IS_GLOBAL";
                    assignFunName = "ASSIGN_GLOBAL";
                    break;
                case STATIC:
                    isFunName = "IS_STATIC";
                    assignFunName = "ASSIGN_STATIC";
                    break;
                case LOCAL:
                    isFunName = "IS_LOCAL";
                    assignFunName = "ASSIGN_LOCAL";
                    break;
                default:
                    throw new RuntimeException("Don't know what to do with " + scope.toString());
            }

            // iteruj po wszystkich parametrach
            for (int i = 0; i < params.size(); i += 2)
            {
                Data did = params.get(i);
                Data dval = params.get(i + 1);

                // sprawdź czy istnieje
                Data dexists = interpreter.getBuiltinFunctions().callFunction(isFunName, Arrays.asList(did), 
                        currentFrame, interpreter, did.getErrorInfo());
                boolean exists = (boolean)dexists.getValue();
                
                // jeżeli nie istnieje
                if (!exists)
                {
                    // wywołaj funkcję assign
                    interpreter.getBuiltinFunctions().callFunction(assignFunName, Arrays.asList(did, dval), 
                            currentFrame, interpreter, did.getErrorInfo());
                }
            }
            
            // zwróć none
            return Data.createNoneData();
        }
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

                Pair<String, Data> p = Struct.create(id, var, currentFrame.getLocalVariables());
                id = p.getLeft();
                var = p.getRight();

                currentFrame.getLocalVariables().put(id, var);
            }

            return Data.createNoneData();
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

                Pair<String, Data> p = Struct.create(id, var, interpreter.getGlobalVariables());
                id = p.getLeft();
                var = p.getRight();

                interpreter.getGlobalVariables().put(id, var);
            }

            return Data.createNoneData();
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
            boolean isLocal = Struct.exists(id, currentFrame.getLocalVariables());
            return Data.createBoolData(isLocal);
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
            boolean isGlobal = Struct.exists(id, interpreter.getGlobalVariables());
            return Data.createBoolData(isGlobal);
        }
    }

    private class IsStaticFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_STATIC";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: id, <id>?
            String id = (String)params.get(0).getValue();

            UserFunction uf = currentFrame.getUserFunction();

            // jeżeli podano opcjonalny parametr to szukamy w funkcji podanej jako parametr
            if (params.size() > 1)
            {
                Data dFunName = params.get(1);
                String funName = (String)dFunName.getValue();

                uf = interpreter.getUserFunctions().get(funName);
                if (uf == null)
                {
                    return ErrorConstruct.USER_FUNCTION_DOESNT_EXIST(getName(), dFunName.getErrorInfo(),
                            interpreter, funName);
                }
            }

            boolean isStatic = Struct.exists(id, uf.getStaticVariables());
            return Data.createBoolData(isStatic);
        }
    }

    private class GetLocalFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_LOCAL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // param: <id>
            Data data = params.get(0);
            String id = (String)data.getValue();
            ErrorInfo ei = data.getErrorInfo();
            // pobierz zmienną lokalną
            Triple<Data, String, MyError> triple = Struct.get(id, interpreter.getCurrentFrame().getLocalVariables());
            data = triple.getFirst();
            // jeżeli istnieje to utwórz nową zmienną i ustaw jej linię i znak
            if (data != null)
            {
//                data = new Data(data);
                data.setErrorInfo(ei);
            }
            // jeżeli nie istnieje to utwórz zmienną błędu zamiast niej
            else
            {
                String message = "There is no local variable " + id + ". " + triple.getSecond();
                MyError cause = triple.getThird();
                MyError myError = new MyError(message, ErrorCodes.NO_LOCAL_VARIABLE.getCode(),
                        cause, ei, interpreter);
                data = Data.createErrorData(myError);
            }

            return data;
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
            // pobierz zmienną globalną
            Triple<Data, String, MyError> triple = Struct.get(id, interpreter.getGlobalVariables());
            Data var = triple.getFirst();
            // jeżeli nie istnieje
            if (var == null)
            {
                String message = "there is no global variable " + id + ". " + triple.getSecond();
                MyError cause = triple.getThird();
                MyError error = new MyError(getName(), message, ErrorCodes.NO_GLOBAL_VARIABLE.getCode(),
                        cause, did.getErrorInfo(), interpreter);
                return Data.createErrorData(error);
            }
            return new Data(var);
        }
    }

    private class GetStaticFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_STATIC";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: id, <id>?
            Data did = params.get(0);
            String id = (String)did.getValue();

            UserFunction uf = currentFrame.getUserFunction();
            Data dFunName = null;
            String funName = null;

            // jeżeli podano opcjonalny parametr
            if (params.size() > 1)
            {
                dFunName = params.get(1);
                funName = (String)dFunName.getValue();

                uf = interpreter.getUserFunctions().get(funName);
                if (uf == null)
                {
                    return ErrorConstruct.USER_FUNCTION_DOESNT_EXIST(getName(), dFunName.getErrorInfo(),
                            interpreter, funName);
                }
            }

            // pobierz zmienną statyczną
            Triple<Data, String, MyError> triple = Struct.get(id, uf.getStaticVariables());
            Data var = triple.getFirst();
            // jeżeli nie istnieje
            if (var == null)
            {
                String message = "there is no static variable " + id;
                if (dFunName != null)
                {
                    message += " in function " + funName;
                }
                message += ". " + triple.getSecond();

                MyError cause = triple.getThird();
                MyError error = new MyError(getName(), message, ErrorCodes.NO_STATIC_VARIABLE.getCode(),
                        cause, did.getErrorInfo(), interpreter);
                return Data.createErrorData(error);
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
            // domyślnie none
            Data var = Data.createNoneData();
            // parametr: <var>?
            if (params.size() > 0)
            {
                var = params.get(0);
            }

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

    private class ExitFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "EXIT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            while (interpreter.getFrameStack().size() > 0)
            {
                interpreter.popFrame();
            }

            return Data.createNoneData();
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
