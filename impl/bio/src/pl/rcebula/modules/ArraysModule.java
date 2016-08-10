/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.Tuple;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.modules.utils.Collections;
import pl.rcebula.modules.utils.Numbers;
import pl.rcebula.modules.utils.error_codes.ErrorConstruct;
import pl.rcebula.modules.utils.type_checker.TypeChecker;
import pl.rcebula.modules.utils.type_checker.TypeCheckerCollection;
import pl.rcebula.modules.utils.type_checker.TypeCheckerNumberCollection;

/**
 *
 * @author robert
 */
public class ArraysModule extends Module
{
    @Override
    public String getName()
    {
        return "arrays";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new CreateTupleFunction());
        putFunction(new GetFunction());
        putFunction(new CreateTupleOfElementsFunction());
        putFunction(new RangeFunction());
        putFunction(new CreateArrayFunction());
        putFunction(new CreateDictFunction());
        putFunction(new SetFunction());
        putFunction(new SizeFunction());
        putFunction(new CopyFunction());
        putFunction(new DeepCopyFunction());
        putFunction(new ExtendArrayFunction());
        putFunction(new UnpackFunction());
        putFunction(new ContainsFunction());
        putFunction(new ContainsKeyFunction());
        putFunction(new ContainsValueFunction());
        putFunction(new CountElementsFunction());
        putFunction(new SortAscFunction());
        putFunction(new SortDescFunction());
    }

    private class SortAscFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SORT_ASC";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data col = params.get(0);

            // sprawdź czy kolekcja
            TypeChecker tc = new TypeChecker(col, getName(), 0, col.getErrorInfo(), interpreter, DataType.TUPLE,
                    DataType.ARRAY);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz tablicę
            Data[] datas = Collections.getDatas(col);
            // sprawdź czy rozmiar większy od zera, jeżeli nie to zwróć nową pustą tablicę
            if (datas.length == 0)
            {
                return Data.createArrayData(new Data[0]);
            }

            // pobierz pierwszy element i sprawdź czy number lub string
            Data firstEl = datas[0];
            TypeCheckerCollection tcc = new TypeCheckerCollection(firstEl, getName(), 0, 0, firstEl.getErrorInfo(),
                    interpreter, DataType.INT, DataType.FLOAT, DataType.STRING);
            if (tcc.isError())
            {
                return tcc.getError();
            }

            // stwórz nową tablicę
            Data[] sorted = new Data[datas.length];

            // jeżeli number
            if (Numbers.isNumber(firstEl))
            {
                // tworzymy tablicę number
                Numbers.Number[] tmp = new Numbers.Number[datas.length];

                // iterujemy po wszystkich elementach i dodajemy do tablicy
                for (int i = 0; i < datas.length; ++i)
                {
                    Data d = datas[i];
                    // sprawdzamy czy int lub float
                    TypeCheckerNumberCollection tcnc = new TypeCheckerNumberCollection(d, getName(), 0, i, 
                            d.getErrorInfo(), interpreter);
                    if (tcnc.isError())
                    {
                        return tcnc.getError();
                    }
                    
                    // jeżeli float
                    if (tcnc.isFloat())
                    {
                        tmp[i] = new Numbers.Number((float)d.getValue());
                    }
                    // inaczej int
                    else
                    {
                        tmp[i] = new Numbers.Number((int)d.getValue());
                    }
                }
                
                // sortujemy
                Arrays.sort(tmp);
                
                // dodajemy do tablicy data
                for (int i = 0; i < tmp.length; ++i)
                {
                    Numbers.Number n = tmp[i];
                    if (n.getType().equals(Numbers.NumberType.FLOAT))
                    {
                        sorted[i] = Data.createFloatData(n.getFloat());
                    }
                    else
                    {
                        sorted[i] = Data.createIntData(n.getInt());
                    }
                }
            }
            // inaczej string
            else
            {
                // tworzymy tablicę stringów
                String[] tmp = new String[datas.length];

                // iterujemy po wszystkich elementach i dodajemy do tablicy
                for (int i = 0; i < datas.length; ++i)
                {
                    Data d = datas[i];
                    // sprawdzamy czy string
                    tcc = new TypeCheckerCollection(d, getName(), 0, i, d.getErrorInfo(), interpreter, 
                            DataType.STRING);
                    if (tcc.isError())
                    {
                        return tcc.getError();
                    }
                    
                    tmp[i] = (String)d.getValue();
                }
                
                // sortujemy
                Arrays.sort(tmp);
                
                // dodajemy do tablicy data
                for (int i = 0; i < tmp.length; ++i)
                {
                    sorted[i] = Data.createStringData(tmp[i]);
                }
            }
            
            // zwracamy sorted jako tablicę
            return Data.createArrayData(sorted);
        }
    }
    
    private class SortDescFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SORT_DESC";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data col = params.get(0);

            // sprawdź czy kolekcja
            TypeChecker tc = new TypeChecker(col, getName(), 0, col.getErrorInfo(), interpreter, DataType.TUPLE,
                    DataType.ARRAY);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz tablicę
            Data[] datas = Collections.getDatas(col);
            // sprawdź czy rozmiar większy od zera, jeżeli nie to zwróć nową pustą tablicę
            if (datas.length == 0)
            {
                return Data.createArrayData(new Data[0]);
            }

            // pobierz pierwszy element i sprawdź czy number lub string
            Data firstEl = datas[0];
            TypeCheckerCollection tcc = new TypeCheckerCollection(firstEl, getName(), 0, 0, firstEl.getErrorInfo(),
                    interpreter, DataType.INT, DataType.FLOAT, DataType.STRING);
            if (tcc.isError())
            {
                return tcc.getError();
            }

            // stwórz nową tablicę
            Data[] sorted = new Data[datas.length];

            // jeżeli number
            if (Numbers.isNumber(firstEl))
            {
                // tworzymy tablicę number
                Numbers.Number[] tmp = new Numbers.Number[datas.length];

                // iterujemy po wszystkich elementach i dodajemy do tablicy
                for (int i = 0; i < datas.length; ++i)
                {
                    Data d = datas[i];
                    // sprawdzamy czy int lub float
                    TypeCheckerNumberCollection tcnc = new TypeCheckerNumberCollection(d, getName(), 0, i, 
                            d.getErrorInfo(), interpreter);
                    if (tcnc.isError())
                    {
                        return tcnc.getError();
                    }
                    
                    // jeżeli float
                    if (tcnc.isFloat())
                    {
                        tmp[i] = new Numbers.Number((float)d.getValue());
                    }
                    // inaczej int
                    else
                    {
                        tmp[i] = new Numbers.Number((int)d.getValue());
                    }
                }
                
                // sortujemy
                Arrays.sort(tmp);
                
                // dodajemy do tablicy data
                for (int i = 0; i < tmp.length; ++i)
                {
                    Numbers.Number n = tmp[i];
                    if (n.getType().equals(Numbers.NumberType.FLOAT))
                    {
                        sorted[tmp.length - i - 1] = Data.createFloatData(n.getFloat());
                    }
                    else
                    {
                        sorted[tmp.length - i - 1] = Data.createIntData(n.getInt());
                    }
                }
            }
            // inaczej string
            else
            {
                // tworzymy tablicę stringów
                String[] tmp = new String[datas.length];

                // iterujemy po wszystkich elementach i dodajemy do tablicy
                for (int i = 0; i < datas.length; ++i)
                {
                    Data d = datas[i];
                    // sprawdzamy czy string
                    tcc = new TypeCheckerCollection(d, getName(), 0, i, d.getErrorInfo(), interpreter, 
                            DataType.STRING);
                    if (tcc.isError())
                    {
                        return tcc.getError();
                    }
                    
                    tmp[i] = (String)d.getValue();
                }
                
                // sortujemy
                Arrays.sort(tmp);
                
                // dodajemy do tablicy data
                for (int i = 0; i < tmp.length; ++i)
                {
                    sorted[tmp.length - i - 1] = Data.createStringData(tmp[i]);
                }
            }
            
            // zwracamy sorted jako tablicę
            return Data.createArrayData(sorted);
        }
    }

    private class CountElementsFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "COUNT_ELEMENTS";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data el = params.get(1);

            // sprawdź czy par1 jest kolekcją lub dict
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.DICT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeżeli kolekcja
            if (Collections.isCollection(par1))
            {
                // pobierz tablicę
                Data[] datas = Collections.getDatas(par1);

                // iteruj po wszystkich elementach i zliczaj wystąpienia
                int counter = 0;
                for (int i = 0; i < datas.length; ++i)
                {
                    List<Data> p = Arrays.asList(datas[i], el);
                    Data resEQ = interpreter.getBuiltinFunctions().callFunction("EQ", p, currentFrame, interpreter,
                            datas[i].getErrorInfo());

                    // jeżeli zwrócono error to zwróć
                    if (resEQ.getDataType().equals(DataType.ERROR))
                    {
                        return resEQ;
                    }
                    // jeżeli true to dodaj
                    if ((boolean)resEQ.getValue() == true)
                    {
                        ++counter;
                    }
                }

                // zwróc ilość
                return Data.createIntData(counter);
            }
            // inaczej dict
            else
            {
                // pobierz mapę
                HashMap<String, Data> map = (HashMap<String, Data>)par1.getValue();

                // iteruj po wszystkich elementach i zliczaj wystąpienia
                int counter = 0;
                for (Map.Entry<String, Data> entry : map.entrySet())
                {
                    List<Data> p = Arrays.asList(entry.getValue(), el);
                    Data resEQ = interpreter.getBuiltinFunctions().callFunction("EQ", p, currentFrame, interpreter,
                            entry.getValue().getErrorInfo());

                    // jeżeli zwrócono error to zwróć
                    if (resEQ.getDataType().equals(DataType.ERROR))
                    {
                        return resEQ;
                    }
                    // jeżeli true to dodaj
                    if ((boolean)resEQ.getValue() == true)
                    {
                        ++counter;
                    }
                }

                // zwróc ilość
                return Data.createIntData(counter);
            }
        }
    }

    private class ContainsKeyFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CONTAINS_KEY";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data dict = params.get(0);
            Data key = params.get(1);

            // sprawdź czy dict jest dict
            TypeChecker tc = new TypeChecker(dict, getName(), 0, dict.getErrorInfo(), interpreter, DataType.DICT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // sprawdź czy key jest string
            tc = new TypeChecker(key, getName(), 0, key.getErrorInfo(), interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }

            HashMap<String, Data> map = (HashMap<String, Data>)dict.getValue();
            String skey = (String)key.getValue();

            return Data.createBoolData(map.containsKey(skey));
        }
    }

    private class ContainsValueFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CONTAINS_VALUE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data dict = params.get(0);
            Data val = params.get(1);

            // sprawdź czy dict jest dict
            TypeChecker tc = new TypeChecker(dict, getName(), 0, dict.getErrorInfo(), interpreter, DataType.DICT);
            if (tc.isError())
            {
                return tc.getError();
            }

            HashMap<String, Data> map = (HashMap<String, Data>)dict.getValue();

            // przejdź po wszystkich elementach i porównaj
            for (Map.Entry<String, Data> entry : map.entrySet())
            {
                List<Data> p = Arrays.asList(entry.getValue(), val);
                Data resEQ = interpreter.getBuiltinFunctions().callFunction("EQ", p, currentFrame, interpreter,
                        entry.getValue().getErrorInfo());

                // jeżeli zwrócono error lub true to zwróć
                if (resEQ.getDataType().equals(DataType.ERROR)
                        || (boolean)resEQ.getValue() == true)
                {
                    return resEQ;
                }
            }

            // zwróć false
            return Data.createBoolData(false);
        }
    }

    private class CreateArrayFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CREATE_ARRAY";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);

            // sprawdź czy typu int
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz rozmiar
            int size = (int)par.getValue();

            // sprawdź czy nie liczba ujemna
            if (size < 0)
            {
                // zwróc błąd
                return ErrorConstruct.SIZE_LESS_THAN_ZERO(getName(), par.getErrorInfo(), interpreter);
            }

            // stwórz tablicę o takim rozmiarze i wypełnij elementami none
            Data[] arr = new Data[size];
            Arrays.fill(arr, Data.createNoneData());

            // zwróć
            return Data.createArrayData(arr);
        }
    }

    private class CreateDictFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CREATE_DICT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>*
            // tworzymy pomocnicze listy
            List<String> keys = new ArrayList<>();
            List<Data> values = new ArrayList<>();
            // sprawdzamy czy przekazano jakieś
            if (params.size() > 0)
            {
                for (int i = 0; i < params.size(); i += 2)
                {
                    // pobieramy po dwa parametry
                    Data p1 = params.get(i);
                    Data p2 = params.get(i + 1);

                    // sprawdzamy czy pierwszy jest stringiem
                    TypeChecker tc = new TypeChecker(p1, getName(), i, p1.getErrorInfo(), interpreter,
                            DataType.STRING);
                    if (tc.isError())
                    {
                        return tc.getError();
                    }

                    // dodajemy do list
                    keys.add((String)p1.getValue());
                    values.add(p2);
                }
            }

            // tworzymy słownik i wypełniamy elementami
            HashMap<String, Data> map = new HashMap<>();
            Iterator<String> strIt = keys.iterator();
            Iterator<Data> dataIt = values.iterator();
            while (strIt.hasNext())
            {
                String key = strIt.next();
                Data value = dataIt.next();
                map.put(key, value);
            }

            // zwracamy
            return Data.createDictData(map);
        }
    }

    private class CreateTupleFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CREATE_TUPLE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return Data.createTupleData(new Tuple(params.toArray(new Data[0])));
        }
    }

    private class CreateTupleOfElementsFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CREATE_TUPLE_OF_ELEMENTS";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);

            // sprawdź czy pierwszy parametr jest intem
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz rozmiar i sprawdź czy nie mniejszy od zera
            int size = Numbers.getInt(par1);
            if (size < 0)
            {
                return ErrorConstruct.SIZE_LESS_THAN_ZERO(getName(), par1.getErrorInfo(), interpreter);
            }

            // stwórz tablicę Data i wypełnij elementami par2
            Data[] datas = new Data[size];
            Arrays.fill(datas, par2);

            // zwróc tuplę
            return Data.createTupleData(new Tuple(datas));
        }
    }

    private class SetFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SET";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);
            Data par3 = params.get(2);

            // sprawdź czy pierwszy parametr to collection lub dict
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.DICT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeżeli kolekcja
            if (Collections.isArray(par1))
            {
                // sprawdź czy drugi parametr to int
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.INT);
                if (tc.isError())
                {
                    return tc.getError();
                }

                // pobierz index
                int index = (int)par2.getValue();
                // pobierz tablicę
                Data[] datas = (Data[])par1.getValue();
                // sprawdź czy index nie wychodzi poza granicę tablicy
                if (index < 0 || index >= datas.length)
                {
                    return ErrorConstruct.INDEX_OUT_OF_BOUNDS(getName(), par2.getErrorInfo(), interpreter,
                            index);
                }
                // ustaw wartość pod indeksem index
                datas[index] = par3;
            }
            // inaczej dict
            else
            {
                // sprawdź czy drugi parametr to string
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.STRING);
                if (tc.isError())
                {
                    return tc.getError();
                }

                // pobierz key
                String key = (String)par2.getValue();
                // pobierz mapę
                HashMap<String, Data> map = (HashMap<String, Data>)par1.getValue();
                // ustaw wartość pod kluczem key
                map.put(key, par3);
            }

            // zwracamy parametr 3
            return par3;
        }
    }

    private class GetFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);

            // sprawdź czy pierwszy parametr to kolekcja lub dict
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.TUPLE, DataType.DICT);
            if (tc.isError())
            {
                return tc.getError();
            }
            // jeżeli kolekcja
            if (Collections.isCollection(par1))
            {
                // sprawdź czy drugi parametr jest typu int
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.INT);
                if (tc.isError())
                {
                    return tc.getError();
                }

                Data[] datas = Collections.getDatas(par1);
                int index = (int)par2.getValue();
                // sprawdź czy index nie wykracza poza granicę datas
                if (index < 0 || index >= datas.length)
                {
                    return ErrorConstruct.INDEX_OUT_OF_BOUNDS(getName(), par2.getErrorInfo(), interpreter, index);
                }
                // zwróc element o indeksie index z kolekcji datas
                return datas[index];
            }
            // inaczej dict
            else
            {
                // sprawdź czy drugi parametr jest typu string
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.STRING);
                if (tc.isError())
                {
                    return tc.getError();
                }

                HashMap<String, Data> map = (HashMap<String, Data>)par1.getValue();
                String key = (String)par2.getValue();

                Data d = map.get(key);
                // jeżeli nie istnieje to błąd
                if (d == null)
                {
                    return ErrorConstruct.KEY_DOESNT_EXIST(getName(), par2.getErrorInfo(), interpreter, key);
                }

                // zwróc wartość pod kluczem key
                return d;
            }
        }
    }

    private class SizeFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SIZE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);

            // sprawdź czy typu array, tuple lub dict
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, DataType.ARRAY,
                    DataType.TUPLE, DataType.DICT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeżeli kolekcja
            if (Collections.isCollection(par))
            {
                Data[] datas = Collections.getDatas(par);
                return Data.createIntData(datas.length);
            }
            // inaczej dict
            else
            {
                HashMap<String, Data> map = (HashMap<String, Data>)par.getValue();
                return Data.createIntData(map.size());
            }
        }
    }

    private class CopyFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "COPY";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);

            // sprawdź czy typu array lub dict
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.DICT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeżeli array
            if (par.getDataType().equals(DataType.ARRAY))
            {
                // pobierz tablicę
                Data[] datas = (Data[])par.getValue();
                // stwórz nową
                Data[] newDatas = new Data[datas.length];
                // skopiuj elementy
                System.arraycopy(datas, 0, newDatas, 0, datas.length);

                // zwróc nową tablicę
                return Data.createArrayData(newDatas);
            }
            // inaczej dict
            else
            {
                // pobierz mapę
                HashMap<String, Data> map = (HashMap<String, Data>)par.getValue();
                // stwórz nową mapę i skopiuj elementy ze starej
                HashMap<String, Data> newMap = new HashMap<>(map);
                // zwróc nowy dict
                return Data.createDictData(newMap);
            }
        }
    }

    private class DeepCopyFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "DEEP_COPY";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);

            // sprawdź czy typu array lub dict
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.DICT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeżeli array
            if (par.getDataType().equals(DataType.ARRAY))
            {
                Data[] datas = (Data[])par.getValue();
                return Data.createArrayData(deepCopy(datas));
            }
            // inaczej dict
            else
            {
                HashMap<String, Data> map = (HashMap<String, Data>)par.getValue();
                return Data.createDictData(deepCopy(map));
            }
        }

        private Data[] deepCopy(Data[] arr)
        {
            Data[] newArr = new Data[arr.length];

            for (int i = 0; i < arr.length; ++i)
            {
                Data d = arr[i];
                if (d.getDataType().equals(DataType.ARRAY))
                {
                    d = Data.createArrayData(deepCopy((Data[])d.getValue()));
                }
                else if (d.getDataType().equals(DataType.DICT))
                {
                    d = Data.createDictData(deepCopy((HashMap<String, Data>)d.getValue()));
                }

                newArr[i] = d;
            }

            return newArr;
        }

        private HashMap<String, Data> deepCopy(HashMap<String, Data> map)
        {
            HashMap<String, Data> newMap = new HashMap<>();

            for (Map.Entry<String, Data> entry : map.entrySet())
            {
                Data d = entry.getValue();

                if (d.getDataType().equals(DataType.ARRAY))
                {
                    d = Data.createArrayData(deepCopy((Data[])d.getValue()));
                }
                else if (d.getDataType().equals(DataType.DICT))
                {
                    d = Data.createDictData(deepCopy((HashMap<String, Data>)d.getValue()));
                }

                newMap.put(entry.getKey(), d);
            }

            return newMap;
        }
    }

    private class ExtendArrayFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "EXTEND_ARRAY";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);

            // sprawdź czy par1 to array
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter,
                    DataType.ARRAY);
            if (tc.isError())
            {
                return tc.getError();
            }

            // sprawdź czy par2 to int
            tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz rozmiar
            int size = (int)par2.getValue();
            // sprawdź czy większy od zera
            if (size < 0)
            {
                return ErrorConstruct.SIZE_LESS_THAN_ZERO(getName(), par2.getErrorInfo(), interpreter);
            }

            // pobierz array
            Data[] datas = (Data[])par1.getValue();
            // stwórz nową tablicę
            Data[] newDatas = new Data[size];
            // skopiuj elementy ze starej do nowej
            int toCopy = (size <= datas.length) ? size : datas.length;
            System.arraycopy(datas, 0, newDatas, 0, toCopy);

            // wypełnij pozostałe elementy elementami none
            for (int i = datas.length; i < size; ++i)
            {
                newDatas[i] = Data.createNoneData();
            }

            // zwróc nową tablicę
            return Data.createArrayData(newDatas);
        }
    }

    private class UnpackFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "UNPACK";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <id+, all>
            // ostatni parametr to kolekcja
            Data col = params.get(params.size() - 1);
            // sprawdź czy kolekcja
            TypeChecker tc = new TypeChecker(col, getName(), params.size() - 1, col.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            List<String> ids = new ArrayList<>();
            // pobierz wszystkie id
            for (int i = 0; i < params.size() - 1; ++i)
            {
                ids.add((String)params.get(i).getValue());
            }

            // pobierz tablicę kolekcji
            Data[] datas = Collections.getDatas(col);
            // sprawdź czy ilość id nie jest większa niż rozmiar kolekcji
            if (ids.size() > datas.length)
            {
                return ErrorConstruct.NUMBER_OF_VARIABLES_GREATER_THAN_COLLECTION_SIZE(
                        getName(), col.getErrorInfo(), interpreter, ids.size(), datas.length);
            }

            List<Data> dataParams = new ArrayList<>();
            // rozpakowujemy kolejno wszystkie wartości (poza ostatnią)
            for (int i = 0; i < ids.size() - 1; ++i)
            {
                Data id = Data.createIdData(ids.get(i));
                Data d = datas[i];

                dataParams.add(id);
                dataParams.add(d);
            }

            Data d;
            Data id = Data.createIdData(ids.get(ids.size() - 1));

            int others = datas.length - ids.size() + 1;
            if (others == 1)
            {
                d = datas[datas.length - 1];
            }
            else
            {
                // tworzymy tuplę pozostałych elementów
                Data[] tup = new Data[others];

                int it = 0;
                for (int i = ids.size() - 1; i < datas.length; ++i)
                {
                    tup[it++] = datas[i];
                }

                d = Data.createTupleData(new Tuple(tup));
            }

            dataParams.add(id);
            dataParams.add(d);

            // wywołujemy funkcję ASSIGN_LOCAL na podanych id
            interpreter.getBuiltinFunctions().callFunction("ASSIGN_LOCAL", dataParams, currentFrame,
                    interpreter, params.get(0).getErrorInfo());

            // zwracamy przekazaną kolekcję
            return col;
        }
    }

    private class ContainsFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CONTAINS";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data col = params.get(0);
            Data el = params.get(1);

            // sprawdź czy par1 jest array, tuple
            TypeChecker tc = new TypeChecker(col, getName(), 0, col.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz tablicę
            Data[] datas = Collections.getDatas(col);
            // iteruj po wszystkich elementach
            for (int i = 0; i < datas.length; ++i)
            {
                List<Data> p = Arrays.asList(datas[i], el);
                Data resEQ = interpreter.getBuiltinFunctions().callFunction("EQ", p, currentFrame, interpreter,
                        datas[i].getErrorInfo());

                // jeżeli zwrócono error lub true to zwróć
                if (resEQ.getDataType().equals(DataType.ERROR)
                        || (boolean)resEQ.getValue() == true)
                {
                    return resEQ;
                }
            }

            // zwróc false
            return Data.createBoolData(false);
        }
    }

    private class RangeFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "RANGE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data d1 = params.get(0);
            Data d2 = params.get(1);

            // sprawdź czy parametry są typu int
            TypeChecker tc = new TypeChecker(d1, getName(), 0, d1.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            tc = new TypeChecker(d2, getName(), 1, d2.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }

            int min = Numbers.getInt(d1);
            int max = Numbers.getInt(d2);

            // sprawdź czy min nie jest większe od max
            if (min > max)
            {
                return ErrorConstruct.MIN_GREATER_THAN_MAX(getName(), d1.getErrorInfo(), interpreter);
            }

            // stwórz tablicę o odpowiednim rozmiarze
            Data[] datas = new Data[max - min + 1];
            // wypełnij liczbami naturalnymi
            for (int i = 0; i < datas.length; ++i)
            {
                datas[i] = Data.createIntData(min + i);
            }

            // zwróc tuplę
            return Data.createTupleData(new Tuple(datas));
        }
    }
}
