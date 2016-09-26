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
public enum DataType
{
    ARRAY,
    BOOL,
    DICT,
    ERROR,
    FLOAT,
    ID,
    INT,
    NONE,
    STRING,
    STRUCT,
    TUPLE,
    VAR;
    
    @Override
    public String toString()
    {
        return name().toLowerCase().replaceAll("_", " ");
    }
}
