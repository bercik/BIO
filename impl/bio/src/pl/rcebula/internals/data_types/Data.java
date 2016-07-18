/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

import java.util.HashMap;

/**
 *
 * @author robert
 */
public class Data
{
    private final DataType dataType;
    private final Object value;

    public Data(DataType dataType, Object value)
    {
        this.dataType = dataType;
        this.value = value;
    }
    
    public static Data createDataInt(Integer i)
    {
        return new Data(DataType.INT, i);
    }
    
    public static Data createDataFloat(Float f)
    {
        return new Data(DataType.FLOAT, f);
    }
    
    public static Data createDataString(String s)
    {
        return new Data(DataType.STRING, s);
    }
    
    public static Data createDataBool(Boolean b)
    {
        return new Data(DataType.BOOL, b);
    }
    
    public Data createDataNone()
    {
        return new Data(DataType.NONE, null);
    }
    
    public static Data createDataArray(Data[] array)
    {
        return new Data(DataType.ARRAY, array);
    }
    
    public static Data createDataDict(HashMap<String, Data> dict)
    {
        return new Data(DataType.DICT, dict);
    }
    
    public Data createDataTuple(Tuple tuple)
    {
        return new Data(DataType.TUPLE, tuple);
    }
    
    public Data createDataError(Error error)
    {
        return new Data(DataType.ERROR, error);
    }
    
    public Data createDataId(String id)
    {
        return new Data(DataType.ID, id);
    }
    
    public DataType getDataType()
    {
        return dataType;
    }

    public Object getValue()
    {
        return value;
    }
}
