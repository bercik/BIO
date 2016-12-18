/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.Arrays;
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
    public void perform(List<Data> parameters, boolean returnValue, UserFunction uf, Interpreter interpreter, 
            ErrorInfo ei, boolean isCalledFromForeach)
    {
        // czyścimy order list
        interpreter.setOrderList(null);
        
        // tworzymy ramkę i odkładamy na stos zaznaczając, czy interesuje nas wartość zwracana i czy 
        // została wywołana przez funkcję FOREACH
        CallFrame cf = new CallFrame(parameters, uf, ei, returnValue, isCalledFromForeach);
        interpreter.pushFrameToStack(cf);

        // tworzymy ramki dla każdego obserwatora w odwrotnej kolejności w jakiej były dodawane, 
        // z zaznaczeniem, że nie interesuje nas zwracana przez nie wartość
        // Jeżeli funkcja obserwatora miała obserwatorów to także dodajemy
        pushCallFrameForObservers(uf, interpreter, parameters, ei);
        
        // czyścimy stos parametrów
        interpreter.currentFrame.getStackParameters().clear();
    }
    
    public void perform(List<Data> parameters, boolean returnValue, UserFunction uf, Interpreter interpreter, 
            ErrorInfo ei)
    {
        perform(parameters, returnValue, uf, interpreter, ei, false);
    }
    
    public void perform(Interpreter interpreter, Line line)
    {
        CallLine callLine = (CallLine)line;
        String funName = callLine.getFunName();
        ErrorInfo ei = callLine.getErrorInfo();
        UserFunction uf = interpreter.userFunctions.get(funName);

        // pobieramy parametry
        List<Data> stackParams = interpreter.currentFrame.getStackParameters();
        // pobieramy ich kolejność
        List<Integer> orderList = interpreter.getOrderList();
        // jeżeli różne od null to zamieniamy kolejność parametrów w odpowiedni sposób
        if (orderList != null)
        {
            Data[] tmp = new Data[stackParams.size()];
            for (int i = 0; i < orderList.size(); ++i)
            {
                tmp[orderList.get(i) - 1] = stackParams.get(i);
            }
            
            stackParams = Arrays.asList(tmp);
            // czyścimy order list
            interpreter.setOrderList(null);
        }
        // tworzymy ramkę i odkładamy na stos
        CallFrame cf = new CallFrame(stackParams, uf, ei);
        interpreter.pushFrameToStack(cf);

        // tworzymy ramki dla każdego obserwatora w odwrotnej kolejności w jakiej były dodawane, 
        // z zaznaczeniem, że nie interesuje nas zwracana przez nie wartość
        // Jeżeli funkcja obserwatora miała obserwatorów to także dodajemy
        pushCallFrameForObservers(uf, interpreter, stackParams, ei);
        
        // czyścimy stos parametrów
        interpreter.currentFrame.getStackParameters().clear();
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
