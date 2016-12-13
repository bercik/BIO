/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils.type_checker;

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.utils.error_codes.ErrorCodes;

/**
 *
 * @author robert
 */
public class TypeCheckerNumberCollection implements ITypeCheckerNumber
{
    private final Data error;
    private final boolean isError;
    private final boolean isFloat;
    
    // !!! paramNum jest zwiększane o jeden !!!
    // !!! elementNum zwiększane o jeden !!!
    public TypeCheckerNumberCollection(Data actual, String funName, int paramNum, int elementNum, 
            ErrorInfo ei, Interpreter interpreter)
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
            
            String message = "in passed parameter " + (paramNum + 1) + " collection, "
                    + "expected " + (elementNum + 1) + " element to be " + DataType.INT.toString() + " or " + 
                    DataType.FLOAT.toString();
            message += " got " + actualDataType.toString();
            
            MyError cause = null;
            if (actual.getDataType().equals(DataType.ERROR))
            {
                cause = (MyError)actual.getValue();
            }
            MyError myError = new MyError(funName, message, ErrorCodes.BAD_PARAMETER_TYPE.getCode(), cause, ei, 
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
    
    @Override
    public boolean isError()
    {
        return isError;
    }

    @Override
    public Data getError()
    {
        return error;
    }

    @Override
    public boolean isFloat()
    {
        return isFloat;
    }
}
