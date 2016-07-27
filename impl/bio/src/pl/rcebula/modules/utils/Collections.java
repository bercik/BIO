/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules.utils;

import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.Tuple;

/**
 *
 * @author robert
 */
public class Collections
{
    public static Data[] getDatas(Data collection)
    {
        if (collection.getDataType().equals(DataType.ARRAY))
        {
            return (Data[])collection.getValue();
        }
        else if (collection.getDataType().equals(DataType.TUPLE))
        {
            return ((Tuple)collection.getValue()).getValues();
        }
        else
        {
            String message = "Passed parameter is not collection";
            throw new RuntimeException(message);
        }
    }

    public static boolean isCollection(Data data)
    {
        return data.getDataType().equals(DataType.ARRAY)
                || data.getDataType().equals(DataType.TUPLE);
    }
}
