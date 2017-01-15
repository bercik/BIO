/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.line.CallLine;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.internals.data_types.Data;

/**
 *
 * @author robert
 */
public class PerformCallLoc
{
    public void perform(Interpreter interpreter, Line line)
    {
        CallLine callLine = (CallLine) line;
        String funName = callLine.getFunName();
        ErrorInfo ei = callLine.getErrorInfo();

        _perform(interpreter, funName, interpreter.currentFrame.getStackParameters(), ei);
    }

    public void perform(Interpreter interpreter, String funName, List<Data> parameters, ErrorInfo ei)
    {
        _perform(interpreter, funName, parameters, ei);
    }

    private void _perform(Interpreter interpreter, String funName, List<Data> parameters, ErrorInfo ei)
    {
        // profiler
        interpreter.profiler.enter(funName);
        // wywołujemy funkcję
        Data data = interpreter.builtinFunctions.callFunction(funName,
                parameters, interpreter.currentFrame, interpreter, ei);
        interpreter.profiler.exit();
        
        // czyścimy stos parametrów
        if (interpreter.currentFrame != null)
        {
            interpreter.currentFrame.getStackParameters().clear();
        }

        // jeżeli data jest różne od null
        if (data != null)
        {
            // ustawiamy error info
            data.setErrorInfo(ei);
            // jeżeli to nie była ostatnia ramka to
            // zapisujemy na stosie wartości aktualnej ramki
            if (interpreter.currentFrame != null)
            {
                interpreter.currentFrame.getVariableStack().push(data);
                
                // sprawdzamy czy należy wywołać funkcję FOREACH
                if (interpreter.currentFrame.isCallForeach())
                {
                    // wywołujemy
                    data = interpreter.getBuiltinFunctions().callFunction("FOREACH", null, interpreter.getCurrentFrame(), 
                            interpreter, null);
                    // jeżeli zmieniono callForeach na false
                    if (!interpreter.currentFrame.isCallForeach())
                    {
                        // zapisujemy na stosie wartości aktualnej ramki
                        interpreter.currentFrame.getVariableStack().push(data);
                    }
                }
            }
            else
            {
                // inaczej wartość zwrócona jest wartością zwróconą z funkcji onSTART
                interpreter.setValueReturnedFromMainFunction(data);
            }
        }
    }
}
