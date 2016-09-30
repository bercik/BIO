/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.Struct;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.Collections;
import pl.rcebula.module.utils.Datas;
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class IterModule extends Module
{
    @Override
    public String getName()
    {
        return "iter";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new ForeachFunction());
    }

    private class ForeachFunction implements IFunction
    {
        // obiekt przekazywany pomiędzy kolejnymi wywołaniami funkcji
        private Data obj;
        // obiekt po którym iterujemy
        // w przypadku array lub tuple
        private List<Data> col;
        private Iterator<Data> colIt;
        // w przypadku dict
        private Map<String, Data> map;
        private Iterator<Map.Entry<String, Data>> mapIt;
        // nazwa funkcji
        private Data dFunName;
        // ostatnio zwrócona wartość
        private Data lastRetVal;

        @Override
        public String getName()
        {
            return "FOREACH";
        }

        private void reset()
        {
            obj = null;
            col = null;
            map = null;
            dFunName = null;
            mapIt = null;
            colIt = null;
            // none domyślnie
            lastRetVal = Data.createNoneData();
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // jeżeli pierwsze wywołanie funkcji foreach to przygotowujemy
            if (!currentFrame.isCallForeach())
            {
                // resetujemy
                reset();

                // ustawiamy, żeby następnym razem przy zwróceniu jakiejś wartości została wywołana nasza
                // funkcja FOREACH
                currentFrame.setCallForeach(true);

                // parametry: <all, id>
                Data iterable = params.get(0);
                Data did = params.get(1);

                // sprawdź czy iterable jest typu array, dict lub tuple
                TypeChecker tc = new TypeChecker(iterable, getName(), 0, iterable.getErrorInfo(), interpreter,
                        DataType.ARRAY, DataType.DICT, DataType.TUPLE);
                if (tc.isError())
                {
                    return tc.getError();
                }

                // pobierz nazwę funkcji
                String funName = (String)did.getValue();
                // utwórz Data String
                dFunName = Data.createStringData(funName);
                // ustaw error info
                dFunName.setErrorInfo(did.getErrorInfo());

                // stwórz strukturę przekazywaną pomiędzy kolejnymi wywołaniami funkcji
                obj = Data.createStructData(new Struct());

                // jeżeli dict
                if (iterable.getDataType().equals(DataType.DICT))
                {
                    // pobierz mapę
                    Map<String, Data> m = (HashMap<String, Data>)iterable.getValue();
                    // skopiuj
                    map = new HashMap<>(m);
                    // utwórz iterator
                    mapIt = map.entrySet().iterator();
                }
                // inaczej array lub tuple
                else
                {
                    // pobierz tablicę
                    Data[] d = Collections.getDatas(iterable);
                    // skopiuj z d do tymczasowej tablicy
                    Data[] tmpD = new Data[d.length];
                    System.arraycopy(d, 0, tmpD, 0, d.length);
                    // utwórz listę
                    col = Arrays.asList(tmpD);
                    // utwórz iterator
                    colIt = col.iterator();
                }
            }
            // inaczej nie pierwsze wywołanie, więc ściągamy parametr ze stosu parametrów
            else
            {
                // ta częśc jest trochę tricky
                // musimy dostać się do wartości zwróconej przez funkcję. 
                // Została ona odłożona na stos parametrów, więc ściągamy ze stosu aktualnej funkcji jeden 
                // parametr
                lastRetVal = currentFrame.getVariableStack().pop();

                // sprawdź czy nie zwrócono błędu, jeżeli tak to zwróć
                if (lastRetVal.getDataType().equals(DataType.ERROR))
                {
                    currentFrame.setCallForeach(false);
                    
                    return lastRetVal;
                }
            }

            // jeżeli dict
            if (mapIt != null)
            {
                // jeżeli nie jest dostępny
                if (!mapIt.hasNext())
                {
                    // ustaw, żeby już nie wywoływać funkcji FOREACH
                    currentFrame.setCallForeach(false);
                    // zwróc ostatnią wartość
                    return lastRetVal;
                }
                // iteruj po kolejnym elemencie
                Map.Entry<String, Data> entry = mapIt.next();
                // stwórz strukturę el z dwoma polami: val i key
                Struct struct = new Struct();
                struct.addField("val", entry.getValue());
                struct.addField("key", Data.createStringData(entry.getKey()));
                Data el = Data.createStructData(struct);

                // wywołaj funkcję CALL_BY_NAME, przekazując do niej 3 parametry: funName, el, obj
                List<Data> pars = Arrays.asList(dFunName, el, obj);
                interpreter.getBuiltinFunctions().callFunction("CALL_BY_NAME", pars, currentFrame,
                        interpreter, new ErrorInfo(-1, -1, new MyFiles.File(-1, "")));
            }
            // inaczej tuple lub array
            else
            {
                // jeżeli nie jest dostępny
                if (!colIt.hasNext())
                {
                    // ustaw, żeby już nie wywoływać funkcji FOREACH
                    currentFrame.setCallForeach(false);
                    // zwróc ostatnią wartość
                    return lastRetVal;
                }
                Data d = colIt.next();
                // stwórz strukturę el z polem val
                Struct struct = new Struct();
                struct.addField("val", d);
                Data el = Data.createStructData(struct);

                // wywołaj funkcję CALL_BY_NAME, przekazując do niej 3 parametry: funName, el, obj
                List<Data> pars = Arrays.asList(dFunName, el, obj);
                interpreter.getBuiltinFunctions().callFunction("CALL_BY_NAME", pars, currentFrame,
                        interpreter, new ErrorInfo(-1, -1, new MyFiles.File(-1, "")));
            }
            
            // zwracamy nic
            return null;
        }
    }
}
