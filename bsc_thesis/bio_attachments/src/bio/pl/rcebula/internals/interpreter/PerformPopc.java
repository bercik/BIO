/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.Arrays;
import pl.rcebula.Constants;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.intermediate_code.line.PopLine;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;

/**
 *
 * @author robert
 */
public class PerformPopc
{
    public void perform(Interpreter interpreter, Line line)
    {
        PopLine popLine = (PopLine)line;
        int amount = popLine.getAmount();
        
        // ściągamy ze stosu amount obiektów
        while (amount-- > 0)
        {
            if (interpreter.currentFrame != null)
            {
                Data d = interpreter.currentFrame.getVariableStack().pop();
                // jeżeli typu error to wywołaj zdarzenie onUNHANDLED_ERROR
                if (d != null && d.getDataType().equals(DataType.ERROR))
                {
                    interpreter.builtinFunctions.callEvent(Constants.unhandledErrorFunctionName, Arrays.asList(d), 
                            interpreter, d.getErrorInfo());
                }
            }
            else
            {
                break;
            }
        }
    }
}
