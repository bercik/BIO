/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
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
public class IntsModule extends Module
{
    @Override
    public String getName()
    {
        return "ints";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new RshiftFunction());
        putFunction(new LshiftFunction());
        putFunction(new XorFunction());
    }
    
    private class XorFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "XOR";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);
            
            // sprawdź czy tupy int, array lub tuple
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.INT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli int
            if (par1.getDataType().equals(DataType.INT))
            {
                // sprawdź czy drugi też int
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.INT);
                if (tc.isError())
                {
                    return tc.getError();
                }
                
                // pobierz inty
                int a = (int)par1.getValue();
                int b = (int)par2.getValue();
                
                // wykonaj operację
                return perform(new int[] { a, b }, 
                        new ErrorInfo[] { par1.getErrorInfo(), par2.getErrorInfo() }, interpreter);
            }
            // inaczej kolekcje
            else
            {
                // sprawdź czy drugi też kolekcja
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.ARRAY, 
                        DataType.TUPLE);
                if (tc.isError())
                {
                    return tc.getError();
                }
                
                // wykonaj operację
                return new CollectionsOperation().perform(getName(), OperationDataType.INT, this, interpreter, 
                        par1, par2);
            }
        }

        @Override
        public Data perform(int[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            int xor = nums[0] ^ nums[1];
            return Data.createIntData(xor);
        }

        @Override
        public Data perform(float[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(boolean[] bools, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(String[] strings, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private class RshiftFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "RSHIFT";
        }

        @Override
        public Data perform(int[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            int rshift = nums[0] >> nums[1];
            return Data.createIntData(rshift);
        }

        @Override
        public Data perform(float[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(boolean[] bools, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(String[] strings, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);
            
            // sprawdź czy tupy int, array lub tuple
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.INT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli int
            if (par1.getDataType().equals(DataType.INT))
            {
                // sprawdź czy drugi też int
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.INT);
                if (tc.isError())
                {
                    return tc.getError();
                }
                
                // pobierz inty
                int num = (int)par1.getValue();
                int shift = (int)par2.getValue();
                
                // wykonaj operację
                return perform(new int[] { num, shift }, 
                        new ErrorInfo[] { par1.getErrorInfo(), par2.getErrorInfo() }, interpreter);
            }
            // inaczej kolekcje
            else
            {
                // sprawdź czy drugi też kolekcja
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.ARRAY, 
                        DataType.TUPLE);
                if (tc.isError())
                {
                    return tc.getError();
                }
                
                // wykonaj operację
                return new CollectionsOperation().perform(getName(), OperationDataType.INT, this, interpreter, 
                        par1, par2);
            }
        }
    }
    
    private class LshiftFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "LSHIFT";
        }

        @Override
        public Data perform(int[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            int rshift = nums[0] << nums[1];
            return Data.createIntData(rshift);
        }

        @Override
        public Data perform(float[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(boolean[] bools, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(String[] strings, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all>
            Data par1 = params.get(0);
            Data par2 = params.get(1);
            
            // sprawdź czy tupy int, array lub tuple
            TypeChecker tc = new TypeChecker(par1, getName(), 0, par1.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.INT, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli int
            if (par1.getDataType().equals(DataType.INT))
            {
                // sprawdź czy drugi też int
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.INT);
                if (tc.isError())
                {
                    return tc.getError();
                }
                
                // pobierz inty
                int num = (int)par1.getValue();
                int shift = (int)par2.getValue();
                
                // wykonaj operację
                return perform(new int[] { num, shift }, 
                        new ErrorInfo[] { par1.getErrorInfo(), par2.getErrorInfo() }, interpreter);
            }
            // inaczej kolekcje
            else
            {
                // sprawdź czy drugi też kolekcja
                tc = new TypeChecker(par2, getName(), 1, par2.getErrorInfo(), interpreter, DataType.ARRAY, 
                        DataType.TUPLE);
                if (tc.isError())
                {
                    return tc.getError();
                }
                
                // wykonaj operację
                return new CollectionsOperation().perform(getName(), OperationDataType.INT, this, interpreter, 
                        par1, par2);
            }
        }
    }
}
