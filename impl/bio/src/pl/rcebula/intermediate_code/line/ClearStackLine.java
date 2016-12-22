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
public class ClearStackLine extends Line
{
    public ClearStackLine(InterpreterFunction interpreterFunction)
    {
        super(interpreterFunction);
    }

    @Override
    public String toString()
    {
        return interpreterFunction.toString();
    }
}
