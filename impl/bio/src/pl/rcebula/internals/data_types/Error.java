/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

/**
 *
 * @author robert
 */
public class Error
{
    private final String message;
    private final Data object;
    private final Error cause;

    public Error(String message, Data object, Error cause)
    {
        this.message = message;
        this.object = object;
        this.cause = cause;
    }

    public String getMessage()
    {
        return message;
    }

    public Data getObject()
    {
        return object;
    }

    public Error getCause()
    {
        return cause;
    }
}
