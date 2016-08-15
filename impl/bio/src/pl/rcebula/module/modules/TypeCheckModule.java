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
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class TypeCheckModule extends Module
{
    @Override
    public String getName()
    {
        return "type_check";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new IsErrorFunction());
        putFunction(new IsIntFunction());
        putFunction(new IsFloatFunction());
        putFunction(new IsBoolFunction());
        putFunction(new IsNoneFunction());
        putFunction(new IsStringFunction());
        putFunction(new IsArrayFunction());
        putFunction(new IsDictFunction());
        putFunction(new IsTupleFunction());
        putFunction(new IsNumberFunction());
        putFunction(new IsCollectionFunction());
        putFunction(new AreSameTypeFunction());
    }

    private static class CheckVarType
    {
        public static Data check(String funName, Interpreter interpreter, Data var, DataType expected)
        {
            // sprawdź czy nie error
            TypeChecker tc = new TypeChecker(var, funName, 0, var.getErrorInfo(), interpreter, DataType.ARRAY,
                    DataType.BOOL, DataType.DICT, DataType.FLOAT, DataType.INT, DataType.NONE,
                    DataType.STRING, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            if (var.getDataType().equals(expected))
            {
                return Data.createBoolData(true);
            }
            else
            {
                return Data.createBoolData(false);
            }
        }

        public static Data checkError(Data var)
        {
            if (var.getDataType().equals(DataType.ERROR))
            {
                return Data.createBoolData(true);
            }
            else
            {
                return Data.createBoolData(false);
            }
        }

        public static Data check(String funName, Interpreter interpreter, Data var, DataType... expected)
        {
            // sprawdź czy nie error
            TypeChecker tc = new TypeChecker(var, funName, 0, var.getErrorInfo(), interpreter, DataType.ARRAY,
                    DataType.BOOL, DataType.DICT, DataType.FLOAT, DataType.INT, DataType.NONE,
                    DataType.STRING, DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }

            for (DataType dt : expected)
            {
                if (var.getDataType().equals(dt))
                {
                    return Data.createBoolData(true);
                }
            }

            return Data.createBoolData(false);
        }
    }

    private class AreSameTypeFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ARE_SAME_TYPE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, all, <all>*>
            Data par1 = params.get(0);

            // iteruj po wszystkich, sprawdź czy któryś to nie error
            for (int i = 0; i < params.size(); ++i)
            {
                Data p = params.get(i);
                // sprawdź czy nie error
                TypeChecker tc = new TypeChecker(p, getName(), i, p.getErrorInfo(), interpreter,
                        DataType.ARRAY, DataType.BOOL, DataType.DICT, DataType.FLOAT, DataType.INT,
                        DataType.NONE, DataType.STRING, DataType.TUPLE);
                if (tc.isError())
                {
                    return tc.getError();
                }
            }

            // iteruj po pozostałych
            for (int i = 1; i < params.size(); ++i)
            {
                Data par2 = params.get(i);
                
                // porównaj, jeżeli różne to false
                if (!par1.getDataType().equals(par2.getDataType()))
                {
                    return Data.createBoolData(false);
                }
            }
            
            // zwróć true
            return Data.createBoolData(true);
        }
    }

    private class IsNumberFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_NUMBER";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.INT, DataType.FLOAT);
        }
    }

    private class IsCollectionFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_COLLECTION";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.ARRAY, DataType.TUPLE);
        }
    }

    private class IsArrayFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_ARRAY";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.ARRAY);
        }
    }

    private class IsDictFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_DICT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.DICT);
        }
    }

    private class IsTupleFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_TUPLE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.TUPLE);
        }
    }

    private class IsBoolFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_BOOL";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.BOOL);
        }
    }

    private class IsStringFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_STRING";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.STRING);
        }
    }

    private class IsNoneFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_NONE";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.NONE);
        }
    }

    private class IsErrorFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_ERROR";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.checkError(par);
        }
    }

    private class IsIntFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_INT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.INT);
        }
    }

    private class IsFloatFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_FLOAT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all>
            Data par = params.get(0);

            return CheckVarType.check(getName(), interpreter, par, DataType.FLOAT);
        }
    }
}
