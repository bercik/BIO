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
public class PushErrorBadParameterTypeLine extends Line
{
    private final String funName;
    private final int paramNum;

    public PushErrorBadParameterTypeLine(String funName, int paramNum, 
            InterpreterFunction interpreterFunction)
    {
        super(interpreterFunction);
        this.funName = funName;
        this.paramNum = paramNum;
    }

    public String getFunName()
    {
        return funName;
    }

    public int getParamNum()
    {
        return paramNum;
    }

    @Override
    public String toString()
    {
        return interpreterFunction.toString() + Constants.fieldsSeparator + funName + Constants.fieldsSeparator 
                + paramNum;
    }
}
