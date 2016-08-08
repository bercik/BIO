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
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.data_types.Tuple;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.modules.utils.Collections;
import pl.rcebula.modules.utils.error_codes.ErrorCodes;
import pl.rcebula.modules.utils.Numbers;
import pl.rcebula.modules.utils.error_codes.ErrorConstruct;
import pl.rcebula.modules.utils.type_checker.TypeChecker;

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
