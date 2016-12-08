/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module;

import pl.rcebula.module.modules.ArraysModule;
import pl.rcebula.module.modules.MathModule;
import pl.rcebula.module.modules.IoModule;
import pl.rcebula.module.modules.TypeCheckModule;
import pl.rcebula.module.modules.IntsModule;
import pl.rcebula.module.modules.ObserverModule;
import pl.rcebula.module.modules.BasicModule;
import pl.rcebula.module.modules.ConversionModule;
import pl.rcebula.module.modules.ErrorsModule;
import pl.rcebula.module.modules.LogicModule;
import pl.rcebula.module.modules.CompareModule;
import java.util.ArrayList;
import java.util.Arrays;
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
import pl.rcebula.module.utils.error_codes.ErrorCodes;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.module.modules.FloatsModule;
import pl.rcebula.module.modules.IterModule;
import pl.rcebula.module.modules.ReflectionsModule;
import pl.rcebula.module.modules.StringsModule;
import pl.rcebula.module.modules.StructsModule;
import pl.rcebula.module.modules.TcpModule;

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
        putModule(new ErrorsModule());
        putModule(new CompareModule());
        putModule(new TypeCheckModule());
        putModule(new IntsModule());
        putModule(new LogicModule());
        putModule(new ConversionModule());
        putModule(new FloatsModule());
        putModule(new StringsModule());
        putModule(new ReflectionsModule());
        putModule(new StructsModule());
        putModule(new IterModule());
        putModule(new TcpModule());
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
            // jeżeli zdarzenie onUNHANDLED_ERROR to specjalne traktowanie
            if (name.equals(Constants.unhandledErrorFunctionName))
            {
                // jeżeli ma obserwatorów to po prostu wywołujemy
                if (uf.getObservers().size() > 0)
                {
                    interpreter.callEvent(parameters, uf, ei);
                }
                // inaczej wypisujemy błąd na ekran i przerywamy
                else
                {
                    // wypisujemy błąd na ekran i nową linię
                    interpreter.getBuiltinFunctions().callFunction("PRINTLN", 
                            Arrays.asList(parameters.get(0), Data.createStringData("")), 
                            interpreter.getCurrentFrame(), interpreter, ei);
                    // wypisujemy stack trace
                    interpreter.getBuiltinFunctions().callFunction("PRINT_STACK_TRACE", parameters, 
                            interpreter.getCurrentFrame(), interpreter, ei);
                    // zakańczamy program
                    interpreter.getBuiltinFunctions().callFunction("EXIT", new ArrayList<Data>(), 
                            interpreter.getCurrentFrame(), interpreter, ei);
                }
            }
            else
            {
                interpreter.callEvent(parameters, uf, ei);
            }
        }
        else
        {
            String message = "Event " + name + " is not implemented";
            throw new RuntimeException(message);
        }
    }
}
