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
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;

/**
 *
 * @author robert
 */
public class IoModule extends Module
{
    @Override
    public String getName()
    {
        return "io";
    }
    
    public IoModule()
    {
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new PrintFunction());
        putFunction(new PrintlnFunction());
    }
    
    private class PrintFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "PRINT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry <val>*
            String toPrint = "";
            for (Data d : params)
            {
                String str = (String)interpreter.getBuiltinFunctions().callFunction("TO_STRING", Arrays.asList(d), 
                        currentFrame, interpreter, d.getErrorInfo()).getValue();
                toPrint += str + " ";
            }
            if (params.size() > 0)
            {
                toPrint = toPrint.substring(0, toPrint.length() - 1);
            }
            
            System.out.print(toPrint);
            
            return Data.createStringData(toPrint);
        }
    }
    
    private class PrintlnFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "PRINTLN";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <val>*
            String toPrint = "";
            
            if (params.size() == 0)
            {
                toPrint = "\n";
            }
            else
            {
                for (Data d : params)
                {
                    String str = (String)interpreter.getBuiltinFunctions().callFunction("TO_STRING", Arrays.asList(d), 
                            currentFrame, interpreter, d.getErrorInfo()).getValue();
                    toPrint += str + "\n";
                }
            }
            
            System.out.print(toPrint);
            
            return Data.createStringData(toPrint);
        }
    }
}
