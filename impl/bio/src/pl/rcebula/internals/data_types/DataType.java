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
    // pass by value, mutable
    ARRAY(false, true),
    BOOL(true, true),
    DICT(false, true),
    FLOAT(true, true),
    ID(false, false),
    INT(true, true),
    NONE(false, false),
    STRING(false, false),
    TUPLE(false, false),
    ERROR(false, false);
    
    private final boolean passByValue;
    private final boolean mutable;

    private DataType(boolean passByValue, boolean mutable)
    {
        this.passByValue = passByValue;
        this.mutable = mutable;
    }

    public boolean isPassByValue()
    {
        return passByValue;
    }

    public boolean isMutable()
    {
        return mutable;
    }
}
