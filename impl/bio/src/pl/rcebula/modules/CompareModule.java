/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.List;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.Tuple;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.modules.utils.Collections;
import pl.rcebula.modules.utils.Datas;
import pl.rcebula.modules.utils.Numbers;
import pl.rcebula.modules.utils.error_codes.ErrorConstruct;
import pl.rcebula.modules.utils.type_checker.TypeChecker;
import pl.rcebula.modules.utils.type_checker.TypeCheckerNumber;
import pl.rcebula.modules.utils.type_checker.TypeCheckerNumberCollection;

/**
 *
 * @author robert
 */
public class CompareModule extends Module
{
    @Override
    public String getName()
    {
        return "compare";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new LsFunction());
        putFunction(new LeFunction());
        putFunction(new GtFunction());
        putFunction(new GeFunction());
        putFunction(new EqFunction());
        putFunction(new NeqFunction());
        putFunction(new IsInRangeFunction());
    }
    
    private interface ICompareOperation
    {
        boolean compare(float a, float b);
        
        boolean compare(int a, int b);
        
        String getName();
    }
    
    private class PerformCompare
    {
        public Data perform(List<Data> params, CallFrame currentFrame, Interpreter interpreter, 
                ICompareOperation compOp)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);
            
            // sprawdź czy pierwszy parametr jest typu int, float, array lub tuple
            TypeChecker tc = new TypeChecker(par1, compOp.getName(), 0, par1.getErrorInfo(), interpreter,
                    DataType.ARRAY, DataType.FLOAT, DataType.INT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli int lub float
            if (Numbers.isNumber(par1))
            {
                // sprawdzamy czy drugi parametr to liczba
                TypeCheckerNumber tcn = new TypeCheckerNumber(par2, compOp.getName(), 1, par2.getErrorInfo(), 
                        interpreter);
                if (tcn.isError())
                {
                    return tcn.getError();
                }
                
                // jeżeli którykolwiek z parametrów to float
                if (tcn.isFloat() || par1.getDataType().equals(DataType.FLOAT))
                {
                    float val1 = Numbers.getFloat(par1);
                    float val2 = Numbers.getFloat(par2);
                    
                    boolean compResult = compOp.compare(val1, val2);
                    return Data.createBoolData(compResult);
                }
                // inaczej oba inty
                else
                {
                    int val1 = (int)par1.getValue();
                    int val2 = (int)par2.getValue();
                    
                    boolean compResult = compOp.compare(val1, val2);
                    return Data.createBoolData(compResult);
                }
            }
            // inaczej tuple lub array
            else
            {
                // sprawdzamy czy drugi parametr jest typu array lub tuple
                tc = new TypeChecker(par2, compOp.getName(), 1, par2.getErrorInfo(), interpreter, DataType.ARRAY, 
                        DataType.TUPLE);
                
                if (tc.isError())
                {
                    return tc.getError();
                }
                
                // wydobywamy tablicę
                Data[] data1 = Collections.getDatas(par1);
                Data[] data2 = Collections.getDatas(par2);
                
                // sprawdzamy czy mają taki sam rozmiar
                if (data1.length != data2.length)
                {
                    return ErrorConstruct.COLLECTIONS_DIFFRENT_SIZES(compOp.getName(), par2.getErrorInfo(), 
                            interpreter, 1);
                }
                
                Data[] result = new Data[data1.length];
                // przechodzimy jednocześnie po obu porównując elementy o tych samych indeksach i 
                // zapisując wynik w tablicy
                for (int i = 0; i < data1.length; ++i)
                {
                    Data d1 = data1[i];
                    Data d2 = data2[i];
                    
                    // sprawdzamy czy pierwsza to liczba
                    TypeCheckerNumberCollection tcnc1 = new TypeCheckerNumberCollection(d1, compOp.getName(), 
                            0, i, par1.getErrorInfo(), interpreter);
                    
                    if (tcnc1.isError())
                    {
                        return tcnc1.getError();
                    }
                    
                    // spradzamy czy druga to liczba
                    TypeCheckerNumberCollection tcnc2 = new TypeCheckerNumberCollection(d2, 
                            compOp.getName(), 1, i, par2.getErrorInfo(), interpreter);
                    
                    if (tcnc2.isError())
                    {
                        return tcnc2.getError();
                    }
                    
                    // jeżeli jedna jest floatem to
                    if (tcnc1.isFloat() || tcnc2.isFloat())
                    {
                        float val1 = Numbers.getFloat(d1);
                        float val2 = Numbers.getFloat(d2);
                        
                        // porównujemy i wstawiamy do tablicy
                        result[i] = Data.createBoolData(compOp.compare(val1, val2));
                    }
                    // inaczej oba inty
                    else
                    {
                        int val1 = (int)d1.getValue();
                        int val2 = (int)d2.getValue();
                        
                        // porównujemy i wstawiamy do tablicy
                        result[i] = Data.createBoolData(compOp.compare(val1, val2));
                    }
                }
                
                // zwracamy tuplę
                return Data.createTupleData(new Tuple(result));
            }
        }
    }
    
    private class LsCompOp implements ICompareOperation
    {
        @Override
        public boolean compare(float a, float b)
        {
            return a < b;
        }

        @Override
        public boolean compare(int a, int b)
        {
            return a < b;
        }

        @Override
        public String getName()
        {
            return "LS";
        }
    }
    
    private class LsFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "LS";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformCompare().perform(params, currentFrame, interpreter, new LsCompOp());
        }
    }
    
    private class LeCompOp implements ICompareOperation
    {
        @Override
        public boolean compare(float a, float b)
        {
            return a <= b;
        }

        @Override
        public boolean compare(int a, int b)
        {
            return a <= b;
        }

        @Override
        public String getName()
        {
            return "LE";
        }
    }
    
    private class LeFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "LE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformCompare().perform(params, currentFrame, interpreter, new LeCompOp());
        }
    }
    
    private class GtCompOp implements ICompareOperation
    {
        @Override
        public boolean compare(float a, float b)
        {
            return a > b;
        }

        @Override
        public boolean compare(int a, int b)
        {
            return a > b;
        }

        @Override
        public String getName()
        {
            return "GT";
        }
    }
    
    private class GtFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformCompare().perform(params, currentFrame, interpreter, new GtCompOp());
        }
    }
    
    private class GeCompOp implements ICompareOperation
    {
        @Override
        public boolean compare(float a, float b)
        {
            return a >= b;
        }

        @Override
        public boolean compare(int a, int b)
        {
            return a >= b;
        }

        @Override
        public String getName()
        {
            return "GE";
        }
    }
    
    private class GeFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new PerformCompare().perform(params, currentFrame, interpreter, new GeCompOp());
        }
    }
    
    private class EqFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "EQ";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data d1 = params.get(0);
            Data d2 = params.get(1);
            
            boolean eq = Datas.equals(d1, d2);
            return Data.createBoolData(eq);
        }
    }
    
    private class NeqFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "NEQ";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data d1 = params.get(0);
            Data d2 = params.get(1);
            
            boolean neq = !Datas.equals(d1, d2);
            return Data.createBoolData(neq);
        }
    }
    
    private class IsInRangeFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_IN_RANGE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, all>
            Data d1 = params.get(0);
            Data d2 = params.get(1);
            Data d3 = params.get(2);
            
            // sprawdź czy wszystkie parametry są liczbami
            TypeCheckerNumber tcn1 = new TypeCheckerNumber(d1, getName(), 0, d1.getErrorInfo(), interpreter);
            if (tcn1.isError())
            {
                return tcn1.getError();
            }
            
            TypeCheckerNumber tcn2 = new TypeCheckerNumber(d2, getName(), 1, d2.getErrorInfo(), interpreter);
            if (tcn2.isError())
            {
                return tcn2.getError();
            }
            
            TypeCheckerNumber tcn3 = new TypeCheckerNumber(d3, getName(), 2, d3.getErrorInfo(), interpreter);
            if (tcn3.isError())
            {
                return tcn3.getError();
            }
            
            // jeżeli którakolwiek liczba to float to konwertuj wszystkie na float
            if (tcn1.isFloat() || tcn2.isFloat() || tcn3.isFloat())
            {
                float num = Numbers.getFloat(d1);
                float min = Numbers.getFloat(d2);
                float max = Numbers.getFloat(d3);
                
                // sprawdź czy min > max
                if (min > max)
                {
                    return ErrorConstruct.MIN_GREATER_THAN_MAX(getName(), d2.getErrorInfo(), interpreter);
                }
                
                // sprawdź czy num jest w zakresie <min, max>
                if (num >= min && num <= max)
                {
                    return Data.createBoolData(true);
                }
                else
                {
                    return Data.createBoolData(false);
                }
            }
            // inaczej wszystkie int
            else
            {
                int num = Numbers.getInt(d1);
                int min = Numbers.getInt(d2);
                int max = Numbers.getInt(d3);
                
                // sprawdź czy min > max
                if (min > max)
                {
                    return ErrorConstruct.MIN_GREATER_THAN_MAX(getName(), d2.getErrorInfo(), interpreter);
                }
                
                // sprawdź czy num jest w zakresie <min, max>
                if (num >= min && num <= max)
                {
                    return Data.createBoolData(true);
                }
                else
                {
                    return Data.createBoolData(false);
                }
            }
        }
    }
}
