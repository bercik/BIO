/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code.line;

import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public abstract class Line
{
    protected final InterpreterFunction interpreterFunction;
    protected final ErrorInfo errorInfo;

    public Line(InterpreterFunction interpreterFunction)
    {
        this(interpreterFunction, null);
    }
    
    public Line(InterpreterFunction interpreterFunction, ErrorInfo errorInfo)
    {
        this.interpreterFunction = interpreterFunction;
        this.errorInfo = errorInfo;
    }

    public InterpreterFunction getInterpreterFunction()
    {
        return interpreterFunction;
    }

    public ErrorInfo getErrorInfo()
    {
        return errorInfo;
    }
}
