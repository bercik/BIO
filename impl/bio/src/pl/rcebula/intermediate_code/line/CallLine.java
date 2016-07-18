/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code.line;

import pl.rcebula.Constants;
import pl.rcebula.code.InterpreterFunction;

/**
 *
 * @author robert
 */
public class CallLine extends Line
{
    private final String funName;

    public CallLine(InterpreterFunction interpreterFunction, String funName, int line, int chNum)
    {
        super(interpreterFunction, line, chNum);
        this.funName = funName;
    }

    public String getFunName()
    {
        return funName;
    }

    @Override
    public String toString()
    {
        return interpreterFunction.toString() + Constants.fieldsSeparator + funName + Constants.fieldsSeparator 
                + line + Constants.fieldsSeparator + chNum;
    }
}
