/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.data_types.Tuple;
import pl.rcebula.internals.interpreter.Interpreter;

/**
 *
 * @author robert
 */
public class IoModule extends Module
{
    @Override
    public String getName()
    {
        return "io";
    }
    
    public IoModule()
    {
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new PrintFunction());
    }
    
    private class PrintFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "PRINT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry <val>*
            String toPrint = "";
            for (Data d : params)
            {
                toPrint += toStr(d) + " ";
            }
            if (params.size() > 0)
            {
                toPrint = toPrint.substring(0, toPrint.length() - 1);
            }
            
            System.out.print(toPrint);
            
            return Data.createStringData(toPrint);
        }
        
        // w przyszłości przenieść do metody TO_STR w module conversion
        private String toStr(Data data)
        {
            String str = "";
            
            switch (data.getDataType())
            {
                case ARRAY:
                    Data[] arr = (Data[])data.getValue();
                    str += "[ ";
                    for (Data d : arr)
                    {
                        str += toStr(d) + ", ";
                    }
                    if (arr.length > 0)
                    {
                        str = str.substring(0, str.length() - 2);
                    }
                    str += " ]";
                    break;
                case BOOL:
                    Boolean b = (boolean)data.getValue();
                    str += b.toString();
                    break;
                case DICT:
                    HashMap<String, Data> dict = (HashMap<String, Data>)data.getValue();
                    str += "{ ";
                    for (Map.Entry<String, Data> d : dict.entrySet())
                    {
                        str += d.getKey() + ": " + toStr(d.getValue()) + ", ";
                    }
                    if (dict.size() > 0)
                    {
                        str = str.substring(0, str.length() - 2);
                    }
                    str += " }";
                    break;
                case ERROR:
                    MyError myError = (MyError)data.getValue();
                    str += myError.toString();
                    break;
                case FLOAT:
                    Float f = (float)data.getValue();
                    str += f.toString();
                    break;
                case INT:
                    Integer i = (int)data.getValue();
                    str += i.toString();
                    break;
                case NONE:
                    str += "none";
                    break;
                case STRING:
                    String s = (String)data.getValue();
                    str += s;
                    break;
                case TUPLE:
                    Tuple tuple = (Tuple)data.getValue();
                    str += "( ";
                    for (i = 0; i < tuple.size(); ++i)
                    {
                        Data d = tuple.get(i);
                        str += toStr(d);
                        if (i != tuple.size() - 1)
                        {
                            str += ", ";
                        }
                    }
                    str += " )";
                    break;
                default:
                    String message = "Unknown type " + data.getDataType().toString() + " in method toSTR()";
                    throw new RuntimeException(message);
            }
            
            return str;
        }
    }
}
