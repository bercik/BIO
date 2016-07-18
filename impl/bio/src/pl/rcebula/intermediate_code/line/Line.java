/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code.line;

import pl.rcebula.code.InterpreterFunction;

/**
 *
 * @author robert
 */
public abstract class Line
{
    protected final InterpreterFunction interpreterFunction;
    protected final int line;
    protected final int chNum;

    public Line(InterpreterFunction interpreterFunction, int line, int chNum)
    {
        this.interpreterFunction = interpreterFunction;
        this.line = line;
        this.chNum = chNum;
    }

    public InterpreterFunction getInterpreterFunction()
    {
        return interpreterFunction;
    }

    public int getLine()
    {
        return line;
    }

    public int getChNum()
    {
        return chNum;
    }
}
