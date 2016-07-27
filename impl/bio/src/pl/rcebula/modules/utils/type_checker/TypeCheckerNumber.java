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
public class TypeCheckerNumber
{
    private final Data error;
    private final boolean isError;
    private final boolean isFloat;
    
    public TypeCheckerNumber(Data actual, String funName, int paramNum, ErrorInfo ei, 
            Interpreter interpreter)
    {
        DataType actualDataType = actual.getDataType();
        
        // sprawdzamy czy typ danych to int
        if (actualDataType.equals(DataType.INT))
        {
            isError = false;
            error = null;
            isFloat = false;
        }
        // inaczej czy float
        else if (actualDataType.equals(DataType.FLOAT))
        {
            isError = false;
            error = null;
            isFloat = true;
        }
        // inaczej błąd
        else
        {
            isError = true;
            isFloat = false;
            
            String message = "In function " + funName + " expected " + paramNum + " parameter to be " + 
                    DataType.INT.toString() + " or " + DataType.FLOAT.toString();
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
    }
    
    private boolean equals(DataType actual, DataType... expected)
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

    public boolean isFloat()
    {
        return isFloat;
    }
}
