/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.data_types.Struct;
import pl.rcebula.internals.data_types.Tuple;

/**
 *
 * @author robert
 */
public class Datas
{
    public static String toStr(Data data)
    {
        return toStr(data, false);
    }
    
    public static String toStr(Data data, boolean inCollection)
    {
        String str = "";

        switch (data.getDataType())
        {
            case ARRAY:
                Data[] arr = (Data[])data.getValue();
                str += "[ ";
                for (Data d : arr)
                {
                    str += toStr(d, true) + ", ";
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
                    str += d.getKey() + ": " + toStr(d.getValue(), true) + ", ";
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
                if (inCollection)
                {
                    str += "\"" + s + "\"";
                }
                else
                {
                    str += s;
                }
                break;
            case TUPLE:
                Tuple tuple = (Tuple)data.getValue();
                str += "( ";
                for (i = 0; i < tuple.size(); ++i)
                {
                    Data d = tuple.get(i);
                    str += toStr(d, true);
                    if (i != tuple.size() - 1)
                    {
                        str += ", ";
                    }
                }
                str += " )";
                break;
            case STRUCT:
                str += "<";
                Struct struct = (Struct)data.getValue();
                // iterujemy po wszystkich polach struktury
                for (String fn : struct.getFieldsName())
                {
                    str += fn + ": " + toStr(struct.getField(fn), true) + ", ";
                }
                // jeżeli struktura zawierała jakieś pola
                if (struct.getFieldsName().size() > 0)
                {
                    // obcinamy dwa ostatnie znaki (średnik i spację)
                    str = str.substring(0, str.length() - 2);
                }
                str += ">";
                break;
            default:
                String message = "Unknown type " + data.getDataType().toString() + " in method toSTR()";
                throw new RuntimeException(message);
        }

        return str;
    }

    public static boolean equals(Data d1, Data d2)
    {
        // sprawdź czy referencje nie są te same
        if (d1 == d2)
        {
            return true;
        }

        // sprawdź czy liczby
        if (Numbers.isNumber(d1) && Numbers.isNumber(d2))
        {
            // sprawdź czy jedna z nich to nie float
            if (d1.getDataType().equals(DataType.FLOAT) || d2.getDataType().equals(DataType.FLOAT))
            {
                float val1 = Numbers.getFloat(d1);
                float val2 = Numbers.getFloat(d2);

                return val1 == val2;
            }
            // inaczej inty
            else
            {
                int val1 = Numbers.getInt(d1);
                int val2 = Numbers.getInt(d2);

                return val1 == val2;
            }
        }

        // sprawdź czy kolekcje
        if (Collections.isCollection(d1) && Collections.isCollection(d2))
        {
            Data[] datas1 = Collections.getDatas(d1);
            Data[] datas2 = Collections.getDatas(d2);

            // porównaj czy rozmiary te same
            if (datas1.length != datas2.length)
            {
                return false;
            }

            // porównaj wszystkie elementy
            // jeżeli przynajmniej jeden nie jest taki sam to false
            // inaczej true
            for (int i = 0; i < datas1.length; ++i)
            {
                if (!equals(datas1[i], datas2[i]))
                {
                    return false;
                }
            }

            return true;
        }

        // sprawdź czy typy te same
        if (!d1.getDataType().equals(d2.getDataType()))
        {
            return false;
        }

        switch (d1.getDataType())
        {
            case STRING:
                String str1 = (String)d1.getValue();
                String str2 = (String)d2.getValue();
                return str1.equals(str2);
            case BOOL:
                boolean bool1 = (boolean)d1.getValue();
                boolean bool2 = (boolean)d2.getValue();
                return bool1 == bool2;
            case NONE:
                return true;
            case ERROR:
                Data obj1 = ((MyError)d1.getValue()).getObject();
                Data obj2 = ((MyError)d2.getValue()).getObject();
                return equals(obj1, obj2);
            case DICT:
                HashMap<String, Data> dict1 = (HashMap<String, Data>)d1.getValue();
                HashMap<String, Data> dict2 = (HashMap<String, Data>)d2.getValue();

                // porównujemy rozmiary
                if (dict1.size() != dict2.size())
                {
                    return false;
                }

                // iterujemy po wszystkich wpisach w mapie 1
                Iterator<Entry<String, Data>> iterator = dict1.entrySet().iterator();
                while (iterator.hasNext())
                {
                    Entry<String, Data> entry = iterator.next();
                    String key = entry.getKey();
                    Data value = entry.getValue();

                    // pobieramy element pod tym samym kluczem w mapie 2
                    // jeżeli nie istnieje to zwracamy false
                    Data dict2Value = dict2.get(key);
                    if (dict2Value == null)
                    {
                        return false;
                    }

                    // jeżeli obiekty pod tym samym kluczem nie są równe to false
                    if (!equals(value, dict2Value))
                    {
                        return false;
                    }
                }

                return true;
            case STRUCT:
                Struct struct1 = (Struct)d1.getValue();
                Struct struct2 = (Struct)d2.getValue();
                
                // sprawdzamy czy obydwie struktury mają taką samą ilość pól
                if (struct1.getFieldsName().size() != struct2.getFieldsName().size())
                {
                    return false;
                }
                
                // sprawdzamy czy mają pola o tej samej nazwie i czy obiekty pod tymi polami są takie same
                for (String fn : struct1.getFieldsName())
                {
                    Data sd1 = struct1.getField(fn);
                    Data sd2 = struct2.getField(fn);
                    
                    if (sd2 == null)
                    {
                        return false;
                    }
                    
                    if (!equals(sd1, sd2))
                    {
                        return false;
                    }
                }
                
                return true;
            default:
                throw new RuntimeException("Unknown type " + d1.getDataType().toString());
        }
    }
}
