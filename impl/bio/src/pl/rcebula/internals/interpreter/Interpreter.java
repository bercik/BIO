/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;
import pl.rcebula.Constants;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.error_report.MyFiles.File;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.module.BuiltinFunctions;
import pl.rcebula.module.utils.error_codes.ErrorCodes;
import pl.rcebula.tools.IProfiler;
import pl.rcebula.utils.TimeProfiler;

/**
 *
 * @author robert
 */
public class Interpreter
{
    // logger
    Logger logger = Logger.getGlobal();

    // time profiler
    final TimeProfiler timeProfiler;
    // profiler
    final IProfiler profiler;
    // stos ramek wywołań
    final Stack<CallFrame> frameStack = new Stack<>();
    // aktualna ramka
    CallFrame currentFrame;
    // zmienne globalne
    final Map<String, Data> globalVariables = new HashMap<>();
    // funkcje użytkownika 
    final Map<String, UserFunction> userFunctions;
    // funkcje wbudowane
    final BuiltinFunctions builtinFunctions;
    // pliki źródłowe
    private final MyFiles files;
    // wartość zwrócona z funckji onSTART
    private Data valueReturnedFromMainFunction;
    // kolejność przekazywania argumentów
    private List<Integer> orderList;
    
    // klasy wykonujące podstawowe (CALL, POP) instrukcje interpretera
    private PerformCall call = new PerformCall();
    private PerformCallLoc callLoc = new PerformCallLoc();
    private PerformClearStack clearStack = new PerformClearStack();
    private PerformJmp jmp = new PerformJmp();
    private PerformJmpIfFalse jmpIfFalse = new PerformJmpIfFalse();
    private PerformPop pop = new PerformPop();
    private PerformPopc popc = new PerformPopc();
    private PerformPush push = new PerformPush();
    private PerformOrder order = new PerformOrder();

