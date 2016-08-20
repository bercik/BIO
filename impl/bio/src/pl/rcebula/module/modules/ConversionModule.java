/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.data_types.Tuple;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.Datas;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class ConversionModule extends Module
{
    @Override
    public String getName()
    {
        return "conversion";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new ToStringFunction());
        putFunction(new ToIntFunction());
        putFunction(new ToFloatFunction());
        putFunction(new ToBoolFunction());
        putFunction(new ToArrayFunction());
        putFunction(new ToTupleFunction());
    }
    
    private class ToTupleFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TO_TUPLE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            // sprawdź czy typu array lub tuple
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli array
            if (par.getDataType().equals(DataType.ARRAY))
            {
                Data[] datas = (Data[])par.getValue();
                Data[] arr = new Data[datas.length];
                // kopiujemy do nowej tablicy
                System.arraycopy(datas, 0, arr, 0, datas.length);
                
                return Data.createTupleData(new Tuple(arr));
            }
            // inaczej tuple
            else
            {
                return par;
            }
        }
    }
    
    private class ToArrayFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TO_ARRAY";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            // sprawdź czy typu array lub tuple
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, 
                    DataType.ARRAY, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli array
            if (par.getDataType().equals(DataType.ARRAY))
            {
                return par;
            }
            // inaczej tuple
            else
            {
                Tuple tup = (Tuple)par.getValue();
                Data[] datas = (Data[])tup.getValues();
                Data[] arr = new Data[datas.length];
                // kopiujemy do nowej tablicy
                System.arraycopy(datas, 0, arr, 0, datas.length);
                
                return Data.createArrayData(arr);
            }
        }
    }
    
    private class ToBoolFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TO_BOOL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            // sprawdź czy typu bool, float, int lub string
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, DataType.BOOL, 
                    DataType.FLOAT, DataType.INT, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli bool
            if (par.getDataType().equals(DataType.BOOL))
            {
                return par;
            }
            // jeżeli float
            else if (par.getDataType().equals(DataType.FLOAT))
            {
                float val = (float)par.getValue();
                if (val == 0.0f)
                {
                    return Data.createBoolData(false);
                }
                else
                {
                    return Data.createBoolData(true);
                }
            }
            // jeżeli int
            else if (par.getDataType().equals(DataType.INT))
            {
                int val = (int)par.getValue();
                if (val == 0)
                {
                    return Data.createBoolData(false);
                }
                else
                {
                    return Data.createBoolData(true);
                }
            }
            // inaczej string
            else 
            {
                String val = (String)par.getValue();
                if (val.equals("true"))
                {
                    return Data.createBoolData(true);
                }
                else if (val.equals("false"))
                {
                    return Data.createBoolData(false);
                }
                else
                {
                    return ErrorConstruct.CONVERSION_ERROR(getName(), par.getErrorInfo(), interpreter, par, 
                            DataType.BOOL);
                }
            }
        }
    }
    
    private class ToStringFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TO_STRING";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            return Data.createStringData(Datas.toStr(par, false));
        }
    }
    
    private class ToIntFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TO_INT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            // sprawdź czy typu bool, float, int lub string
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, DataType.BOOL, 
                    DataType.FLOAT, DataType.INT, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli bool
            if (par.getDataType().equals(DataType.BOOL))
            {
                boolean val = (boolean)par.getValue();
                int res = val ? 1 : 0;
                return Data.createIntData(res);
            }
            // jeżeli float
            else if (par.getDataType().equals(DataType.FLOAT))
            {
                float val = (float)par.getValue();
                int res = Math.round(val);
                return Data.createIntData(res);
            }
            // jeżeli int
            else if (par.getDataType().equals(DataType.INT))
            {
                return par;
            }
            // inaczej string
            else 
            {
                String val = (String)par.getValue();
                // spróbuj
                try
                {
                    int res = Integer.parseInt(val);
                    return Data.createIntData(res);
                }
                catch (NumberFormatException ex)
                {
                    return ErrorConstruct.CONVERSION_ERROR(getName(), par.getErrorInfo(), interpreter, par, 
                            DataType.INT);
                }
            }
        }
    }
    
    private class ToFloatFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "TO_FLOAT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            Data par = params.get(0);
            
            // sprawdź czy typu bool, float, int lub string
            TypeChecker tc = new TypeChecker(par, getName(), 0, par.getErrorInfo(), interpreter, DataType.BOOL, 
                    DataType.FLOAT, DataType.INT, DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // jeżeli bool
            if (par.getDataType().equals(DataType.BOOL))
            {
                boolean val = (boolean)par.getValue();
                float res = val ? 1.0f : 0.0f;
                return Data.createFloatData(res);
            }
            // jeżeli float
            else if (par.getDataType().equals(DataType.FLOAT))
            {
                return par;
            }
            // jeżeli int
            else if (par.getDataType().equals(DataType.INT))
            {
                int val = (int)par.getValue();
                float res = (float)val;
                return Data.createFloatData(res);
            }
            // inaczej string
            else 
            {
                String val = (String)par.getValue();
                // spróbuj
                try
                {
                    float res = Float.parseFloat(val);
                    return Data.createFloatData(res);
                }
                catch (NumberFormatException ex)
                {
                    return ErrorConstruct.CONVERSION_ERROR(getName(), par.getErrorInfo(), interpreter, par, 
                            DataType.FLOAT);
                }
            }
        }
    }
}
