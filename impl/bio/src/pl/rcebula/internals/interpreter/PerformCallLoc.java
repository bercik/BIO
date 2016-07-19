/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

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
        int lineNum = callLine.getLine();
        int chNum = callLine.getChNum();
        
        // wywołujemy funkcję
        Data data = interpreter.builtinFunctions.callFunction(funName, 
                interpreter.currentFrame.getStackParameters(), interpreter.currentFrame, interpreter, 
                lineNum, chNum);
        
        // jeżeli data jest różne od null
        if (data != null)
        {
            // ustawiamy nowe line i chNum
            data.setLineAndChNum(lineNum, chNum);
            // zapisujemy na stosie wartości aktualnej ramki
            interpreter.currentFrame.getVariableStack().push(data);
        }
    }
}
