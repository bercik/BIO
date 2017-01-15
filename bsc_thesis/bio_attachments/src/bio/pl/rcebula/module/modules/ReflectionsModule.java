/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.modules;

import java.util.List;
import java.util.Set;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.internals.interpreter.PerformCall;
import pl.rcebula.module.IFunction;
import pl.rcebula.module.Module;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;
import pl.rcebula.module.utils.type_checker.TypeChecker;

/**
 *
 * @author robert
 */
public class ReflectionsModule extends Module
{
    @Override
    public String getName()
    {
        return "reflections";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new CallByNameFunction());
        putFunction(new GetUserFunctionsNamesFunction());
    }
    
    private class CallByNameFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "CALL_BY_NAME";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry: <all, <all>*>
            Data name = params.get(0);
            // pozostałe parametry
            params = params.subList(1, params.size());
            
            // sprawdzamy czy name to string
            TypeChecker tc = new TypeChecker(name, getName(), 0, name.getErrorInfo(), interpreter, 
                    DataType.STRING);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            String sname = (String)name.getValue();
            // sprawdzamy czy funkcja użytkownika o takiej nazwie istnieje
            UserFunction uf = interpreter.getUserFunctions().get(sname);
            if (uf == null)
            {
                return ErrorConstruct.USER_FUNCTION_DOESNT_EXIST(getName(), name.getErrorInfo(), interpreter, 
                        sname);
            }
            
            int paramsSize = params.size();
            int ufParamsSize = uf.getParams().size();
            // sprawdczamy czy przekazano odpowiednią ilość parametrów
            if (params.size() != uf.getParams().size())
            {
                return ErrorConstruct.TOO_LITTLE_PARAMETERS(getName(), name.getErrorInfo(), interpreter, 
                        sname, ufParamsSize, paramsSize);
            }
            
            // wywołujemy funkcję sname z parametrami params, zaznaczając, że interesuje nas zwracana przez nią
            // wartość
            new PerformCall().perform(params, true, uf, interpreter, name.getErrorInfo());
            
            // zwracamy null ponieważ chcemy, żeby wartość zwrócona z wywołanej funkcji była brana pod uwagę, 
            // a nie z naszej funkcji
            return null;
        }
    }
    
    private class GetUserFunctionsNamesFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "GET_USER_FUNCTIONS_NAMES";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // bez parametrów
            Set<String> funNames = interpreter.getUserFunctions().keySet();
            Data[] datas = new Data[funNames.size()];
            
            // dodaj wszystkie nazwy funkcji do tablicy
            int it = 0;
            for (String funName : funNames)
            {
                datas[it++] = Data.createStringData(funName);
            }
            
            // zwróć jako array
            return Data.createArrayData(datas);
        }
    }
}
