/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.intermediate_code.line.OrderLine;

/**
 *
 * @author robert
 */
public class PerformOrder
{
    public void perform(Interpreter interpreter, Line line)
    {
        OrderLine orderLine = (OrderLine)line;
        interpreter.setOrderList(orderLine.getOrderList());
    }
}
