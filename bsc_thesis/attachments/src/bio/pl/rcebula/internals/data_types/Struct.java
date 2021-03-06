/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pl.rcebula.utils.Pair;
import pl.rcebula.utils.Triple;

/**
 *
 * @author robert
 */
public class Struct
{
    private final Map<String, Data> fields = new HashMap<>();
    
    public Data getField(String fieldName)
    {
        return fields.get(fieldName);
    }
    
    public void addField(String fieldName, Data data)
    {
        fields.put(fieldName, data);
    }
    
    public Set<String> getFieldsName()
    {
        return fields.keySet();
    }
    
    private static String constructStructName(List<String> ids)
    {
        String str = "";
        for (int i = 0; i < ids.size() - 1; ++i)
        {
            str += ids.get(i) + ".";
        }
        
        str += ids.get(ids.size() - 1);
        
        return str;
    }
    
    public static Triple<Data, String, MyError> get(String id, Map<String, Data> variables)
    {
        String errorMsg = "";
        Data toReturn = null;
        
        List<String> splited = Arrays.asList(id.split("\\."));
        
        id = splited.get(0);
        Data d = variables.get(id);
        
        // sprawdzamy czy istnieje
        if (d == null)
        {
            errorMsg = "Variable " + id + " doesn't exist";
            return new Triple<>(toReturn, errorMsg, null);
        }
        
        // iterujemy po wszystkich członach poza pierwszym
        for (int i = 1; i < splited.size(); ++i)
        {
            // sprawdzamy czy struktura
            if (!d.getDataType().equals(DataType.STRUCT))
            {
                // jeżeli error
                if (d.getDataType().equals(DataType.ERROR))
                {
                    errorMsg = constructStructName(splited.subList(0, i)) + " is error";
                    MyError cause = (MyError)d.getValue();
                    return new Triple<>(toReturn, errorMsg, cause);
                }
                
                errorMsg = constructStructName(splited.subList(0, i)) + " is not struct";
                return new Triple<>(toReturn, errorMsg, null);
            }
            
            Struct struct = (Struct)d.getValue();
            // pobieramy pole
            id = splited.get(i);
            d = struct.getField(id);
            
            // sprawdzamy czy istnieje
            if (d == null)
            {
                errorMsg = "In " + constructStructName(splited.subList(0, i)) + " field " + id + " doens't exist";
                return new Triple<>(toReturn, errorMsg, null);
            }
        }
        
        return new Triple<>(d, "", null);
    }
    
    public static boolean exists(String id, Map<String, Data> variables)
    {
        List<String> splited = Arrays.asList(id.split("\\."));
        
        id = splited.get(0);
        Data d = variables.get(id);
        
        // sprawdzamy czy istnieje
        if (d == null)
        {
            return false;
        }
        
        // iterujemy po wszystkich członach poza pierwszym
        for (int i = 1; i < splited.size(); ++i)
        {
            // sprawdzamy czy struktura
            if (!d.getDataType().equals(DataType.STRUCT))
            {
                return false;
            }
            
            Struct struct = (Struct)d.getValue();
            // pobieramy pole
            id = splited.get(i);
            d = struct.getField(id);
            
            // sprawdzamy czy istnieje
            if (d == null)
            {
                return false;
            }
        }
        
        return true;
    }
    
    public static Pair<String, Data> create(String id, Data data, Map<String, Data> variables)
    {
        List<String> splited = Arrays.asList(id.split("\\."));
        // jeżeli po rozdzieleniu dostaliśmy więcej niż jeden łańcuch oznacza to, że mamy do czynienia z strukturą
        if (splited.size() > 1)
        {
            // pobieramy pierwszy człon
            id = splited.get(0);
            // pobieramy zmienną pod pierwszym członem
            Data d = variables.get(splited.get(0));
            Data newData = d;
            // jeżeli zmienna o tym id już istniała i była strukturą
            if (d != null && d.getDataType().equals(DataType.STRUCT))
            {
                // iterujemy po pozostałych członach poza ostatnim
                for (int i = 1; i < splited.size() - 1; ++i)
                {
                    // pobieramy strukturę
                    Struct struct = (Struct)d.getValue();
                    // pobieramy kolejne pole z struktury
                    d = struct.getField(splited.get(i));
                    
                    // jeżeli istnieje i jest strukturą
                    if (d != null && d.getDataType().equals(DataType.STRUCT))
                    {
                        continue;
                    }
                    // inaczej tworzymy od zera
                    else
                    {
                        d = createStructFromScratch(splited.subList(i + 1, splited.size()), data);
                        struct.addField(splited.get(i), d);
                        
                        return new Pair<>(id, newData);
                    }
                }
                
                // dodajemy pole
                Struct struct = (Struct)d.getValue();
                struct.addField(splited.get(splited.size() - 1), data);
                
                return new Pair<>(id, newData);
            }
            // inaczej tworzymy od zera
            else
            {
                data = createStructFromScratch(splited.subList(1, splited.size()), data);
                return new Pair<>(id, data);
            }
        }
        // inaczej zwracamy to co nam przekazano
        else
        {
            return new Pair<>(id, data);
        }
    }
    
    public static Struct createStructTreeFromScratch(Struct struct, String id, Data data)
    {
        String[] ids = id.split("\\.");
        return createStructTreeFromScratch(struct, Arrays.asList(ids), data);
    }
    
    private static Struct createStructTreeFromScratch(Struct struct, List<String> ids, Data data)
    {
        Struct toRet = struct;
        
        for (String id : ids)
        {
            // jeżeli nie ostatni element
            if (id != ids.get(ids.size() - 1))
            {
                Struct tmpStruct = new Struct();
                struct.addField(id, Data.createStructData(tmpStruct));
                struct = tmpStruct;
            }
            // jeżeli ostatni element
            else
            {
                struct.addField(id, data);
            }
        }
        
        return toRet;
    }
    
    private static Data createStructFromScratch(List<String> ids, Data data)
    {
        Struct struct = new Struct();
        return Data.createStructData(createStructTreeFromScratch(struct, ids, data));
    }
}
