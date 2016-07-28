/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules.utils.type_checker;

import java.util.List;
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
public class TypeChecker implements ITypeChecker
{
    private Data error;
    private boolean isError;
    
    public TypeChecker(Data actual, String funName, int paramNum, ErrorInfo ei, 
            Interpreter interpreter, DataType... expected)
    {
        check(actual, funName, paramNum, ei, interpreter, expected);
    }
    
    public TypeChecker(List<Data> actual, String funName, Interpreter interpreter, DataType... expected)
    {
        for (int i = 0; i < actual.size(); ++i)
        {
            if (!check(actual.get(i), funName, i, actual.get(i).getErrorInfo(), interpreter, expected))
            {
                break;
            }
        }
    }
    
    // zwraca false jeżeli błąd, true jeżeli nie ma błędu
    private boolean check(Data actual, String funName, int paramNum, ErrorInfo ei, 
            Interpreter interpreter, DataType... expected)
    {
        DataType actualDataType = actual.getDataType();
        isError = !equals(actualDataType, expected);
        
        if (isError)
        {
            String message = "expected " + paramNum + " parameter to be ";
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
            MyError myError = new MyError(funName, message, ErrorCodes.BAD_PARAMETER_TYPE.getCode(), 
                    cause, ei, interpreter);
            error = Data.createErrorData(myError);
            
            return false;
        }
        else
        {
            error = null;
            
            return true;
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
}
