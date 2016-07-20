/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals;

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
    BUILTIN_FUNCTION_NOT_IMPLEMENTED(4);
    
    private final Data code;
            
    private ErrorCodes(int code)
    {
        this.code = Data.createDataInt(code);
    }

    public Data getCode()
    {
        return code;
    }
}
