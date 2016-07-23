/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.interpreter.Interpreter;

/**
 *
 * @author robert
 */
public class MyError
{
    private final String message;
    private final Data object;
    private final MyError cause;
    private final ErrorInfo errorInfo;
    
    private final CallStack callStack;

    public MyError(String message, Data object, MyError cause, ErrorInfo errorInfo, Interpreter interpreter)
    {
        this.message = message;
        this.object = object;
        this.cause = cause;
        this.errorInfo = errorInfo;
        
        this.callStack = new CallStack(interpreter.getFrameStack());
    }

    public String getMessage()
    {
        return message;
    }

    public Data getObject()
    {
        return object;
    }

    public MyError getCause()
    {
        return cause;
    }

    public ErrorInfo getErrorInfo()
    {
        return errorInfo;
    }

    public CallStack getCallStack()
    {
        return callStack;
    }

    @Override
    public String toString()
    {
        String str = "";
        
        str += errorInfo.toString() + ": ";
        str += message;
        
        return str;
    }
}
