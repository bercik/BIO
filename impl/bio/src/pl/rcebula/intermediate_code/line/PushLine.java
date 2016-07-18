/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code.line;

import pl.rcebula.Constants;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.intermediate_code.Param;

/**
 *
 * @author robert
 */
public class PushLine extends Line
{
    private final Param param;

    public PushLine(InterpreterFunction interpreterFunction, Param param, int line, int chNum)
    {
        super(interpreterFunction, line, chNum);
        this.param = param;
    }

    public Param getParam()
    {
        return param;
    }
    
    @Override
    public String toString()
    {
        return interpreterFunction.toString() + Constants.fieldsSeparator + param.toString() + 
                Constants.fieldsSeparator + line + Constants.fieldsSeparator + chNum;
    }
}
