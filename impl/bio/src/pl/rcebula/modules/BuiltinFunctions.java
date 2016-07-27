/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.rcebula.Constants;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code.ParamType;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.intermediate_code.Param;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.intermediate_code.line.CallLine;
import pl.rcebula.intermediate_code.line.PopLine;
import pl.rcebula.intermediate_code.line.PushLine;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.modules.utils.ErrorCodes;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.MyError;

/**
 *
 * @author robert
 */
public class BuiltinFunctions
{
    private final Map<String, IFunction> functions = new HashMap<>();
    private final Map<String, IEvent> events = new HashMap<>();

    private final Map<String, IModule> modules = new HashMap<>();

    public BuiltinFunctions(List<String> modulesName, Map<String, UserFunction> userFunctions, MyFiles files)
    {
        // ADD YOUR MODULE HERE
        putModule(new BasicModule());
        putModule(new IoModule());
        putModule(new ObserverModule());
        putModule(new MathModule());
        putModule(new ArraysModule());
        // STOP HERE, DON'T EDIT REST OF CODE

        createFunctionsInModules(modulesName);
        getFunctionsAndEventsFromModules();

        createUserFunctionsForEvents(userFunctions, files);
    }

    private void putModule(IModule module)
    {
        modules.put(module.getName(), module);
    }

    private void createFunctionsInModules(List<String> modulesName)
    {
        for (String moduleName : modulesName)
        {
            IModule module = modules.get(moduleName);

            if (module != null)
            {
                module.createFunctionsAndEvents();
            }
            else
            {
                String message = "There is no implementation for module " + moduleName;
                throw new RuntimeException(message);
            }
        }
    }

    private void createUserFunctionsForEvents(Map<String, UserFunction> userFunctions, MyFiles files)
    {
        for (Map.Entry<String, IEvent> set : events.entrySet())
        {
            String name = set.getKey();
            IEvent event = set.getValue();

            // tworzymy funkcję o schemacie:
            // name,par1,par2,...,parN
            // push,none:,-1,-1,-1
            // pop,1
            // call_loc,return,-1,-1,-1
            ErrorInfo ei = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());
            UserFunction uf = new UserFunction(name, ei);

            // dodajemy parametry
            List<String> params = new ArrayList<>();
            for (int i = 0; i < event.getNumberOfParameters(); ++i)
            {
                params.add("par" + i);
            }
            uf.addParams(params);

            // dodajemy linię push,none:,-1,-1,-1
            Param param = new Param(ParamType.NONE, null);
            uf.addLine(new PushLine(InterpreterFunction.PUSH, param, ei));
            // dodajemy linię pop,1
            uf.addLine(new PopLine(InterpreterFunction.POP, 1, ei));
            // dodajemy linię call_loc,return,-1,-1,-1
            uf.addLine(new CallLine(InterpreterFunction.CALL_LOC, Constants.returnFunctionName, ei));

            // dodajemy user function do mapy user functions
            userFunctions.put(uf.getName(), uf);
        }
    }

    private void getFunctionsAndEventsFromModules()
    {
        for (Map.Entry<String, IModule> set : modules.entrySet())
        {
            IModule m = set.getValue();
            functions.putAll(m.getFunctions());
            events.putAll(m.getEvents());
        }
    }

    public Data callFunction(String name, List<Data> params, CallFrame currentFrame, Interpreter interpreter,
            ErrorInfo ei)
    {
        IFunction fun = functions.get(name);
        if (fun != null)
        {
            return fun.call(params, currentFrame, interpreter);
        }
        else
        {
            String message = "Builtin function " + name + " is not implemented. Please contact interpreter creator";
            MyError myError = new MyError(message,
                    ErrorCodes.BUILTIN_FUNCTION_NOT_IMPLEMENTED.getCode(), null, ei, interpreter);
            return Data.createErrorData(myError);
        }
    }

    public void callEvent(String name, List<Data> parameters, Interpreter interpreter, ErrorInfo ei)
    {
        UserFunction uf = interpreter.getUserFunctions().get(name);
        if (uf != null)
        {
            interpreter.callEvent(parameters, uf, ei);
        }
        else
        {
            String message = "Event " + name + " is not implemented";
            throw new RuntimeException(message);
        }
    }
}
