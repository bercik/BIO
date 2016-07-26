/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.Iterator;
import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.intermediate_code.line.CallLine;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;

/**
 *
 * @author robert
 */
public class PerformCall
{
    public PerformCall(List<Data> parameters, UserFunction uf, Interpreter interpreter, ErrorInfo ei)
    {
        // tworzymy ramkę i odkładamy na stos zaznaczając, że nie interesuje nas wartość zwracana
        CallFrame cf = new CallFrame(parameters, uf, ei, false);
        interpreter.pushFrameToStack(cf);

        // tworzymy ramki dla każdego obserwatora w odwrotnej kolejności w jakiej były dodawane, 
        // z zaznaczeniem, że nie interesuje nas zwracana przez nie wartość
        // Jeżeli funkcja obserwatora miała obserwatorów to także dodajemy
        pushCallFrameForObservers(uf, interpreter, parameters, ei);
    }
    
    public PerformCall(Interpreter interpreter, Line line)
    {
        CallLine callLine = (CallLine)line;
        String funName = callLine.getFunName();
        ErrorInfo ei = callLine.getErrorInfo();
        UserFunction uf = interpreter.userFunctions.get(funName);

        // tworzymy ramkę i odkładamy na stos
        List<Data> stackParams = interpreter.currentFrame.getStackParameters();
        CallFrame cf = new CallFrame(stackParams, uf, ei);
        interpreter.pushFrameToStack(cf);

        // tworzymy ramki dla każdego obserwatora w odwrotnej kolejności w jakiej były dodawane, 
        // z zaznaczeniem, że nie interesuje nas zwracana przez nie wartość
        // Jeżeli funkcja obserwatora miała obserwatorów to także dodajemy
        pushCallFrameForObservers(uf, interpreter, stackParams, ei);
    }
    
    private void pushCallFrameForObservers(UserFunction uf, Interpreter interpreter, 
            List<Data> stackParams, ErrorInfo ei)
    {
        Iterator<String> it = uf.getObservers().descendingIterator();
        while (it.hasNext())
        {
            String observer = it.next();
            uf = interpreter.userFunctions.get(observer);
            CallFrame cf = new CallFrame(stackParams, uf, ei, false);
            interpreter.pushFrameToStack(cf);
            
            // dodajemy obserwatorów tej funkcji rekursywnie
            pushCallFrameForObservers(uf, interpreter, stackParams, ei);
        }
    }
}