/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module;

import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.internals.data_types.Data;

/**
 *
 * @author robert
 */
public interface IFunction
{
    // nazwa funkcji
    public String getName();
    
    // wywołanie funkcji, zwracana wartość jest odkładana na stos wartości aktualnej ramki
    default public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
    {
        return call(params, currentFrame, interpreter, null);
    }
    
    default public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, 
            ErrorInfo callErrorInfo)
    {
        return call(params, currentFrame, interpreter);
    }
}
