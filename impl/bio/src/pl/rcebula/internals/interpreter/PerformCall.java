/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.intermediate_code.line.CallLine;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.internals.CallFrame;

/**
 *
 * @author robert
 */
public class PerformCall
{
    public PerformCall(Interpreter interpreter, Line line)
    {
        CallLine callLine = (CallLine)line;
        String funName = callLine.getFunName();
        ErrorInfo ei = callLine.getErrorInfo();
        UserFunction uf = interpreter.userFunctions.get(funName);

        // tworzymy ramkę i odkładamy na stos
        CallFrame cf = new CallFrame(interpreter.currentFrame.getStackParameters(), uf, ei);
        interpreter.pushFrameToStack(cf);

        // tworzymy ramki dla każdego obserwatora w losowej kolejności, z zaznaczeniem, że nie interesuje nas
        // zwracana przez nie wartość
        for (String observer : uf.getObservers())
        {
            uf = interpreter.userFunctions.get(observer);
            cf = new CallFrame(interpreter.currentFrame.getStackParameters(), uf, ei, false);
            interpreter.pushFrameToStack(cf);
        }
    }
}
