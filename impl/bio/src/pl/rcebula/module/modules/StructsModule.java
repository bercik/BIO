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
import pl.rcebula.internals.data_types.Struct;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class StructsModule extends Module
{
    @Override
    public String getName()
    {
        return "structs";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new HasFieldFunction());
    }
    
    private class HasFieldFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "HAS_FIELD";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, id>
            Data ds = params.get(0);
            Data did = params.get(1);
            
            // sprawdź czy typu struct
            TypeChecker tc = new TypeChecker(ds, getName(), 0, ds.getErrorInfo(), interpreter, DataType.STRUCT);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz strukturę i id
            String id = (String)did.getValue();
            
            // sprawdź czy struktura zawiera pola
            String[] fields = id.split("\\.");
            Data d = ds;
            for (String field : fields)
            {
                if (!d.getDataType().equals(DataType.STRUCT))
                {
                    return Data.createBoolData(false);
                }
                
                Struct struct = (Struct)d.getValue();
                d = struct.getField(field);
                
                if (d == null)
                {
                    return Data.createBoolData(false);
                }
            }
            
            return Data.createBoolData(true);
        }
    }
}
