/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;

/**
 *
 * @author robert
 */
public class Datas
{
    public static boolean equals(Data d1, Data d2)
    {
        // TODO
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
            default:
                throw new RuntimeException("Unknown type " + d1.getDataType().toString());
        }
    }
}
