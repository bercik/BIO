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
    public PerformCallLoc(Interpreter interpreter, Line line)
    {
        CallLine callLine = (CallLine)line;
        String funName = callLine.getFunName();
        ErrorInfo ei = callLine.getErrorInfo();
        
        perform(interpreter, funName, interpreter.currentFrame.getStackParameters(), ei);
    }
    
    public PerformCallLoc(Interpreter interpreter, String funName, List<Data> parameters, ErrorInfo ei)
    {
        perform(interpreter, funName, parameters, ei);
    }
    
    private void perform(Interpreter interpreter, String funName, List<Data> parameters, ErrorInfo ei)
    {
        // profiler
        interpreter.profiler.enter(funName);
        // wywołujemy funkcję
        Data data = interpreter.builtinFunctions.callFunction(funName, 
                parameters, interpreter.currentFrame, interpreter, ei);
        interpreter.profiler.exit();
        
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
            }
            else
            {
                // inaczej wartość zwrócona jest wartością zwróconą z funkcji onSTART
                interpreter.setValueReturnedFromMainFunction(data);
            }
        }
    }
}
