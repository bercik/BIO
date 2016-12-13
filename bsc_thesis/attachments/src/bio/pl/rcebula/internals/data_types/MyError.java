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
    private ErrorInfo errorInfo;
    
    private final CallStack callStack;

    public MyError(String message, Data object, MyError cause, Interpreter interpreter)
    {
        this.message = message;
        this.object = object;
        this.cause = cause;
        
        this.callStack = new CallStack(interpreter.getFrameStack());
    }
    
    public MyError(String message, Data object, MyError cause, ErrorInfo errorInfo, Interpreter interpreter)
    {
        this.message = message;
        this.object = object;
        this.cause = cause;
        this.errorInfo = errorInfo;
        
        this.callStack = new CallStack(interpreter.getFrameStack());
    }
    
    public MyError(String functionName, String message, Data object, MyError cause, 
            ErrorInfo errorInfo, Interpreter interpreter)
    {
        this("In function " + functionName + " " + message, object, cause, errorInfo, interpreter);
    }

    public void setErrorInfo(ErrorInfo errorInfo)
    {
        this.errorInfo = errorInfo;
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
        
        if (cause != null)
        {
            str += "\ncaused by " + cause.toString();
        }
        
        return str;
    }
    
    public String getFirstCauseStackTrace()
    {
        String str = "Stack trace:\n";
        
        MyError callStackError = this;
        MyError causeError = cause;
        while (causeError != null)
        {
            callStackError = causeError;
            causeError = causeError.getCause();
        }
        
        str += callStackError.getCallStack().toString();
        
        return str;
    }
    
    public String getFirstCauseNthLastStackTrace(int n)
    {
        MyError callStackError = this;
        MyError causeError = cause;
        while (causeError != null)
        {
            callStackError = causeError;
            causeError = causeError.getCause();
        }
        
        String str = callStackError.getCallStack().toStringNthLastTrace(n);
        
        return str;
    }
}
