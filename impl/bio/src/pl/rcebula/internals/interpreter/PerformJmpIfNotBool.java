/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.line.JmpLine;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;

/**
 *
 * @author robert
 */
public class PerformJmpIfNotBool
{
    public void perform(Interpreter interpreter, Line line)
    {
        JmpLine jmpLine = (JmpLine)line;
        
        // podglądamy parametr z stack parameters
        Data cond = interpreter.currentFrame.getStackParameters().get(0);
        // sprawdzamy czy  jest typu bool, jeżeli nie jest to
        if (!cond.getDataType().equals(DataType.BOOL))
        {
            // skaczemy do linii
            int dest = jmpLine.getDest();
            interpreter.currentFrame.setIp(dest);
        }
    }
}
