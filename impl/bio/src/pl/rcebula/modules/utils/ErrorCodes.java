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
    BUILTIN_FUNCTION_NOT_IMPLEMENTED(4),
    CALLBACK_ALREADY_ATTACHED(5),
    CALLBACK_NOT_ATTACHED(6);
    
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
