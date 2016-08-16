/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.List;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.Collections;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;
import pl.rcebula.module.utils.operation.IOperation;
import pl.rcebula.module.utils.type_checker.TypeChecker;
import pl.rcebula.module.utils.type_checker.TypeCheckerCollection;

/**
 *
 * @author robert
 */
public class StringsModule extends Module
{
    @Override
    public String getName()
    {
        return "strings";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new SubstrFunction());
        putFunction(new LengthFunction());
        putFunction(new ToLowercaseFunction());
        putFunction(new ToUppercaseFunction());
    }
    
    private class ToLowerUpperCaseFunction
    {
        public Data perform(List<Data> params, Interpreter interpreter, IFunction function, IOperation operation)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            // sprawdź czy string, array lub tuple
            TypeChecker tc = new TypeChecker(par, function.getName(), 0, par.getErrorInfo(), interpreter, 
                    DataType.STRING, DataType.ARRAY, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli string
            if (par.getDataType().equals(DataType.STRING))
            {
                String str = (String)par.getValue();
                return operation.perform(new String[] { str });
            }
            // inaczej kolekcja
            else
            {
                Data[] datas = Collections.getDatas(par);
                Data[] newDatas = new Data[datas.length];
                
                // iteruj po wszystkich elementach
                for (int i = 0; i < datas.length; ++i)
                {
                    Data d = datas[i];
                    // sprawdź czy string
                    TypeCheckerCollection tcc = new TypeCheckerCollection(d, function.getName(), 0, i, 
                            d.getErrorInfo(), interpreter, DataType.STRING);
                    if (tcc.isError())
                    {
                        return tcc.getError();
                    }
                    
                    // pobierz stringa
                    String str = (String)d.getValue();
                    // wykonaj operację i zapisz w nowej tablicy
                    newDatas[i] = operation.perform(new String[] { str });
                }
                
                // zwróć tablicę
                return Data.createArrayData(newDatas);
            }
        }
    }
    
    private class ToLowercaseFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "TO_LOWERCASE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new ToLowerUpperCaseFunction().perform(params, interpreter, this, this);
        }

        @Override
        public Data perform(int... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(float... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(boolean... bools)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(String... strings)
        {
            String res = strings[0].toLowerCase();
            return Data.createStringData(res);
        }
    }
    
    private class ToUppercaseFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "TO_UPPERCASE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new ToLowerUpperCaseFunction().perform(params, interpreter, this, this);
        }

        @Override
        public Data perform(int... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(float... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(boolean... bools)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(String... strings)
        {
            String res = strings[0].toUpperCase();
            return Data.createStringData(res);
        }
    }
    
    private class LengthFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "LENGTH";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            // sprawdź czy string
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, 
                    DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz długość stringa
            int length = ((String)par.getValue()).length();
            // zwróć
            return Data.createIntData(length);
        }
    }
    
    private class SubstrFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SUBSTR";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, all>
            Data str = params.get(0);
            Data start = params.get(1);
            Data end = params.get(2);
            
            // sprawdź czy str typu 
            TypeChecker tc = new TypeChecker(str, getName(), 0, str.getErrorInfo(), interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // sprawdź czy start i end typu int
            tc = new TypeChecker(params.subList(1, 3), getName(), 1, interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            String sstr = (String)str.getValue();
            
            int istart = (int)start.getValue();
            int iend = (int)end.getValue();
            // sprawdź czy start nie jest większe od end
            if (istart > iend)
            {
                return ErrorConstruct.START_GREATER_THAN_END(getName(), start.getErrorInfo(), interpreter);
            }
            
            // sprawdź czy start jest większe od -1 i end mniejsze od rozmiaru stringa
            if (istart < 0)
            {
                return ErrorConstruct.INDEX_OUT_OF_BOUNDS(getName(), start.getErrorInfo(), interpreter, istart);
            }
            if (iend > sstr.length())
            {
                return ErrorConstruct.INDEX_OUT_OF_BOUNDS(getName(), end.getErrorInfo(), interpreter, iend);
            }
            
            // uzyskaj substring
            String substr = sstr.substring(istart, iend);
            return Data.createStringData(substr);
        }
    }
}
