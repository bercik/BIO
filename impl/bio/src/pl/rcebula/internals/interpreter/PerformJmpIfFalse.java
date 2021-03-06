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

/**
 *
 * @author robert
 */
public class PerformJmpIfFalse
{
    public void perform(Interpreter interpreter, Line line)
    {
        JmpLine jmpLine = (JmpLine)line;
        ErrorInfo ei = jmpLine.getErrorInfo();

        // pobieramy parametr z stack parameters
        Data cond = interpreter.currentFrame.getStackParameters().get(0);
        // czyścimy stos parametrów
        interpreter.currentFrame.getStackParameters().clear();

        // sprawdzamy czy false, jeżeli tak to robimy skok
        boolean val = (boolean)cond.getValue();
        if (!val)
        {
            int dest = jmpLine.getDest();
            interpreter.currentFrame.setIp(dest);
        }
    }
}
