/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils.error_codes;

import pl.rcebula.internals.data_types.Data;

/**
 *
 * @author robert
 */
public enum ErrorCodes
{
    // upewnij się, że dla każdego errora jest przypisany inny kod
    NO_GLOBAL_VARIABLE(1),
    NO_LOCAL_VARIABLE(2),
    BAD_PARAMETER_TYPE(3),
    COLLECTIONS_DIFFRENT_SIZES(4),
    BUILTIN_FUNCTION_NOT_IMPLEMENTED(5),
    CALLBACK_ALREADY_ATTACHED(6),
    CALLBACK_NOT_ATTACHED(7),
    INDEX_OUT_OF_BOUNDS(8),
    DIVISION_BY_ZERO(9),
    SIZE_LESS_THAN_ZERO(10),
    MIN_GREATER_THAN_MAX(11),
    KEY_DOESNT_EXIST(12),
    NUMBER_OF_VARIABLES_GREATER_THAN_COLLECTION_SIZE(13),
    START_GREATER_THAN_END(14),
    CONVERSION_ERROR(15),
    REGEX_ERROR(16),
    USER_FUNCTION_DOESNT_EXIST(17),
    CALLBACK_PARAMS_GREATER_THAN_EVENT(18),
    TOO_LITTLE_PARAMETERS(19),
    NO_STACK_TRACE(20),
    STACK_OVERFLOW(21),
    HEAP_OVERFLOW(22),
    INTERVAL_LESS_THAN_ZERO(23),
    TCP_CONNECTION_ERROR(100),
    TCP_PORT_OUT_OF_RANGE(101),
    TCP_BAD_IP_ADDRESS(102),
    TCP_CONNECTION_DOESNT_EXIST(103);
    
    private final Data code;
            
    private ErrorCodes(int code)
    {
        this.code = Data.createIntData(code);
    }

    public Data getCode()
    {
        return code;
    }
}
