/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code.line;

import pl.rcebula.Constants;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public class JmpLine extends Line
{
    private final int dest;

    public JmpLine(InterpreterFunction interpreterFunction, int dest, ErrorInfo errorInfo)
    {
        super(interpreterFunction, errorInfo);
        this.dest = dest;
    }

    public int getDest()
    {
        return dest;
    }
    
    @Override
    public String toString()
    {
        return interpreterFunction.toString() + Constants.fieldsSeparator + dest + Constants.fieldsSeparator 
                + errorInfo.getLineNum() + Constants.fieldsSeparator + errorInfo.getChNum() + 
                Constants.fieldsSeparator + errorInfo.getFile().getNum();
    }
}
