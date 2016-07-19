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
    private int line;
    private int chNum;

    public Data(DataType dataType, Object value)
    {
        this.dataType = dataType;
        this.value = value;
    }
    
    public Data(DataType dataType, Object value, int line, int chNum)
    {
        this.dataType = dataType;
        this.value = value;
        this.line = line;
        this.chNum = chNum;
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
    
    public static Data createDataNone()
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
    
    public static Data createDataTuple(Tuple tuple)
    {
        return new Data(DataType.TUPLE, tuple);
    }
    
    public static Data createDataError(MyError error)
    {
        return new Data(DataType.ERROR, error);
    }
    
    public static Data createDataId(String id)
    {
        return new Data(DataType.ID, id);
    }
    
    public static Data createDataVar(String var)
    {
        return new Data(DataType.VAR, var);
    }
    
    public DataType getDataType()
    {
        return dataType;
    }

    public Object getValue()
    {
        return value;
    }

    public void setLineAndChNum(int line, int chNum)
    {
        this.line = line;
        this.chNum = chNum;
    }

    public int getLine()
    {
        return line;
    }

    public int getChNum()
    {
        return chNum;
    }
}
