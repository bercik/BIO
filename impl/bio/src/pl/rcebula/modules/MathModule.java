/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.data_types.Tuple;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.modules.utils.Collections;
import pl.rcebula.modules.utils.ErrorCodes;
import pl.rcebula.modules.utils.type_checker.ITypeChecker;
import pl.rcebula.modules.utils.type_checker.TypeChecker;
import pl.rcebula.modules.utils.type_checker.TypeCheckerCollection;
import pl.rcebula.modules.utils.type_checker.TypeCheckerNumber;
import pl.rcebula.modules.utils.type_checker.TypeCheckerNumberCollection;

/**
 *
 * @author robert
 */
public class MathModule extends Module
{
    @Override
    public String getName()
    {
        return "math";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new SumFunction());
        putFunction(new AddFunction());
    }

    private class SumFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SUM";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return call(getName(), params, currentFrame, interpreter, false, -1);
        }

        public Data call(String funName, List<Data> params, CallFrame currentFrame, Interpreter interpreter,
                boolean addFunction, int elementNum)
        {
            // parametr: <all>
            // sprawdzamy czy kolekcja (array lub tuple)
            Data col = params.get(0);
            ErrorInfo ei = col.getErrorInfo();
            ITypeChecker tc;
            if (!addFunction)
            {
                tc = new TypeChecker(
                        col, funName, 0, ei, interpreter, DataType.ARRAY, DataType.TUPLE);

            }
            else
            {
                tc = new TypeCheckerCollection(
                        col, funName, 0, elementNum, ei, interpreter, DataType.ARRAY, DataType.TUPLE);
            }
            if (tc.isError())
            {
                return tc.getError();
            }

            // pobierz tablicę z kolekcji
            Data[] array = Collections.getDatas(col);
            // sprawdź czy rozmiar większy od zera, jeżeli nie zwróć none
            if (array.length == 0)
            {
                return Data.createNoneData();
            }

            // pobierz pierwszy element i sprawdź czy jego typ jest string, int lub float
            Data firstElement = array[0];
            TypeCheckerCollection tcc;
            if (!addFunction)
            {
                tcc = new TypeCheckerCollection(firstElement, funName, 0, 0, ei, interpreter,
                        DataType.STRING, DataType.INT, DataType.FLOAT);
            }
            else
            {
                tcc = new TypeCheckerCollection(firstElement, funName, 0, elementNum, ei, interpreter,
                        DataType.STRING, DataType.INT, DataType.FLOAT);
            }

            if (tcc.isError())
            {
                return tcc.getError();
            }

            // jeżeli element to string to konkatenacja, inaczej dodawanie
            if (firstElement.getDataType().equals(DataType.STRING))
            {
                String str = (String)firstElement.getValue();
                for (int i = 1; i < array.length; ++i)
                {
                    // pobierz następny element
                    Data d = array[i];
                    // sprawdź czy typu string
                    if (!addFunction)
                    {
                        tcc = new TypeCheckerCollection(d, funName, 0, i, ei, interpreter, DataType.STRING);
                    }
                    else
                    {
                        tcc = new TypeCheckerCollection(d, funName, i, elementNum, ei, interpreter, DataType.STRING);
                    }
                    if (tcc.isError())
                    {
                        return tcc.getError();
                    }
                    // dołączamy stringa
                    str += (String)d.getValue();
                }

                // zwracamy stringa
                return Data.createStringData(str);
            }
            // dodawanie liczb
            else
            {
                boolean isFloat = false;
                int isum = 0;
                float fsum = 0.0f;

                for (int i = 0; i < array.length; ++i)
                {
                    // pobierz element
                    Data d = array[i];
                    // sprawdź czy typu liczbowego
                    TypeCheckerNumberCollection tcnc;
                    if (!addFunction)
                    {
                        tcnc = new TypeCheckerNumberCollection(d, funName, 0, i, ei,
                                interpreter);
                    }
                    else
                    {
                        tcnc = new TypeCheckerNumberCollection(d, funName, i,
                                elementNum, ei, interpreter);
                    }
                    if (tcnc.isError())
                    {
                        return tcnc.getError();
                    }
                    // jeżeli dotychczas było int to sprawdzamy czy się nie zmieniło
                    if (!isFloat)
                    {
                        isFloat = tcnc.isFloat();
                        if (isFloat)
                        {
                            // konwertujemy dotychczasową sumę z int na float
                            fsum = isum;
                        }
                    }
                    // dodajemy liczbę do sumy
                    if (!isFloat)
                    {
                        int val = (int)d.getValue();
                        isum += val;
                    }
                    else
                    {
                        float val;
                        if (d.getDataType().equals(DataType.INT))
                        {
                            val = ((Integer)d.getValue()).floatValue();
                        }
                        else
                        {
                            val = (float)d.getValue();
                        }
                        fsum += val;
                    }
                }

                // zwracamy float lub int
                if (isFloat)
                {
                    return Data.createFloatData(fsum);
                }
                else
                {
                    return Data.createIntData(isum);
                }
            }
        }
    }

    private class CreateDatas
    {
        private Data error;
        private boolean isError;
        private Data[][] datas;

        public CreateDatas(String funName, List<Data> params, Interpreter interpreter,
                int defaultInt, float defaultFloat, String defaultString)
        {
            // pobierz pierwszą kolekcją
            Data[] firstCol = Collections.getDatas(params.get(0));
            // stwórz tablicę 2D
            datas = new Data[firstCol.length][params.size()];
            // iteruj po wszystkich typach danych
            for (int i = 0; i < params.size(); ++i)
            {
                Data d = params.get(i);
                ErrorInfo ei = d.getErrorInfo();
                // sprawdź czy jest kolekcją
                TypeChecker tc = new TypeChecker(d, funName, i, ei, interpreter, DataType.ARRAY,
                        DataType.TUPLE);
                if (tc.isError())
                {
                    isError = true;
                    error = tc.getError();
                    datas = null;
                    return;
                }

                // pobierz dane z kolekcji
                Data[] col = Collections.getDatas(d);
                // sprawdź czy rozmiar jest taki jak poprzednich
                if (col.length != firstCol.length)
                {
                    // błąd
                    String message = "In function " + funName + " collection passed as " + i + " parameter "
                            + "differs in size with previous";

                    MyError cause = null;
                    MyError myError = new MyError(message, ErrorCodes.COLLECTIONS_DIFFRENT_SIZES.getCode(),
                            cause, ei, interpreter);
                    error = Data.createErrorData(myError);
                    isError = true;
                    datas = null;
                    return;
                }
                // iteruj po wszystkich elementach
                for (int k = 0; k < col.length; ++k)
                {
                    // dodaj do datas
                    datas[k][i] = col[k];
                }
            }

            isError = false;
            error = null;
        }

        public Data getError()
        {
            return error;
        }

        public boolean IsError()
        {
            return isError;
        }

        public Data[][] getDatas()
        {
            return datas;
        }
    }

    private class AddFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ADD";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, all*>
            Data firstElement = params.get(0);
            // sprawdzamy czy typ to array, tuple, string, int lub float
            TypeChecker tc = new TypeChecker(firstElement, getName(), 0, firstElement.getErrorInfo(),
                    interpreter, DataType.ARRAY, DataType.FLOAT, DataType.INT, DataType.STRING,
                    DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeżeli element to string to konkatenacja, inaczej jeśli int lub float dodawanie, inaczej dodawanie kolekcji
            if (firstElement.getDataType().equals(DataType.STRING))
            {
                String str = (String)firstElement.getValue();
                for (int i = 1; i < params.size(); ++i)
                {
                    // pobierz następny element
                    Data d = params.get(i);
                    // sprawdź czy typu string
                    tc = new TypeChecker(d, getName(), i, d.getErrorInfo(), interpreter, DataType.STRING);
                    if (tc.isError())
                    {
                        return tc.getError();
                    }
                    // dołączamy stringa
                    str += (String)d.getValue();
                }

                // zwracamy stringa
                return Data.createStringData(str);
            }
            // dodawanie liczb
            else if (firstElement.getDataType().equals(DataType.INT)
                    || firstElement.getDataType().equals(DataType.FLOAT))
            {
                boolean isFloat = false;
                int isum = 0;
                float fsum = 0.0f;

                for (int i = 0; i < params.size(); ++i)
                {
                    // pobierz element
                    Data d = params.get(i);
                    // sprawdź czy typu liczbowego
                    TypeCheckerNumber tcn = new TypeCheckerNumber(d, getName(), i, d.getErrorInfo(), interpreter);
                    if (tcn.isError())
                    {
                        return tcn.getError();
                    }
                    // jeżeli dotychczas było int to sprawdzamy czy się nie zmieniło
                    if (!isFloat)
                    {
                        isFloat = tcn.isFloat();
                        if (isFloat)
                        {
                            // konwertujemy dotychczasową sumę z int na float
                            fsum = isum;
                        }
                    }
                    // dodajemy liczbę do sumy
                    if (!isFloat)
                    {
                        int val = (int)d.getValue();
                        isum += val;
                    }
                    else
                    {
                        float val;
                        if (d.getDataType().equals(DataType.INT))
                        {
                            val = ((Integer)d.getValue()).floatValue();
                        }
                        else
                        {
                            val = (float)d.getValue();
                        }
                        fsum += val;
                    }
                }

                // zwracamy float lub int
                if (isFloat)
                {
                    return Data.createFloatData(fsum);
                }
                else
                {
                    return Data.createIntData(isum);
                }
            }
            // inaczej tworzymy tablicę 2D i sumujemy każdą
            else
            {
                CreateDatas cd = new CreateDatas(getName(), params, interpreter, 0, 0.0f, "");

                if (cd.IsError())
                {
                    return cd.getError();
                }

                SumFunction sf = new SumFunction();
                Data[][] datas = cd.getDatas();
                Data[] tupleData = new Data[datas.length];
                // przechodzimy po każdej tablicy i sumujemy jej elementy przy użyciu funkcji SUM
                for (int i = 0; i < datas.length; ++i)
                {
                    Data[] d = datas[i];
                    Data arr = Data.createArrayData(d);
                    arr.setErrorInfo(params.get(0).getErrorInfo());

                    Data sum = sf.call(getName(), Arrays.asList(arr), currentFrame, interpreter, true, i);
                    if (sum.getDataType().equals(DataType.ERROR))
                    {
                        return sum;
                    }

                    tupleData[i] = sum;
                }

                return Data.createTupleData(new Tuple(tupleData));
            }
        }
    }
}
