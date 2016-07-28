/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules.utils;

import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;

/**
 *
 * @author robert
 */
public class Numbers
{
    public static boolean isNumber(Data data)
    {
        return data.getDataType().equals(DataType.INT) 
                || data.getDataType().equals(DataType.FLOAT) ;
    }
    
    public static float getFloat(Data number)
    {
        if (number.getDataType().equals(DataType.FLOAT))
        {
            return (float)number.getValue();
        }
        else if (number.getDataType().equals(DataType.INT))
        {
            return ((Integer)number.getValue()).floatValue();
        }
        else
        {
            String message = "Passed parameter is not number";
            throw new RuntimeException(message);
        }
    }
    
    public static int getInt(Data number)
    {
        return (int)number.getValue();
    }
}
