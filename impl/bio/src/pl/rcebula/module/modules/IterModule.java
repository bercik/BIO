/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

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

    public class ForeachFunction implements IFunction
    {
        public class ForeachInfo
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

            public ForeachInfo()
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
        }

        @Override
        public String getName()
        {
            return "FOREACH";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // referencja do struktury przechowującej dane pomocnicze
            ForeachInfo foreachInfo = currentFrame.getForeachInfo();
            
            // jeżeli pierwsze wywołanie funkcji foreach to przygotowujemy
            if (!currentFrame.isCallForeach())
            {
                // tworzymy nowy obiekt foreach info
                foreachInfo = new ForeachInfo();

                // ustawiamy, żeby następnym razem przy zwróceniu jakiejś wartości została wywołana nasza
                // funkcja FOREACH
                currentFrame.setCallForeach(true);
                currentFrame.setForeachInfo(foreachInfo);

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
                foreachInfo.dFunName = Data.createStringData(funName);
                // ustaw error info
                foreachInfo.dFunName.setErrorInfo(did.getErrorInfo());

                // stwórz strukturę przekazywaną pomiędzy kolejnymi wywołaniami funkcji
                foreachInfo.obj = Data.createStructData(new Struct());

                // jeżeli dict
                if (iterable.getDataType().equals(DataType.DICT))
                {
                    // pobierz mapę
                    Map<String, Data> m = (HashMap<String, Data>)iterable.getValue();
                    // skopiuj
                    foreachInfo.map = new HashMap<>(m);
                    // utwórz iterator
                    foreachInfo.mapIt = foreachInfo.map.entrySet().iterator();
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
                    foreachInfo.col = Arrays.asList(tmpD);
                    // utwórz iterator
                    foreachInfo.colIt = foreachInfo.col.iterator();
                }
            }
            // inaczej nie pierwsze wywołanie, więc ściągamy parametr ze stosu parametrów
            else
            {
                // ta częśc jest trochę tricky
                // musimy dostać się do wartości zwróconej przez funkcję. 
                // Została ona odłożona na stos parametrów, więc ściągamy ze stosu aktualnej funkcji jeden 
                // parametr
                foreachInfo.lastRetVal = currentFrame.getVariableStack().pop();

                // sprawdź czy nie zwrócono błędu, jeżeli tak to zwróć
                if (foreachInfo.lastRetVal.getDataType().equals(DataType.ERROR))
                {
                    currentFrame.setCallForeach(false);
                    currentFrame.setForeachInfo(null);

                    return foreachInfo.lastRetVal;
                }
            }

            // jeżeli dict
            if (foreachInfo.mapIt != null)
            {
                // jeżeli nie jest dostępny
                if (!foreachInfo.mapIt.hasNext())
                {
                    // ustaw, żeby już nie wywoływać funkcji FOREACH
                    currentFrame.setCallForeach(false);
                    currentFrame.setForeachInfo(null);
                    // zwróc ostatnią wartość
                    return foreachInfo.lastRetVal;
                }
                // iteruj po kolejnym elemencie
                Map.Entry<String, Data> entry = foreachInfo.mapIt.next();
                // stwórz strukturę el z dwoma polami: val i key
                Struct struct = new Struct();
                struct.addField("val", entry.getValue());
                struct.addField("key", Data.createStringData(entry.getKey()));
                Data el = Data.createStructData(struct);

                // wywołaj funkcję CALL_BY_NAME, przekazując do niej 3 parametry: funName, el, obj
                List<Data> pars = Arrays.asList(foreachInfo.dFunName, el, foreachInfo.obj);
                interpreter.getBuiltinFunctions().callFunction("CALL_BY_NAME", pars, currentFrame,
                        interpreter, new ErrorInfo(-1, -1, new MyFiles.File(-1, "")));
            }
            // inaczej tuple lub array
            else
            {
                // jeżeli nie jest dostępny
                if (!foreachInfo.colIt.hasNext())
                {
                    // ustaw, żeby już nie wywoływać funkcji FOREACH
                    currentFrame.setCallForeach(false);
                    // zwróc ostatnią wartość
                    return foreachInfo.lastRetVal;
                }
                Data d = foreachInfo.colIt.next();
                // stwórz strukturę el z polem val
                Struct struct = new Struct();
                struct.addField("val", d);
                Data el = Data.createStructData(struct);

                // wywołaj funkcję CALL_BY_NAME, przekazując do niej 3 parametry: funName, el, obj
                List<Data> pars = Arrays.asList(foreachInfo.dFunName, el, foreachInfo.obj);
                interpreter.getBuiltinFunctions().callFunction("CALL_BY_NAME", pars, currentFrame,
                        interpreter, new ErrorInfo(-1, -1, new MyFiles.File(-1, "")));
            }

            // zwracamy nic
            return null;
        }
    }
}
