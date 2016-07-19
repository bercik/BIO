/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

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
    private final int line;
    private final int chNum;
    
    private final CallStack callStack;

    public MyError(String message, Data object, MyError cause, int line, int chNum, Interpreter interpreter)
    {
        this.message = message;
        this.object = object;
        this.cause = cause;
        this.line = line;
        this.chNum = chNum;
        
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

    public int getLine()
    {
        return line;
    }

    public int getChNum()
    {
        return chNum;
    }

    public CallStack getCallStack()
    {
        return callStack;
    }
}
