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
public class PopLine extends Line
{
    private final int amount;

    public PopLine(InterpreterFunction interpreterFunction, int amount, ErrorInfo errorInfo)
    {
        super(interpreterFunction, errorInfo);
        this.amount = amount;
    }

    public int getAmount()
    {
        return amount;
    }

    @Override
    public String toString()
    {
        return interpreterFunction.toString() + Constants.fieldsSeparator + amount;
    }
}
