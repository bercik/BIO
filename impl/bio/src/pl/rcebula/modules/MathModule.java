/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.CallFrame;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.modules.utils.Collections;
import pl.rcebula.modules.utils.type_checker.TypeChecker;
import pl.rcebula.modules.utils.type_checker.TypeCheckerCollection;
import pl.rcebula.modules.utils.type_checker.TypeCheckerNumber;
import pl.rcebula.modules.utils.type_checker.TypeCheckerNumberCollection;

/**
 *
 * @author robert
 */
public class MathModule extends Module
{
    @Override
    public String getName()
    {
        return "math";
    }

    @Override
    public void createFunctionsAndEvents()
    {
        putFunction(new SumFunction());
        putFunction(new AddFunction());
    }

    private class SumFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "SUM";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // parametr: <all>
            // sprawdzamy czy kolekcja (array lub tuple)
            Data col = params.get(0);
            ErrorInfo ei = col.getErrorInfo();
            TypeChecker tc = new TypeChecker(
                    col, getName(), 0, ei, interpreter, DataType.ARRAY, DataType.TUPLE);
            
            if (tc.isError())
            {
                return tc.getError();
            }
            
            // pobierz tablicę z kolekcji
            Data[] array = Collections.getDatas(col);
            // sprawdź czy rozmiar większy od zera, jeżeli nie zwróć none
            if (array.length == 0)
            {
                return Data.createNoneData();
            }
            
            // pobierz pierwszy element i sprawdź czy jego typ jest string, int lub float
            Data firstElement = array[0];
            TypeCheckerCollection tcc = new TypeCheckerCollection(firstElement, getName(), 0, 0, ei, interpreter, 
                DataType.STRING, DataType.INT, DataType.FLOAT);
            
            if (tcc.isError())
            {
                return tcc.getError();
            }
            
            // jeżeli element to string to konkatenacja, inaczej dodawanie
            if (firstElement.getDataType().equals(DataType.STRING))
            {
                String str = (String)firstElement.getValue();
                for (int i = 1; i < array.length; ++i)
                {
                    // pobierz następny element
                    Data d = array[i];
                    // sprawdź czy typu string
                    tcc = new TypeCheckerCollection(d, getName(), 0, i, ei, interpreter, DataType.STRING);
                    if (tcc.isError())
                    {
                        return tcc.getError();
                    }
                    // dołączamy stringa
                    str += (String)d.getValue();
                }
                
                // zwracamy stringa
                return Data.createStringData(str);
            }
            // dodawanie liczb
            else
            {
                boolean isFloat = false;
                int isum = 0;
                float fsum = 0.0f;
                
                for (int i = 0; i < array.length; ++i)
                {
                    // pobierz element
                    Data d = array[i];
                    // sprawdź czy typu liczbowego
                    TypeCheckerNumberCollection tcnc = new TypeCheckerNumberCollection(d, getName(), 0, i, ei, 
                            interpreter);
                    if (tcnc.isError())
                    {
                        return tcnc.getError();
                    }
                    // jeżeli dotychczas było int to sprawdzamy czy się nie zmieniło
                    if (!isFloat)
                    {
                        isFloat = tcnc.isFloat();
                        if (isFloat)
                        {
                            // konwertujemy dotychczasową sumę z int na float
                            fsum = isum;
                        }
                    }
                    // dodajemy liczbę do sumy
                    if (!isFloat)
                    {
                        int val = (int)d.getValue();
                        isum += val;
                    }
                    else
                    {
                        float val;
                        if (d.getDataType().equals(DataType.INT))
                        {
                            val = ((Integer)d.getValue()).floatValue();
                        }
                        else
                        {
                            val = (float)d.getValue();
                        }
                        fsum += val;
                    }
                }
                
                // zwracamy float lub int
                if (isFloat)
                {
                    return Data.createFloatData(fsum);
                }
                else
                {
                    return Data.createIntData(isum);
                }
            }
        }
    }

    private class AddFunction implements IFunction
    {
        @Override
        public String getName()
        {
            return "ADD";
        }

        @Override
        public Data call(List<Data> params, CallFrame currentFrame, Interpreter interpreter)
        {
            // TODO
            return Data.createNoneData();
        }
    }
}
