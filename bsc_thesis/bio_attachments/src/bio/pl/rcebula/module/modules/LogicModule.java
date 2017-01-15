/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.Arrays;
import java.util.List;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.operation.CollectionsOperation;
import pl.rcebula.module.utils.operation.IOperation;
import pl.rcebula.module.utils.operation.OperationDataType;
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class LogicModule extends Module
{
    @Override
    public String getName()
    {
        return "logic";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new NotFunction());
        putFunction(new AndFunction());
        putFunction(new OrFunction());
    }
    
    private class NotFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "NOT";
        }

        @Override
        public Data perform(int... nums)
        {
            int res = ~nums[0];
            return Data.createIntData(res);
        }

        @Override
        public Data perform(float... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(boolean... bools)
        {
            boolean res = !bools[0];
            return Data.createBoolData(res);
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
            Data par = params.get(0);
            
            // sprawdź czy int, bool, collection
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.TUPLE, DataType.INT, DataType.BOOL);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli liczba
            if (par.getDataType().equals(DataType.INT))
            {
                int num = (int)par.getValue();
                return perform(new int[] { num });
            }
            // jeżeli bool
            else if (par.getDataType().equals(DataType.BOOL))
            {
                boolean bool = (boolean)par.getValue();
                return perform(new boolean[] { bool });
            }
            // inaczej kolekcja
            else
            {
                return new CollectionsOperation().perform(getName(), 
                        Arrays.asList(OperationDataType.BOOL, OperationDataType.INT), this, interpreter, par);
            }
        }
    }
    
    private class AndFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "AND";
        }

        @Override
        public Data perform(int... nums)
        {
            int res = nums[0] & nums[1];
            
            for (int i = 2; i < nums.length; ++i)
            {
                res &= nums[i];
            }
            
            return Data.createIntData(res);
        }

        @Override
        public Data perform(float... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(boolean... bools)
        {
            boolean res = bools[0] & bools[1];
            
            for (int i = 2; i < bools.length; ++i)
            {
                res &= bools[i];
            }
            
            return Data.createBoolData(res);
        }

        @Override
        public Data perform(String... strings)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, <all>*>
            Data par1 = params.get(0);
            
            // sprawdź czy typu int, bool, array, tuple
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.BOOL, DataType.INT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli liczba
            if (par1.getDataType().equals(DataType.INT))
            {
                int res = (int)par1.getValue();
                
                // iteruj po reszcie elementów sprawdzając ich typ i wykonując operację
                for (int i = 1; i < params.size(); ++i)
                {
                    Data p = params.get(i);
                    tc = new TypeChecker(p, getName(), i, p.getErrorInfo(), interpreter, DataType.INT);
                    if (tc.isError())
                    {
                        return tc.getError();
                    }
                    
                    int ip = (int)p.getValue();
                    
                    res &= ip;
                }
                
                return Data.createIntData(res);
            }
            // inaczej jeżeli bool
            else if (par1.getDataType().equals(DataType.BOOL))
            {
                boolean res = (boolean)par1.getValue();
                
                // iteruj po reszcie elementów sprawdzając ich typ i wykonując operację
                for (int i = 1; i < params.size(); ++i)
                {
                    Data p = params.get(i);
                    tc = new TypeChecker(p, getName(), i, p.getErrorInfo(), interpreter, DataType.BOOL);
                    if (tc.isError())
                    {
                        return tc.getError();
                    }
                    
                    boolean bp = (boolean)p.getValue();
                    
                    res &= bp;
                }
                
                return Data.createBoolData(res);
            }
            // inaczej kolekcja
            else
            {
                return new CollectionsOperation().perform(getName(), 
                        Arrays.asList(OperationDataType.BOOL, OperationDataType.INT), this, interpreter, params);
            }
        }
    }
    
    private class OrFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "OR";
        }

        @Override
        public Data perform(int... nums)
        {
            int res = nums[0] | nums[1];
            
            for (int i = 2; i < nums.length; ++i)
            {
                res |= nums[i];
            }
            
            return Data.createIntData(res);
        }

        @Override
        public Data perform(float... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(boolean... bools)
        {
            boolean res = bools[0] | bools[1];
            
            for (int i = 2; i < bools.length; ++i)
            {
                res |= bools[i];
            }
            
            return Data.createBoolData(res);
        }

        @Override
        public Data perform(String... strings)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, <all>*>
            Data par1 = params.get(0);
            
            // sprawdź czy typu int, bool, array, tuple
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.BOOL, DataType.INT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli liczba
            if (par1.getDataType().equals(DataType.INT))
            {
                int res = (int)par1.getValue();
                
                // iteruj po reszcie elementów sprawdzając ich typ i wykonując operację
                for (int i = 1; i < params.size(); ++i)
                {
                    Data p = params.get(i);
                    tc = new TypeChecker(p, getName(), i, p.getErrorInfo(), interpreter, DataType.INT);
                    if (tc.isError())
                    {
                        return tc.getError();
                    }
                    
                    int ip = (int)p.getValue();
                    
                    res |= ip;
                }
                
                return Data.createIntData(res);
            }
            // inaczej jeżeli bool
            else if (par1.getDataType().equals(DataType.BOOL))
            {
                boolean res = (boolean)par1.getValue();
                
                // iteruj po reszcie elementów sprawdzając ich typ i wykonując operację
                for (int i = 1; i < params.size(); ++i)
                {
                    Data p = params.get(i);
                    tc = new TypeChecker(p, getName(), i, p.getErrorInfo(), interpreter, DataType.BOOL);
                    if (tc.isError())
                    {
                        return tc.getError();
                    }
                    
                    boolean bp = (boolean)p.getValue();
                    
                    res |= bp;
                }
                
                return Data.createBoolData(res);
            }
            // inaczej kolekcja
            else
            {
                return new CollectionsOperation().perform(getName(), 
                        Arrays.asList(OperationDataType.BOOL, OperationDataType.INT), this, interpreter, params);
            }
        }
    }
}
