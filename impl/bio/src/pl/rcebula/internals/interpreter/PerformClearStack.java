/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.Arrays;
import pl.rcebula.Constants;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;

/**
 *
 * @author robert
 */
public class PerformClearStack
{
    public PerformClearStack(Interpreter interpreter, Line line)
    {
        while (true)
        {
            if (interpreter.currentFrame != null)
            {
                if (interpreter.currentFrame.getVariableStack().size() <= 0)
                {
                    break;
                }
                
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
