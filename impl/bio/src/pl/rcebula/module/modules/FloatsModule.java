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
        putFunction(new IsNanFunctions());
    }
    
    private class IsNanFunctions implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_NAN";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, ErrorInfo callErrorInfo)
        {
            // parametr: <all>
            Data dVal = params.get(0);
            
            // sprawdź czy typu float
            TypeChecker tc = new TypeChecker(dVal, getName(), 0, dVal.getErrorInfo(), interpreter, 
                    DataType.FLOAT);
            
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz wartość
            float val = (float)dVal.getValue();
            
            // zwróc czy jest NaN
            return Data.createBoolData(Float.isNaN(val));
        }
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
        public Data perform(int[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(float[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            int floored = (int)Math.floor(nums[0]);
            return Data.createIntData(floored);
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
        public Data perform(int[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Data perform(float[] nums, ErrorInfo[] errorInfos, Interpreter interpreter)
        {
            int ceiled = (int)Math.ceil(nums[0]);
            return Data.createIntData(ceiled);
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
                return operation.perform(new float[] { val }, new ErrorInfo[] { par.getErrorInfo() }, interpreter);
            }
            // inaczej int, więc zwracamy
            else
            {
                return par;
            }
        }
    }
}
