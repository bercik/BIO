/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.module.modules.IterModule;

/**
 *
 * @author robert
 */
public class CallFrame
{
    // czy mamy skopiować wartość z funkcji RETURN do poprzedniej ramki
    private final boolean returnToCaller;
    // flaga informująca o tym czy należy wywołać funkcję FOREACH w momencie kiedy
    // zostanie ściągnięta poprzednia ramka
    private boolean callForeach;
    // struktura przechowująca informację dla funkcji FOREACH
    private IterModule.ForeachFunction.ForeachInfo foreachInfo;
    // informacja o miejscu występowania w kodzie źródłowym
    private final ErrorInfo errorInfo;
    // instruction pointer, wskaźnik na linijkę w kodzie
    private int ip;
    // referencja do kodu funkcji i innych informacji (np. obserwatorów)
    private final UserFunction userFunction;
    // stos wartości
    private final Stack<Data> variableStack = new Stack<>();
    // wartości lokalne funkcji
    private final Map<String, Data> localVariables = new HashMap<>();
    // parametry ściągnięte ze stosu metodą POP
    private final List<Data> stackParameters = new ArrayList<>();

    public CallFrame(List<Data> passedParameters, UserFunction userFunction, ErrorInfo errorInfo)
    {
        this(passedParameters, userFunction, errorInfo, true);
    }
    
    public CallFrame(List<Data> passedParameters, UserFunction userFunction, ErrorInfo errorInfo, 
            boolean returnToCaller)
    {
        this.returnToCaller = returnToCaller;
        this.userFunction = userFunction;
        this.errorInfo = errorInfo;
        // wskaźnik instrukcji jest ustawiany na zero
        this.ip = 0;
        
        // kopiujemy parametry do zmiennych lokalnych
        copyPassedParameters(passedParameters);
    }
    
    private void copyPassedParameters(List<Data> passedParameters)
    {
        Iterator<Data> it = passedParameters.iterator();
        for (String param : userFunction.getParams())
        {
            Data data = it.next();
            localVariables.put(param, data);
        }
    }

    public IterModule.ForeachFunction.ForeachInfo getForeachInfo()
    {
        return foreachInfo;
    }

    public void setForeachInfo(IterModule.ForeachFunction.ForeachInfo foreachInfo)
    {
        this.foreachInfo = foreachInfo;
    }

    public boolean isCallForeach()
    {
        return callForeach;
    }

    public void setCallForeach(boolean callForeach)
    {
        this.callForeach = callForeach;
    }
    
    public Map<String, Data> getLocalVariables()
    {
        return localVariables;
    }

    public ErrorInfo getErrorInfo()
    {
        return errorInfo;
    }

    public UserFunction getUserFunction()
    {
        return userFunction;
    }

    public boolean isReturnToCaller()
    {
        return returnToCaller;
    }

    public int getIp()
    {
        return ip;
    }

    public void setIp(int ip)
    {
        this.ip = ip;
    }

    public List<Data> getStackParameters()
    {
        return stackParameters;
    }

    public Stack<Data> getVariableStack()
    {
        return variableStack;
    }
}
