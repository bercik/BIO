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
public class JmpLine extends Line
{
    private final int dest;

    public JmpLine(InterpreterFunction interpreterFunction, int dest, int line, int chNum)
    {
        super(interpreterFunction, line, chNum);
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
                + line + Constants.fieldsSeparator + chNum;
    }
}
