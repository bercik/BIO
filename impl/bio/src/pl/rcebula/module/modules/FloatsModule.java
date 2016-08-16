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
import pl.rcebula.module.utils.operation.IOperation;
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class FloatsModule extends Module
{
    @Override
    public String getName()
    {
        return "floats";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new CeilFunction());
        putFunction(new FloorFunction());
    }
    
    private class FloorFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "FLOOR";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new CeilFloorFunction().perform(params, interpreter, this, this);
        }

        @Override
        public Data perform(int... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(float... nums)
        {
            int floored = (int)Math.floor(nums[0]);
            return Data.createIntData(floored);
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
    }
    
    private class CeilFunction implements IFunction, IOperation
    {
        @Override
        public String getName()
        {
            return "CEIL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            return new CeilFloorFunction().perform(params, interpreter, this, this);
        }

        @Override
        public Data perform(int... nums)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(float... nums)
        {
            int ceiled = (int)Math.ceil(nums[0]);
            return Data.createIntData(ceiled);
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
    }
    
    private class CeilFloorFunction
    {
        private Data perform(List<Data> params, Interpreter interpreter, IFunction function, IOperation operation)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            // sprawdź czy typu float lub int
            TypeChecker tc = new TypeChecker(par, function.getName(), 0, par.getErrorInfo(), interpreter, 
                    DataType.FLOAT, DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli float
            if (par.getDataType().equals(DataType.FLOAT))
            {
                float val = (float)par.getValue();
                return operation.perform(new float[] { val });
            }
            // inaczej int, więc zwracamy
            else
            {
                return par;
            }
        }
    }
}
