/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules.utils.type_checker;

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.modules.utils.ErrorCodes;

/**
 *
 * @author robert
 */
public class TypeCheckerCollection
{
    private final Data error;
    private final boolean isError;
    
    public TypeCheckerCollection(Data actual, String funName, int paramNum, int argNum, ErrorInfo ei, 
            Interpreter interpreter, DataType... expected)
    {
        DataType actualDataType = actual.getDataType();
        isError = !equals(actualDataType, expected);
        
        if (isError)
        {
            String message = "In function " + funName + " in passed parameter " + paramNum + " collection, "
                    + "expected " + argNum + " element to be ";
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
            MyError myError = new MyError(message, ErrorCodes.BAD_PARAMETER_TYPE.getCode(), cause, ei, 
                    interpreter);
            error = Data.createErrorData(myError);
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
