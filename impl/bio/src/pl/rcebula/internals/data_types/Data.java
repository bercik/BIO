/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

import java.util.HashMap;
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public class Data
{
    private final DataType dataType;
    private final Object value;
    private ErrorInfo errorInfo;

    public Data(Data other)
    {
        this.dataType = other.dataType;
        this.value = other.value;
    }
    
    public Data(DataType dataType, Object value)
    {
        this.dataType = dataType;
        this.value = value;
    }
    
    public Data(DataType dataType, Object value, ErrorInfo errorInfo)
    {
        this.dataType = dataType;
        this.value = value;
        this.errorInfo = errorInfo;
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

    public void setErrorInfo(ErrorInfo errorInfo)
    {
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo()
    {
        return errorInfo;
    }

    @Override
    public String toString()
    {
        String str = "";
        str += dataType.toString() + ": " + value.toString();
        return str;
    }
}
