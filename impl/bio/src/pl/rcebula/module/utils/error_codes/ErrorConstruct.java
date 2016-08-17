/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils.error_codes;

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.interpreter.Interpreter;

/**
 *
 * @author robert
 */
public class ErrorConstruct
{
    public static Data COLLECTIONS_DIFFRENT_SIZES(String funName, ErrorInfo ei, Interpreter interpreter,
            int param)
    {
        String message = "collection passed as " + param + " parameter differs in size with previous";

        MyError myError = new MyError(funName, message,
                ErrorCodes.COLLECTIONS_DIFFRENT_SIZES.getCode(), null, ei, interpreter);

        return Data.createErrorData(myError);
    }

    public static Data DIVISION_BY_ZERO(String funName, ErrorInfo ei, Interpreter interpreter)
    {
        String message = "division by zero";

        MyError error = new MyError(funName, message, ErrorCodes.DIVISION_BY_ZERO.getCode(), null, ei,
                interpreter);

        return Data.createErrorData(error);
    }

    public static Data MIN_GREATER_THAN_MAX(String funName, ErrorInfo ei, Interpreter interpreter)
    {
        String message = "min is greater than max";

        MyError error = new MyError(funName, message, ErrorCodes.MIN_GREATER_THAN_MAX.getCode(), null,
                ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data START_GREATER_THAN_END(String funName, ErrorInfo ei, Interpreter interpreter)
    {
        String message = "start is greater than end";

        MyError error = new MyError(funName, message, ErrorCodes.START_GREATER_THAN_END.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data SIZE_LESS_THAN_ZERO(String funName, ErrorInfo ei, Interpreter interpreter)
    {
        String message = "size is less than zero";

        MyError error = new MyError(funName, message, ErrorCodes.SIZE_LESS_THAN_ZERO.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data INDEX_OUT_OF_BOUNDS(String funName, ErrorInfo ei, Interpreter interpreter,
            int index)
    {
        String message = "Index " + index + " out of collection bounds";

        MyError error = new MyError(funName, message, ErrorCodes.INDEX_OUT_OF_BOUNDS.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data KEY_DOESNT_EXIST(String funName, ErrorInfo ei, Interpreter interpreter, String key)
    {
        String message = "key " + key + " doesn't exists";

        MyError error = new MyError(funName, message, ErrorCodes.KEY_DOESNT_EXIST.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data NUMBER_OF_VARIABLES_GREATER_THAN_COLLECTION_SIZE(String funName,
            ErrorInfo ei, Interpreter interpreter, int numberOfVariables, int collectionSize)
    {
        String message = "given " + numberOfVariables + " variables is greater than collection size "
                + collectionSize;

        MyError error = new MyError(funName, message,
                ErrorCodes.NUMBER_OF_VARIABLES_GREATER_THAN_COLLECTION_SIZE.getCode(), null,
                ei, interpreter);

        return Data.createErrorData(error);
    }
    
    public static Data CONVERSION_ERROR(String funName, ErrorInfo ei, Interpreter interpreter, 
            Data actual, DataType toWhat)
    {
        String message = "can't convert " + actual.getDataType().toString() + " " + actual.getValue().toString()
                + " to " + toWhat.toString();
        
        MyError error = new MyError(funName, message, ErrorCodes.CONVERSION_ERROR.getCode(), null, 
                ei, interpreter);
        
        return Data.createErrorData(error);
    }
    
    public static Data REGEX_ERROR(String funName, ErrorInfo ei, Interpreter interpreter, String regex)
    {
        String message = "regex \"" + regex + "\" is invalid";
        
        MyError error = new MyError(funName, message, ErrorCodes.REGEX_ERROR.getCode(), null, ei, 
                interpreter);
        
        return Data.createErrorData(error);
    }
}
