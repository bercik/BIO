/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules.utils;

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
    SIZE_LESS_THAN_ZERO(10);
    
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
