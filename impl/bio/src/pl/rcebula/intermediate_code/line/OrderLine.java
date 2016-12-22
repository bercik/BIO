/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code.line;

import java.util.List;
import pl.rcebula.Constants;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public class OrderLine extends Line
{
    private final List<Integer> orderList;
    
    public OrderLine(InterpreterFunction interpreterFunction, List<Integer> orderList)
    {
        super(interpreterFunction);
        
        this.orderList = orderList;
    }

    public List<Integer> getOrderList()
    {
        return orderList;
    }
    
    @Override
    public String toString()
    {
        String res = interpreterFunction.toString() + Constants.fieldsSeparator;
        
        for (int i = 0; i < orderList.size(); ++i)
        {
            res += orderList.get(i);
            
            if (i != orderList.size() - 1)
            {
                res += Constants.fieldsSeparator;
            }
        }
        
        return res;
    }
}
