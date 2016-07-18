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
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.internals.data_types.Data;

/**
 *
 * @author robert
 */
public class CallFrame
{
    // czy mamy skopiować wartość z funkcji RETURN do poprzedniej ramki
    private final boolean returnToCaller;
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

    public CallFrame(List<Data> passedParameters, UserFunction userFunction)
    {
        this(passedParameters, userFunction, true);
    }
    
    public CallFrame(List<Data> passedParameters, UserFunction userFunction, boolean returnToCaller)
    {
        this.returnToCaller = returnToCaller;
        this.userFunction = userFunction;
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
}
