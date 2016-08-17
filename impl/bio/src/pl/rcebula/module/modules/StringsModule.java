/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.List;
import java.util.regex.PatternSyntaxException;
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
        putFunction(new InsertFunction());
        putFunction(new StartsWithFunction());
        putFunction(new EndsWithFunction());
        putFunction(new ReplaceFunction());
        putFunction(new IndexOfFunction());
        putFunction(new LastIndexOfFunction());
        putFunction(new SplitFunction());
        putFunction(new TrimFunction());
    }
    
    private class TrimFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TRIM";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data str = params.get(0);
            
            // sprawdź czy typu string
            TypeChecker tc = new TypeChecker(str, getName(), 0, str.getErrorInfo(), interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            String sstr = (String)str.getValue();
            String res = sstr.trim();
            
            return Data.createStringData(res);
        }
    }
    
    private class SplitFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SPLIT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data str = params.get(0);
            Data regex = params.get(1);
            
            // sprawdź czy typu string
            TypeChecker tc = new TypeChecker(params, getName(), 0, interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz wartości
            String sstr = (String)str.getValue();
            String sregex = (String)regex.getValue();
            
            // wykonaj split
            String[] splited = sstr.split(sregex);
            
            // utwórz tablicę Data i wypełnij
            Data[] datas = new Data[splited.length];
            for (int i = 0; i < splited.length; ++i)
            {
                datas[i] = Data.createStringData(splited[i]);
            }
            
            // zwróc jako tablicę
            return Data.createArrayData(datas);
        }
    }
    
    private static class IndexLastIndexOfFunction
    {
        private enum Operation
        {
            INDEX_OF,
            LAST_INDEX_OF;
        }
        
        public Data perform(List<Data> params, Interpreter interpreter, IFunction function, Operation operation)
        {
            // parametry: <all, all>
            Data str = params.get(0);
            Data substr = params.get(1);
            
            // sprawdź czy typu string
            TypeChecker tc = new TypeChecker(params, function.getName(), 0, interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            String sstr = (String)str.getValue();
            String ssubstr = (String)substr.getValue();
            
            int indexOf;
            if (operation.equals(Operation.INDEX_OF))
            {
                indexOf = sstr.indexOf(ssubstr);
            }
            else
            {
                indexOf = sstr.lastIndexOf(ssubstr);
            }
            
            return Data.createIntData(indexOf);
        }
    }
    
    private class IndexOfFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "INDEX_OF";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new IndexLastIndexOfFunction().perform(params, interpreter, this, 
                    IndexLastIndexOfFunction.Operation.INDEX_OF);
        }
    }
    
    private class LastIndexOfFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "LAST_INDEX_OF";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new IndexLastIndexOfFunction().perform(params, interpreter, this, 
                    IndexLastIndexOfFunction.Operation.LAST_INDEX_OF);
        }
    }
    
    private class ReplaceFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "REPLACE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, all>
            Data str = params.get(0);
            Data b = params.get(1);
            Data replacement = params.get(2);
            
            // sprawdź czy str typu string
            TypeChecker tc = new TypeChecker(str, getName(), 0, str.getErrorInfo(), interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // sprawdź czy b typu int lub string
            tc = new TypeChecker(b, getName(), 1, b.getErrorInfo(), interpreter, DataType.STRING, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // sprawdź czy replacement typu string
            tc = new TypeChecker(replacement, getName(), 0, str.getErrorInfo(), interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz wartości
            String sstr = (String)str.getValue();
            String sreplacement = (String)replacement.getValue();
            
            // jeżeli b to string
            if (b.getDataType().equals(DataType.STRING))
            {
                String regex = (String)b.getValue();
                try
                {
                    String res = sstr.replaceAll(regex, sreplacement);
                    return Data.createStringData(res);
                }
                catch (PatternSyntaxException ex)
                {
                    return ErrorConstruct.REGEX_ERROR(getName(), b.getErrorInfo(), interpreter, regex);
                }
            }
            // inaczej int
            else
            {
                int ib = (int)b.getValue();
                if (ib < 0 || ib > sstr.length())
                {
                    return ErrorConstruct.INDEX_OUT_OF_BOUNDS(getName(), b.getErrorInfo(), interpreter, ib);
                }
                
                StringBuilder sb = new StringBuilder(sstr);
                sb.replace(ib, ib + sreplacement.length(), sreplacement);
                String res = sb.toString();
                
                return Data.createStringData(res);
            }
        }
    }
    
    private static class StartsEndsWithFunction
    {
        private enum Operation
        {
            STARTS_WITH,
            ENDS_WITH;
        }
        
        public Data perform(List<Data> params, Interpreter interpreter, IFunction function, Operation operation)
        {
            // parametry: <all, all>
            Data str = params.get(0);
            Data prefixSufix = params.get(1);
            
            // sprawdź czy typu string
            TypeChecker tc = new TypeChecker(params, function.getName(), 0, interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz wartość
            String sstr = (String)str.getValue();
            String sprefixSufix = (String)prefixSufix.getValue();
            
            boolean res;
            if (operation.equals(Operation.STARTS_WITH))
            {
                // sprawdź czy str zaczyna się od prefix
                res = sstr.startsWith(sprefixSufix);
            }
            else
            {
                // sprawdź czy str kończy się na sufix
                res = sstr.endsWith(sprefixSufix);
            }
            
            return Data.createBoolData(res);
        }
    }
    
    private class StartsWithFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "STARTS_WITH";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new StartsEndsWithFunction().perform(params, interpreter, this, 
                    StartsEndsWithFunction.Operation.STARTS_WITH);
        }
    }
    
    private class EndsWithFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ENDS_WITH";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new StartsEndsWithFunction().perform(params, interpreter, this, 
                    StartsEndsWithFunction.Operation.ENDS_WITH);
        }
    }
    
    private class InsertFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "INSERT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, all>
            Data str = params.get(0);
            Data index = params.get(1);
            Data toInsert = params.get(2);
            
            // sprawdź czy str typu string
            TypeChecker tc = new TypeChecker(str, getName(), 0, str.getErrorInfo(), interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // sprawdź czy index typu int
            tc = new TypeChecker(index, getName(), 0, index.getErrorInfo(), interpreter, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // sprawdź czy toInsert typu string
            tc = new TypeChecker(toInsert, getName(), 0, toInsert.getErrorInfo(), interpreter, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz wartości
            String sstr = (String)str.getValue();
            int iindex = (int)index.getValue();
            String stoInsert = (String)toInsert.getValue();
            
            // sprawdź czy iindex nie wychodzi poza granicę sstr
            if (iindex < 0 || iindex > sstr.length())
            {
                return ErrorConstruct.INDEX_OUT_OF_BOUNDS(getName(), index.getErrorInfo(), interpreter, iindex);
            }
            
            // użyj klasy StringBuilder do wstawienia
            StringBuilder sb = new StringBuilder(sstr);
            sb.insert(iindex, stoInsert);
            String res = sb.toString();
            
            return Data.createStringData(res);
        }
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
