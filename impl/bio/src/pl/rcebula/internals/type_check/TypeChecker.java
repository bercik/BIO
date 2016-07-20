/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.type_check;

import pl.rcebula.internals.ErrorCodes;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.interpreter.Interpreter;

/**
 *
 * @author robert
 */
public class TypeChecker
{
    private final Data error;
    private final boolean isError;
    
    public TypeChecker(Data actual, String funName, int paramNum, int line, int chNum, 
            Interpreter interpreter, DataType... expected)
    {
        DataType actualDataType = actual.getDataType();
        isError = equals(actualDataType, expected);
        if (isError)
        {
            String message = "In function " + funName + " expected " + paramNum + " parameter to be ";
            for (DataType dt : expected)
            {
                message += dt.toString() + ", ";
            }
            message = message.substring(0, message.length() - 2);
            message += " got " + actualDataType.toString();
            
            MyError cause = null;
            if (actual.getDataType().equals(DataType.ERROR))
            {
                cause = (MyError)actual.getValue();
            }
            MyError myError = new MyError(message, ErrorCodes.BAD_PARAMETER_TYPE.getCode(), cause, line, 
                    chNum, interpreter);
            error = Data.createDataError(myError);
        }
        else
        {
            error = null;
        }
    }
    
    private boolean equals(DataType actual, DataType[] expected)
    {
        if (expected.length == 0)
        {
            return true;
        }
        
        for (DataType dt : expected)
        {
            if (dt.equals(actual))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isError()
    {
        return isError;
    }

    public Data getError()
    {
        return error;
    }
}
