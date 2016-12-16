/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class TimeModule extends Module
{
    @Override
    public String getName()
    {
        return "time";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new SleepFunction());
        putFunction(new TimeFunction());
    }
    
    private class TimeFunction implements IFunction
    {
        private boolean firstCall = true;
        private long firstCallTime;
        
        @Override
        public String getName()
        {
            return "TIME";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, 
                ErrorInfo callErrorInfo)
        {
            // no parameters
            if (firstCall)
            {
                firstCall = false;
                firstCallTime = System.currentTimeMillis();
                return Data.createIntData(0);
            }
            else
            {
                int diffTime = (int)(System.currentTimeMillis() - firstCallTime);
                return Data.createIntData(diffTime);
            }
        }
    }
    
    private class SleepFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SLEEP";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter, 
                ErrorInfo callErrorInfo)
        {
            // parametr: <all>
            Data dInterval = params.get(0);
            
            // sprawdź czy typu int
            TypeChecker tc = new TypeChecker(dInterval, getName(), 0, dInterval.getErrorInfo(), interpreter, 
                    DataType.INT);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz wartość
            int interval = (int)dInterval.getValue();
            // sprawdź czy większe od zera
            if (interval < 0)
            {
                // zwróc error
                return ErrorConstruct.INTERVAL_LESS_THAN_ZERO(getName(), dInterval.getErrorInfo(), 
                        interpreter, interval);
            }
            
            try
            {
                // uśpij na podaną ilość ms
                TimeUnit.MILLISECONDS.sleep(interval);
            }
            catch (InterruptedException ex)
            {
                // shouldn't occur ever
            }
            
            return Data.createNoneData();
        }
    }
}
