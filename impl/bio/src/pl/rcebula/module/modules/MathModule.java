/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.Arrays;
import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.Collections;
import pl.rcebula.module.utils.Numbers;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;
import pl.rcebula.module.utils.operation.CollectionsOperation;
import pl.rcebula.module.utils.operation.IOperation;
import pl.rcebula.module.utils.operation.OperationDataType;
import pl.rcebula.module.utils.type_checker.ITypeChecker;
import pl.rcebula.module.utils.type_checker.TypeChecker;
import pl.rcebula.module.utils.type_checker.TypeCheckerCollection;
import pl.rcebula.module.utils.type_checker.TypeCheckerNumber;
import pl.rcebula.module.utils.type_checker.TypeCheckerNumberCollection;

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
        putFunction(new DiffFunction());
        putFunction(new SubFunction());
        putFunction(new ProductFunction());
        putFunction(new MulFunction());
        putFunction(new QuotientFunction());
        putFunction(new DivFunction());
        putFunction(new SqrtFunction());
        putFunction(new PowFunction());
        putFunction(new ModFunction());
        putFunction(new IncFunction());
        putFunction(new DecFunction());
        putFunction(new NegateFunction());
        putFunction(new RandFunction());
    }

    private class RandFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "RAND";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // bez parametrów
            float rand = (float)Math.random();
            return Data.createFloatData(rand);
        }
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

    private static class CreateDatas
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
                    error = ErrorConstruct.COLLECTIONS_DIFFRENT_SIZES(funName, ei, interpreter, i);
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
            // inaczej dodawanie kolekcji
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

                return Data.createArrayData(tupleData);
            }
        }
    }

    private enum Operation
    {
        DIFF,
        PRODUCT,
        QUOTIENT;
    }

    private static class PerformOperationOnCollection
    {

        public Data perform(String funName, List<Data> params, CallFrame currentFrame, Interpreter interpreter,
                boolean addFunction, int elementNum, Operation operation)
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

            // pobierz pierwszy element i sprawdź czy jego typ jest int lub float
            Data firstElement = array[0];
            TypeCheckerCollection tcc;
            if (!addFunction)
            {
                tcc = new TypeCheckerCollection(firstElement, funName, 0, 0, ei, interpreter,
                        DataType.INT, DataType.FLOAT);
            }
            else
            {
                tcc = new TypeCheckerCollection(firstElement, funName, 0, elementNum, ei, interpreter,
                        DataType.INT, DataType.FLOAT);
            }

            if (tcc.isError())
            {
                return tcc.getError();
            }

            // operacja na liczbach
            boolean isFloat = false;
            int iresult = 0;
            float fresult = 0.0f;

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
                        fresult = iresult;
                    }
                }
                // jeżeli pierwszy obieg to przypisujemy do wyniku liczbę
                if (i == 0)
                {
                    if (isFloat)
                    {
                        fresult = (float)d.getValue();
                    }
                    else
                    {
                        iresult = (int)d.getValue();
                    }
                }
                else // wykonujemy operację na liczbie
                {
                    if (!isFloat)
                    {
                        int val = (int)d.getValue();
                        switch (operation)
                        {
                            case DIFF:
                                iresult -= val;
                                break;
                            case PRODUCT:
                                iresult *= val;
                                break;
                            case QUOTIENT:
                                // sprawdzamy czy nie dzielimy przez zero
                                if (val == 0)
                                {
                                    return ErrorConstruct.DIVISION_BY_ZERO(funName, ei, interpreter);
                                }
                                iresult /= val;
                                break;
                            default:
                                throw new RuntimeException("Unknown " + operation.toString() + " operation");
                        }
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
                        switch (operation)
                        {
                            case DIFF:
                                fresult -= val;
                                break;
                            case PRODUCT:
                                fresult *= val;
                                break;
                            case QUOTIENT:
                                if (val == 0)
                                {
                                    return ErrorConstruct.DIVISION_BY_ZERO(funName, ei, interpreter);
                                }
                                fresult /= val;
                                break;
                            default:
                                throw new RuntimeException("Unknown " + operation.toString() + " operation");
                        }
                    }
                }
            }

            // zwracamy float lub int
            if (isFloat)
            {
                return Data.createFloatData(fresult);
            }
            else
            {
                return Data.createIntData(iresult);
            }
        }
    }

    private static class PerformOperation
    {
        public Data perform(String funName, List<Data> params, CallFrame currentFrame, Interpreter interpreter,
                Operation operation)
        {
            // parametry: <all, all, all*>
            Data firstElement = params.get(0);
            ErrorInfo ei = firstElement.getErrorInfo();
            // sprawdzamy czy typ to array, tuple, int lub float
            TypeChecker tc = new TypeChecker(firstElement, funName, 0, firstElement.getErrorInfo(),
                    interpreter, DataType.ARRAY, DataType.FLOAT, DataType.INT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeśli pierwszy element to int lub float operacja, inaczej dodawanie kolekcji
            if (firstElement.getDataType().equals(DataType.INT)
                    || firstElement.getDataType().equals(DataType.FLOAT))
            {
                boolean isFloat = false;
                int iresult = 0;
                float fresult = 0.0f;

                for (int i = 0; i < params.size(); ++i)
                {
                    // pobierz element
                    Data d = params.get(i);
                    // sprawdź czy typu liczbowego
                    TypeCheckerNumber tcn = new TypeCheckerNumber(d, funName, i, d.getErrorInfo(), interpreter);
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
                            fresult = iresult;
                        }
                    }
                    // jeżeli pierwsza liczba to przypisujemy ją do wyniku
                    if (i == 0)
                    {
                        if (isFloat)
                        {
                            fresult = (float)d.getValue();
                        }
                        else
                        {
                            iresult = (int)d.getValue();
                        }
                    }
                    else // wykonujemy operację
                     if (!isFloat)
                        {
                            int val = (int)d.getValue();
                            switch (operation)
                            {
                                case DIFF:
                                    iresult -= val;
                                    break;
                                case PRODUCT:
                                    iresult *= val;
                                    break;
                                case QUOTIENT:
                                    if (val == 0)
                                    {
                                        return ErrorConstruct.DIVISION_BY_ZERO(funName, ei, interpreter);
                                    }
                                    iresult /= val;
                                    break;
                                default:
                                    throw new RuntimeException("Unknown operation " + operation.toString());
                            }
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
                            switch (operation)
                            {
                                case DIFF:
                                    fresult -= val;
                                    break;
                                case PRODUCT:
                                    fresult *= val;
                                    break;
                                case QUOTIENT:
                                    if (val == 0)
                                    {
                                        return ErrorConstruct.DIVISION_BY_ZERO(funName, ei, interpreter);
                                    }
                                    fresult /= val;
                                    break;
                                default:
                                    throw new RuntimeException("Unknown operation " + operation.toString());
                            }
                        }
                }

                // zwracamy float lub int
                if (isFloat)
                {
                    return Data.createFloatData(fresult);
                }
                else
                {
                    return Data.createIntData(iresult);
                }
            }
            // inaczej tworzymy tablicę 2D i sumujemy każdą
            else
            {
                CreateDatas cd = new CreateDatas(funName, params, interpreter, 0, 0.0f, "");

                if (cd.IsError())
                {
                    return cd.getError();
                }

                PerformOperationOnCollection pooc = new PerformOperationOnCollection();
                Data[][] datas = cd.getDatas();
                Data[] tupleData = new Data[datas.length];
                // przechodzimy po każdej tablicy i sumujemy jej elementy przy użyciu funkcji SUM
                for (int i = 0; i < datas.length; ++i)
                {
                    Data[] d = datas[i];
                    Data arr = Data.createArrayData(d);
                    arr.setErrorInfo(params.get(0).getErrorInfo());

                    Data result = pooc.perform(funName, Arrays.asList(arr), currentFrame, interpreter, true, i,
                            operation);
                    if (result.getDataType().equals(DataType.ERROR))
                    {
                        return result;
                    }

                    tupleData[i] = result;
                }

                return Data.createArrayData(tupleData);
            }
        }
    }

    private class DiffFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "DIFF";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformOperationOnCollection().perform(getName(), params, currentFrame, interpreter,
                    false, -1, Operation.DIFF);
        }
    }

    private class SubFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SUB";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformOperation().perform(getName(), params, currentFrame, interpreter,
                    MathModule.Operation.DIFF);
        }
    }

    private class ProductFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "PRODUCT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformOperationOnCollection().perform(getName(), params, currentFrame, interpreter,
                    false, -1, Operation.PRODUCT);
        }
    }

    private class MulFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "MUL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformOperation().perform(getName(), params, currentFrame, interpreter,
                    MathModule.Operation.PRODUCT);
        }
    }

    private class QuotientFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "QUOTIENT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformOperationOnCollection().perform(getName(), params, currentFrame, interpreter,
                    false, -1, Operation.QUOTIENT);
        }
    }

    private class DivFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "DIV";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformOperation().perform(getName(), params, currentFrame, interpreter,
                    MathModule.Operation.QUOTIENT);
        }
    }

    private class SqrtFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "SQRT";
        }

        @Override
        public Data perform(int... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(float... nums)
        {
            return Data.createFloatData((float)Math.sqrt(nums[0]));
        }

        @Override
        public Data perform(boolean... bools)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(String... strings)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data d = params.get(0);

            // sprawdzamy czy typu number lub collection
            TypeChecker tc = new TypeChecker(d, getName(), 0, d.getErrorInfo(), interpreter,
                    DataType.INT, DataType.FLOAT, DataType.ARRAY, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeżeli liczba
            if (Numbers.isNumber(d))
            {
                float val = Numbers.getFloat(d);
                return perform(new float[]
                {
                    val
                });
            }
            // kolekcja
            else
            {
                return new CollectionsOperation().perform(getName(), OperationDataType.FLOAT, this, interpreter, d);
            }
        }
    }

    private class PowFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "POW";
        }

        @Override
        public Data perform(int... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(float... nums)
        {
            return Data.createFloatData((float)Math.pow(nums[0], nums[1]));
        }

        @Override
        public Data perform(boolean... bools)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(String... strings)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);

            // sprawdź czy liczba lub kolekcja
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter,
                    DataType.INT, DataType.FLOAT, DataType.ARRAY, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            // liczba
            if (Numbers.isNumber(par1))
            {
                // sprawdź czy parametr drugi to liczba
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.FLOAT,
                        DataType.INT);
                if (tc.isError())
                {
                    return tc.getError();
                }

                // pobierz jako float
                float base = Numbers.getFloat(par1);
                float exponent = Numbers.getFloat(par2);

                return perform(new float[]
                {
                    base, exponent
                });
            }
            // kolekcja
            else
            {
                return new CollectionsOperation().perform(getName(), OperationDataType.FLOAT, this, interpreter,
                        par1, par2);
            }
        }
    }

    private class ModFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "MOD";
        }

        @Override
        public Data perform(int... nums)
        {
            return Data.createIntData(nums[0] % nums[1]);
        }

        @Override
        public Data perform(float... nums)
        {
            return Data.createFloatData(nums[0] % nums[1]);
        }

        @Override
        public Data perform(boolean... bools)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(String... strings)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);

            // sprawdź czy number lub collection
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.FLOAT, DataType.INT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            // jeżeli liczba to
            if (Numbers.isNumber(par1))
            {
                // sprawdź czy parametr pierwszy jest floatem
                boolean isFloat = par1.getDataType().equals(DataType.FLOAT);

                // sprawdź czy drugi parametr jest liczbą i czy float
                TypeCheckerNumber tcn
                        = new TypeCheckerNumber(par2, getName(), 1, par2.getErrorInfo(), interpreter);
                if (tcn.isError())
                {
                    return tcn.getError();
                }

                if (!isFloat)
                {
                    isFloat = tcn.isFloat();
                }

                // jeżeli jedna z liczb była typu float to konwertujemy obie na float
                if (isFloat)
                {
                    float val1 = Numbers.getFloat(par1);
                    float val2 = Numbers.getFloat(par2);

                    return perform(val1, val2);
                }
                // inaczej inty
                {
                    int val1 = Numbers.getInt(par1);
                    int val2 = Numbers.getInt(par2);
                    
                    return perform(val1, val2);
                }
            }
            // inaczej kolekcje
            else
            {
                return new CollectionsOperation().perform(
                        getName(), OperationDataType.NUMBER, this, interpreter, par1, par2);
            }
        }
    }

    private static class IncDecFunction
    {
        private enum FUN
        {
            INC,
            DEC;
        }

        private static Data perform(List<Data> params, CallFrame currentFrame, Interpreter interpreter, FUN fun,
                String funName)
        {
            // parametr: <id>, <val>?
            Data idData = params.get(0);
            Data data = interpreter.getBuiltinFunctions().callFunction("GET_LOCAL", params, currentFrame,
                    interpreter, idData.getErrorInfo());

            // jeżeli error to zwróć
            if (data.getDataType().equals(DataType.ERROR))
            {
                return data;
            }

            // sprawdź czy liczba
            TypeCheckerNumber tcn = new TypeCheckerNumber(data, funName, 0, data.getErrorInfo(),
                    interpreter);
            if (tcn.isError())
            {
                return tcn.getError();
            }

            Integer iinc = null;
            Float finc = null;
            // sprawdź czy drugi parametr istnieje,
            // jeżeli tak to
            if (params.size() > 1)
            {
                // pobierz
                Data dVal = params.get(1);

                // jeżeli data jest typu float
                if (tcn.isFloat())
                {
                    // sprawdź czy dVal jest liczbą
                    TypeCheckerNumber valTcn = new TypeCheckerNumber(dVal, funName, 1, dVal.getErrorInfo(),
                            interpreter);

                    if (valTcn.isError())
                    {
                        return valTcn.getError();
                    }

                    // pobierz wartość
                    finc = Numbers.getFloat(dVal);
                }
                // inaczej data to int
                else
                {
                    // sprawdź czy dVal jest intem
                    TypeChecker tc = new TypeChecker(dVal, funName, 1, dVal.getErrorInfo(), interpreter,
                            DataType.INT);

                    if (tc.isError())
                    {
                        return tc.getError();
                    }

                    // pobierz wartość
                    iinc = (int)dVal.getValue();
                }
            }

            if (tcn.isFloat())
            {
                float dataVal = (float)data.getValue();
                // jeżeli podano wartość do inkrementowania
                if (finc != null)
                {
                    if (fun.equals(FUN.INC))
                    {
                        dataVal += finc;
                    }
                    else
                    {
                        dataVal -= finc;
                    }
                }
                else
                {
                    if (fun.equals(FUN.INC))
                    {
                        ++dataVal;
                    }
                    else
                    {
                        --dataVal;
                    }
                }
                Data newData = Data.createFloatData(dataVal);
                interpreter.getBuiltinFunctions().callFunction("ASSIGN_LOCAL", Arrays.asList(idData, newData),
                        currentFrame, interpreter, idData.getErrorInfo());
                return newData;
            }
            else
            {
                int dataVal = Numbers.getInt(data);
                // jeżeli podano wartość do inkrementowania
                if (iinc != null)
                {
                    if (fun.equals(FUN.INC))
                    {
                        dataVal += iinc;
                    }
                    else
                    {
                        dataVal -= iinc;
                    }
                }
                else
                {
                    if (fun.equals(FUN.INC))
                    {
                        ++dataVal;
                    }
                    else
                    {
                        --dataVal;
                    }
                }
                Data newData = Data.createIntData(dataVal);
                interpreter.getBuiltinFunctions().callFunction("ASSIGN_LOCAL", Arrays.asList(idData, newData),
                        currentFrame, interpreter, idData.getErrorInfo());
                return newData;
            }
        }
    }

    private class IncFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "INC";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return IncDecFunction.perform(params, currentFrame, interpreter, 
                    IncDecFunction.FUN.INC, getName());
        }
    }

    private class DecFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "DEC";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return IncDecFunction.perform(params, currentFrame, interpreter, 
                    IncDecFunction.FUN.DEC, getName());
        }
    }

    private class NegateFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "NEGATE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data data = params.get(0);

            // sprawdzamy czy liczba
            TypeCheckerNumber tcn = new TypeCheckerNumber(data, getName(), 0, data.getErrorInfo(),
                    interpreter);

            if (tcn.isError())
            {
                return tcn.getError();
            }

            // negujemy i zwracamy
            if (tcn.isFloat())
            {
                float val = (float)data.getValue();
                val = -val;
                return Data.createFloatData(val);
            }
            else
            {
                int val = (int)data.getValue();
                val = -val;
                return Data.createIntData(val);
            }
        }
    }
}