    public Interpreter(String pathToScript, String[] args, Map<String, UserFunction> userFunctions,
            BuiltinFunctions builtinFunctions, TimeProfiler timeProfiler, IProfiler profiler, MyFiles files)
    {
        logger.info("Interpreter");

        this.userFunctions = userFunctions;
        this.builtinFunctions = builtinFunctions;
        this.timeProfiler = timeProfiler;
        this.profiler = profiler;
        this.files = files;

        // wstawiamy ścieżkę od skryptu jako pierwszy argument
        String[] tmpArgs = new String[args.length + 1];
        tmpArgs[0] = pathToScript;
        System.arraycopy(args, 0, tmpArgs, 1, args.length);
        // tworzymy ramkę z funkcją main (onSTART)
        CallFrame mainFrame = createMainFrame(tmpArgs);
        // wrzucamy na stos ramek
        pushFrameToStack(mainFrame);

        // zaczynamy wykonywanie kodu
        // try catch na wypadek błędów końca stosu lub pamięci
        try
        {
            run();
        }
        catch (StackOverflowError ex)
        {
            ErrorInfo ei = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());
            MyError error = new MyError("Stack overflow", ErrorCodes.STACK_OVERFLOW.getCode(), null, ei, this);
            valueReturnedFromMainFunction = Data.createErrorData(error);
        }
        catch (OutOfMemoryError ex)
        {
            ErrorInfo ei = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());
            MyError error = new MyError("Heap overflow", ErrorCodes.HEAP_OVERFLOW.getCode(), null, ei, this);
            valueReturnedFromMainFunction = Data.createErrorData(error);
        }
        // po wykonaniu kodu, uruchamiamy ewentualną obsługę błędu zwróconego z funckji onSTART
        endOfFirstRun();
    }

    void pushFrameToStack(CallFrame cf)
    {
        frameStack.push(cf);
        setCurrentFrame(cf);
        profiler.enter(cf.getUserFunction().getName());
    }

    void run()
    {
        // działa dopóki na stosie ramek są jakieś ramki
        while (currentFrame != null)
        {
            timeProfiler.start("Read Line");
            // wczytujemy linię kodu
            UserFunction uf = currentFrame.getUserFunction();
            // pobieramy instruction pointer
            int ip = currentFrame.getIp();
            // pobieramy linię kodu
            Line line = uf.getLines().get(ip);
            // ustawiamy instruction pointer na następną linię
            currentFrame.setIp(ip + 1);
            // koniec wczytywania
            timeProfiler.stop();

            // TODELETE
//            logger.finest(uf.getName());
//            logger.finest(Integer.toString(ip));
//            logger.fine(line.toString());

            // w zależności od opcode wykonujemy różne akcje
            switch (line.getInterpreterFunction())
            {
                case CALL:
                    timeProfiler.start("CALL");
                    call.perform(this, line);
                    timeProfiler.stop();
                    break;
                case CALL_LOC:
                    timeProfiler.start("CALL_LOC");
                    callLoc.perform(this, line);
                    timeProfiler.stop();
                    break;
                case PUSH:
                    timeProfiler.start("PUSH");
                    push.perform(this, line);
                    timeProfiler.stop();
                    break;
                case POP:
                    timeProfiler.start("POP");
                    pop.perform(this, line);
                    timeProfiler.stop();
                    break;
                case POPC:
                    timeProfiler.start("POPC");
                    popc.perform(this, line);
                    timeProfiler.stop();
                    break;
                case JMP:
                    timeProfiler.start("JMP");
                    jmp.perform(this, line);
                    timeProfiler.stop();
                    break;
                case JMP_IF_FALSE:
                    timeProfiler.start("JMP_IF_FALSE");
                    jmpIfFalse.perform(this, line);
                    timeProfiler.stop();
                    break;
                case CLEAR_STACK:
                    timeProfiler.start("CLEAR_STACK");
                    clearStack.perform(this, line);
                    timeProfiler.stop();
                    break;
                case ORDER:
                    timeProfiler.start("ORDER");
                    order.perform(this, line);
                    timeProfiler.stop();
                    break;
                default:
                    String message = "Don't know what to do with " + line.getInterpreterFunction() + " instruction";
                    throw new RuntimeException(message);
            }
        }
    }

    private void endOfFirstRun()
    {
        // jeżeli wartość zwrócona z funckji onSTART jest typu ERROR
        if (valueReturnedFromMainFunction != null
                && valueReturnedFromMainFunction.getDataType().equals(DataType.ERROR))
        {
            // wywołujemy zdarzenie onUNHANDLED_ERROR
            List<Data> parameters = Arrays.asList(valueReturnedFromMainFunction);
            builtinFunctions.callEvent(Constants.unhandledErrorFunctionName, parameters, this, 
                    valueReturnedFromMainFunction.getErrorInfo());
        }

        run();
    }

    CallFrame createMainFrame(String[] args)
    {
        UserFunction uf = userFunctions.get(Constants.mainFunctionName);
        List<Data> parameters = new ArrayList<>();
        if (uf.getParams().size() == 1)
        {
            Data[] dataArgs = new Data[args.length];
            for (int i = 0; i < args.length; ++i)
            {
                dataArgs[i] = Data.createStringData(args[i]);
            }
            parameters.add(Data.createArrayData(dataArgs));
        }

        File file = files.getFileGeneratedByCompiler();
        return new CallFrame(parameters, uf, new ErrorInfo(-1, -1, file));
    }

    public Map<String, Data> getGlobalVariables()
    {
        return globalVariables;
    }

    public Stack<CallFrame> getFrameStack()
    {
        return frameStack;
    }

    public BuiltinFunctions getBuiltinFunctions()
    {
        return builtinFunctions;
    }

    public Map<String, UserFunction> getUserFunctions()
    {
        return userFunctions;
    }

    public CallFrame getCurrentFrame()
    {
        return currentFrame;
    }

    public CallFrame popFrame()
    {
        profiler.exit();
        CallFrame cf = frameStack.pop();

        if (frameStack.size() > 0)
        {
            setCurrentFrame(frameStack.peek());
        }
        else
        {
            setCurrentFrame(null);
        }

        return cf;
    }

    public void setValueReturnedFromMainFunction(Data valueReturnedFromMainFunction)
    {
        this.valueReturnedFromMainFunction = valueReturnedFromMainFunction;
    }

    public List<Integer> getOrderList()
    {
        return orderList;
    }

    public void setOrderList(List<Integer> orderList)
    {
        this.orderList = orderList;
    }

    public void setCurrentFrame(CallFrame currentFrame)
    {
        this.currentFrame = currentFrame;
    }

    public void callEvent(List<Data> parameters, UserFunction uf, ErrorInfo ei)
    {
        if (uf.getParams().size() == parameters.size())
        {
            call.perform(parameters, false, uf, this, ei);
        }
        else
        {
            String message = "Event " + uf.getName() + " get " + uf.getParams().size() + " parameters, got "
                    + parameters.size();
            throw new RuntimeException(message);
        }
    }
}
