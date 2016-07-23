/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.ArrayList;
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
import pl.rcebula.modules.BuiltinFunctions;
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

    public Interpreter(String[] args, Map<String, UserFunction> userFunctions, BuiltinFunctions builtinFunctions,
            TimeProfiler timeProfiler, IProfiler profiler, MyFiles files)
    {
        logger.info("Interpreter");

        this.userFunctions = userFunctions;
        this.builtinFunctions = builtinFunctions;
        this.timeProfiler = timeProfiler;
        this.profiler = profiler;
        this.files = files;

        // tworzymy ramkę z funkcją main (onSTART)
        CallFrame mainFrame = createMainFrame(args);
        // wrzucamy na stos ramek
        pushFrameToStack(mainFrame);

        // zaczynamy wykonywanie kodu
        run();
    }

    void pushFrameToStack(CallFrame cf)
    {
        frameStack.push(cf);
        setCurrentFrame(cf);
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

            // w zależności od opcode wykonujemy różne akcje
            switch (line.getInterpreterFunction())
            {
                case CALL:
                    timeProfiler.start("CALL");
                    new PerformCall(this, line);
                    timeProfiler.stop();
                    break;
                case CALL_LOC:
                    timeProfiler.start("CALL_LOC");
                    new PerformCallLoc(this, line);
                    timeProfiler.stop();
                    break;
                case PUSH:
                    timeProfiler.start("PUSH");
                    new PerformPush(this, line);
                    timeProfiler.stop();
                    break;
                case POP:
                    timeProfiler.start("POP");
                    new PerformPop(this, line);
                    timeProfiler.stop();
                    break;
                case POPC:
                    timeProfiler.start("POPC");
                    new PerformPopc(this, line);
                    timeProfiler.stop();
                    break;
                case JMP:
                    timeProfiler.start("JMP");
                    new PerformJmp(this, line);
                    timeProfiler.stop();
                    break;
                case JMP_IF_FALSE:
                    timeProfiler.start("JMP_IF_FALSE");
                    new PerformJmpIfFalse(this, line);
                    timeProfiler.stop();
                    break;
                case CLEAR_STACK:
                    timeProfiler.start("CLEAR_STACK");
                    new PerformClearStack(this, line);
                    timeProfiler.stop();
                    break;
            }
        }
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
                dataArgs[i] = Data.createDataString(args[i]);
            }
            parameters.add(Data.createDataArray(dataArgs));
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

    public void setCurrentFrame(CallFrame currentFrame)
    {
        if (currentFrame != null)
        {
            profiler.enter(currentFrame.getUserFunction().getName());
        }
        this.currentFrame = currentFrame;
    }
}
