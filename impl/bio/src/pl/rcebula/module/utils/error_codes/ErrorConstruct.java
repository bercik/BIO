/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils.error_codes;

import java.util.List;
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
    public static Data BAD_PARAMETER_TYPE(String funName, ErrorInfo ei,
            Interpreter interpreter, Data actual, int paramNum, List<DataType> expected)
    {
        DataType actualDataType = actual.getDataType();
        
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

        return Data.createErrorData(myError);
    }

    public static Data END_FOREACH_NOT_INSIDE_FOREACH(String funName, ErrorInfo ei,
            Interpreter interpreter)
    {
        String message = "END_FOREACH function call isn't from function called by FOREACH";

        MyError error = new MyError(message,
                ErrorCodes.END_FOREACH_NOT_INSIDE_FOREACH.getCode(), null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data INTERVAL_LESS_THAN_ZERO(String funName, ErrorInfo ei, Interpreter interpreter,
            int interval)
    {
        String message = "interval " + interval + " is less than zero";

        MyError error = new MyError(funName, message, ErrorCodes.INTERVAL_LESS_THAN_ZERO.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data TCP_CONNECTION_DOESNT_EXIST(String funName, ErrorInfo ei, Interpreter interpreter,
            int connId)
    {
        String message = "tcp connection with " + connId + " id doesn't exist";

        MyError myError = new MyError(funName, message,
                ErrorCodes.TCP_CONNECTION_DOESNT_EXIST.getCode(), null, ei, interpreter);

        return Data.createErrorData(myError);
    }

    public static Data TCP_BAD_IP_ADDRESS(String funName, ErrorInfo ei, Interpreter interpreter,
            String ipAddr)
    {
        String message = "tcp ip address " + ipAddr + " is bad";

        MyError myError = new MyError(funName, message, ErrorCodes.TCP_BAD_IP_ADDRESS.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(myError);
    }

    public static Data TCP_PORT_OUT_OF_RANGE(String funName, ErrorInfo ei, Interpreter interpreter,
            int port)
    {
        String message = "tcp port " + port + " out of range";

        MyError myError = new MyError(funName, message, ErrorCodes.TCP_PORT_OUT_OF_RANGE.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(myError);
    }

    public static Data TCP_CONNECTION_ERROR(String funName, ErrorInfo ei, Interpreter interpreter)
    {
        String message = "tcp connection error";

        MyError myError = new MyError(funName, message, ErrorCodes.TCP_CONNECTION_ERROR.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(myError);
    }

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
        String message = "can't convert " + actual.getDataType().toString() + " ";
        if (actual.getDataType().equals(DataType.STRING))
        {
            message += "\"" + actual.getValue().toString() + "\"";
        }
        else
        {
            message += actual.getValue().toString();
        }
        message += " to " + toWhat.toString();

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

    public static Data USER_FUNCTION_DOESNT_EXIST(String funName, ErrorInfo ei, Interpreter interpreter,
            String function)
    {
        String message = "user function " + function + " doesn't exist";

        MyError error = new MyError(funName, message,
                ErrorCodes.USER_FUNCTION_DOESNT_EXIST.getCode(), null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data CALLBACK_PARAMS_GREATER_THAN_EVENT(String funName, ErrorInfo ei,
            Interpreter interpreter, String eventName, int eventParams, String callbackName, int callbackParams)
    {
        String message = "callback " + callbackName + " takes " + callbackParams + " parameters which is "
                + "greater than event " + eventName + " " + eventParams + " parameters";

        MyError error = new MyError(funName, message,
                ErrorCodes.CALLBACK_PARAMS_GREATER_THAN_EVENT.getCode(), null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data TOO_LITTLE_PARAMETERS(String funName, ErrorInfo ei, Interpreter interpreter,
            String callFunName, int callFunNameParams, int passedParams)
    {
        String message = "function " + callFunName + " takes " + callFunNameParams + " parameters, got "
                + passedParams;

        MyError error = new MyError(funName, message, ErrorCodes.TOO_LITTLE_PARAMETERS.getCode(),
                null, ei, interpreter);

        return Data.createErrorData(error);
    }

    public static Data NO_STACK_TRACE(String funName, ErrorInfo ei, Interpreter interpreter,
            int nthStackTrace)
    {
        String message = "there is no " + nthStackTrace + " stack trace";

        MyError error = new MyError(funName, message, ErrorCodes.NO_STACK_TRACE.getCode(), null, ei,
                interpreter);

        return Data.createErrorData(error);
    }
}
