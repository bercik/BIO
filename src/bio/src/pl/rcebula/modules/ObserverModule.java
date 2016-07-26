/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.rcebula.intermediate_code.UserFunction;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.ErrorCodes;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.MyError;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.utils.Pair;

/**
 *
 * @author robert
 */
public class ObserverModule extends Module
{
    @Override
    public String getName()
    {
        return "observer";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new AttachToEventFunctions());
        putFunction(new IsAttachedFunction());
        putFunction(new DetachFromEventFunction());
    }

    private class AttachToEventFunctions implements IFunction
    {
        @Override
        public String getName()
        {
            return "ATTACH_TO_EVENT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            List<Pair<String, UserFunction>> tmp = new ArrayList<>();
            // parametry <id, id>+
            for (int i = 0; i < params.size(); i += 2)
            {
                Data eventData = params.get(i);
                Data callbackData = params.get(i + 1);
                
                String eventName = (String)eventData.getValue();
                String callbackName = (String)callbackData.getValue();
                
                UserFunction eventUf = interpreter.getUserFunctions().get(eventName);
                // jeżeli jeszcze nie ma takiego obserwatora to dodajemy do tymczasowej listy
                // jest to robione po to, że w przypadku wystąpienia błędu żaden obserwator nie zostanie dodany
                boolean error = true;
                if (!eventUf.getObservers().contains(callbackName))
                {
                    // musimy też przeszukać tymczasową listę
                    boolean duplicate = false;
                    for (Pair<String, UserFunction> p : tmp)
                    {
                        if (p.getLeft().equals(callbackName) && 
                                p.getRight().getName().equals(eventUf.getName()))
                        {
                            duplicate = true;
                            break;
                        }
                    }
                    
                    if (!duplicate)
                    {
                        error = false;
                        tmp.add(new Pair<>(callbackName, eventUf));
                    }
                }
                // błąd
                if (error)
                {
                    String message = "callback " + callbackName + " is already attached to event " + eventName;
                    MyError err = new MyError(getName(), message, 
                            ErrorCodes.CALLBACK_ALREADY_ATTACHED.getCode(), null, eventData.getErrorInfo(), 
                            interpreter);
                    return Data.createDataError(err);
                }
            }
            
            // dodajemy wszystkie callbacki do eventów z tymczasowej listy
            for (Pair<String, UserFunction> p : tmp)
            {
                UserFunction eventUf = p.getRight();
                String callbackName = p.getLeft();
                eventUf.addObserver(callbackName);
            }
            
            return Data.createDataNone();
        }
    }
    
    private class IsAttachedFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "IS_ATTACHED";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametry <id, id>
            Data eventData = params.get(0);
            Data callbackData = params.get(1);
            
            String eventName = (String)eventData.getValue();
            String callbackName = (String)callbackData.getValue();
            
            UserFunction eventUf = interpreter.getUserFunctions().get(eventName);
            boolean isAttached = eventUf.getObservers().contains(callbackName);
            
            return Data.createDataBool(isAttached);
        }
    }
    
    private class DetachFromEventFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "DETACH_FROM_EVENT";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            List<Pair<String, UserFunction>> tmp = new ArrayList<>();
            // parametry <id, id>+
            for (int i = 0; i < params.size(); i += 2)
            {
                Data eventData = params.get(i);
                Data callbackData = params.get(i + 1);
                
                String eventName = (String)eventData.getValue();
                String callbackName = (String)callbackData.getValue();
                
                UserFunction eventUf = interpreter.getUserFunctions().get(eventName);
                // jeżeli istnieje taki obserwator to dodajemy do tymczasowej listy
                // jest to robione po to, że w przypadku wystąpienia błędu żaden obserwator nie zostanie usunięty
                boolean error = true;
                if (eventUf.getObservers().contains(callbackName))
                {
                    // musimy też przeszukać tymczasową listę
                    boolean duplicate = false;
                    for (Pair<String, UserFunction> p : tmp)
                    {
                        if (p.getLeft().equals(callbackName) && 
                                p.getRight().getName().equals(eventUf.getName()))
                        {
                            duplicate = true;
                            break;
                        }
                    }
                    
                    if (!duplicate)
                    {
                        error = false;
                        tmp.add(new Pair<>(callbackName, eventUf));
                    }
                }
                // błąd
                if (error)
                {
                    String message = "callback " + callbackName + " is not attached to event " + eventName;
                    MyError err = new MyError(getName(), message, 
                            ErrorCodes.CALLBACK_NOT_ATTACHED.getCode(), null, eventData.getErrorInfo(), 
                            interpreter);
                    return Data.createDataError(err);
                }
            }
            
            // usuwamy wszystkie callbacki z eventów z tymczasowej listy
            for (Pair<String, UserFunction> p : tmp)
            {
                UserFunction eventUf = p.getRight();
                String callbackName = p.getLeft();
                eventUf.removeObserver(callbackName);
            }
            
            return Data.createDataNone();
        }
    }
}

