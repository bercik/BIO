/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.interpreter.Interpreter;
import pl.rcebula.module.utils.Collections;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;
import pl.rcebula.module.utils.type_checker.TypeChecker;
import pl.rcebula.module.utils.type_checker.TypeCheckerCollection;
import pl.rcebula.module.utils.type_checker.TypeCheckerNumberCollection;

/**
 *
 * @author beata
 */
public class CollectionsOperation
{
    public Data perform(String funName, OperationDataType dataType, IOperation op, 
            Interpreter interpreter, Data... collections)
    {
        return perform(funName, Arrays.asList(dataType), op, interpreter, collections);
    }
    
    public Data perform(String funName, List<OperationDataType> dataTypes, IOperation op, 
            Interpreter interpreter, List<Data> collections)
    {
        return perform(funName, dataTypes, op, interpreter, collections.toArray(new Data[0]));
    }
    
    // zakłada, że collections zawiera przynajmniej jeden element i pierwszy jest kolekcją
    // zwraca tuple z elementami, które są wynikami operacji op na elementach przekazanych kolekcji
    // dataTypes określa jakie typy danych są akceptowane (patrz OperationDataType po szczegóły)
    public Data perform(String funName, List<OperationDataType> dataTypes, IOperation op, 
            Interpreter interpreter, Data... collections)
    {
        List<Data[]> col = new ArrayList<>();
        // najpierw sprawdzamy czy wszystkie parametry to kolekcje i czy ich rozmiar jest taki sam
        col.add(Collections.getDatas(collections[0]));
        int size = col.get(0).length;
        
        for (int i = 1; i < collections.length; ++i)
        {
            Data d = collections[i];
            TypeChecker tc = new TypeChecker(d, funName, i, d.getErrorInfo(), interpreter, DataType.ARRAY, 
                    DataType.TUPLE);
            if (tc.isError())
            {
                return tc.getError();
            }
            
            col.add(Collections.getDatas(collections[i]));
            if (col.get(i).length != size)
            {
                return ErrorConstruct.COLLECTIONS_DIFFRENT_SIZES(funName, collections[i].getErrorInfo(), 
                        interpreter, i);
            }
        }
        
        // tablica z wynikami
        Data[] results = new Data[size];
        
        // teraz przechodzimy po wszystkich parametrach pod kolejnymi indeksami we wszystkich kolekcjach
        for (int i = 0; i < size; ++i)
        {
            // rezultat dla i-tego indeksu
            Data res;
            
            OperationDataType dataType = null;
            // pobierz i-ty parametr z pierwszej kolekcji
            Data firstPar = col.get(0)[i];
            // sprawdź czy jednego z typów
            Set<DataType> possibleDataTypes = new HashSet<>();
            for (OperationDataType dt : dataTypes)
            {
                switch (dt)
                {
                    case BOOL:
                        possibleDataTypes.add(DataType.BOOL);
                        break;
                    case FLOAT:
                        possibleDataTypes.add(DataType.FLOAT);
                        possibleDataTypes.add(DataType.INT);
                        break;
                    case INT:
                        possibleDataTypes.add(DataType.INT);
                        break;
                    case NUMBER:
                        possibleDataTypes.add(DataType.FLOAT);
                        possibleDataTypes.add(DataType.INT);
                        break;
                    case STRING:
                        possibleDataTypes.add(DataType.STRING);
                        break;
                    default:
                        throw new RuntimeException("Unknown operation data type " + dt.toString());
                }
            }
            TypeCheckerCollection tcc = new TypeCheckerCollection(firstPar, funName, 0, i, firstPar.getErrorInfo(), 
                    interpreter, possibleDataTypes.toArray(new DataType[0]));
            if (tcc.isError())
            {
                return tcc.getError();
            }
            
            forLabel:
            for (OperationDataType dt : dataTypes)
            {
                switch (dt)
                {
                    case BOOL:
                        if (firstPar.getDataType().equals(DataType.BOOL))
                        {
                            dataType = OperationDataType.BOOL;
                            break forLabel;
                        }
                        break;
                    case FLOAT:
                        if (firstPar.getDataType().equals(DataType.FLOAT)
                                || firstPar.getDataType().equals(DataType.INT))
                        {
                            dataType = OperationDataType.FLOAT;
                            break forLabel;
                        }
                        break;
                    case INT:
                        if (firstPar.getDataType().equals(DataType.INT))
                        {
                            dataType = OperationDataType.INT;
                            break forLabel;
                        }
                        break;
                    case STRING:
                        if (firstPar.getDataType().equals(DataType.STRING))
                        {
                            dataType = OperationDataType.STRING;
                            break forLabel;
                        }
                        break;
                    case NUMBER:
                        if (firstPar.getDataType().equals(DataType.FLOAT) ||
                                firstPar.getDataType().equals(DataType.INT))
                        {
                            dataType = OperationDataType.NUMBER;
                            break forLabel;
                        }
                        break;
                }
            }
            
            // w zależności od typu operacji wykonujemy różne akcje
            switch (dataType)
            {
                case BOOL:
                    // tablica parametrów
                    boolean[] bparams = new boolean[col.size()];
                    
                    // przechodzimy po wszystkich kolekcjach
                    for (int k = 0; k < col.size(); ++k)
                    {
                        // pobieramy parametr i-ty z k-tej kolekcji
                        Data d = col.get(k)[i];
                        
                        // sprawdzamy czy jest typu bool
                        tcc = new TypeCheckerCollection(d, funName, k, i, 
                                collections[k].getErrorInfo(), interpreter, DataType.BOOL);
                        if (tcc.isError())
                        {
                            return tcc.getError();
                        }
                        
                        // dodajemy do tablicy parametrów
                        bparams[k] = (boolean)col.get(k)[i].getValue();
                    }
                    
                    // wykonujemy działanie
                    res = op.perform(bparams);
                    break;
                    
                case INT:
                    // tablica parametrów
                    int[] iparams = new int[col.size()];
                    
                    // przechodzimy po wszystkich kolekcjach
                    for (int k = 0; k < col.size(); ++k)
                    {
                        // pobieramy parametr i-ty z k-tej kolekcji
                        Data d = col.get(k)[i];
                        
                        // sprawdzamy czy jest typu int
                        tcc = new TypeCheckerCollection(d, funName, k, i, 
                                collections[k].getErrorInfo(), interpreter, DataType.INT);
                        if (tcc.isError())
                        {
                            return tcc.getError();
                        }
                        
                        // dodajemy do tablicy parametrów
                        iparams[k] = (int)col.get(k)[i].getValue();
                    }
                    
                    // wykonujemy działanie
                    res = op.perform(iparams);
                    break;
                    
                case STRING:
                    // tablica parametrów
                    String[] sparams = new String[col.size()];
                    
                    // przechodzimy po wszystkich kolekcjach
                    for (int k = 0; k < col.size(); ++k)
                    {
                        // pobieramy parametr i-ty z k-tej kolekcji
                        Data d = col.get(k)[i];
                        
                        // sprawdzamy czy jest typu string
                        tcc = new TypeCheckerCollection(d, funName, k, i, 
                                collections[k].getErrorInfo(), interpreter, DataType.STRING);
                        if (tcc.isError())
                        {
                            return tcc.getError();
                        }
                        
                        // dodajemy do tablicy parametrów
                        sparams[k] = (String)col.get(k)[i].getValue();
                    }
                    
                    // wykonujemy działanie
                    res = op.perform(sparams);
                    break;
                    
                case FLOAT:
                    // w przypadku float konwertuj inty do float
                    // tablica parametrów
                    float[] fparams = new float[col.size()];
                    
                    // przechodzimy po wszystkich kolekcjach
                    for (int k = 0; k < col.size(); ++k)
                    {
                        // pobieramy parametr i-ty z k-tej kolekcji
                        Data d = col.get(k)[i];
                        
                        // sprawdzamy czy jest typu int lub float
                        TypeCheckerNumberCollection tcnc = new TypeCheckerNumberCollection(d, funName, k, i, 
                                collections[k].getErrorInfo(), interpreter);
                        if (tcnc.isError())
                        {
                            return tcnc.getError();
                        }
                        
                        // dodajemy do tablicy parametrów
                        if (tcnc.isFloat())
                        {
                            fparams[k] = (float)col.get(k)[i].getValue();
                        }
                        else
                        {
                            fparams[k] = ((Integer)col.get(k)[i].getValue()).floatValue();
                        }
                    }
                    
                    // wykonujemy działanie
                    res = op.perform(fparams);
                    break;
                    
                case NUMBER:
                    // w przypadku liczb utrzymuj int dopóki nie natrafisz na jakiegoś floata, 
                    // wtedy konwertuj wszystko do float
                    iparams = new int[col.size()];
                    fparams = new float[col.size()];
                    boolean isFloat = false;
                    
                    // przechodzimy po wszystkich kolekcjach
                    for (int k = 0; k < col.size(); ++k)
                    {
                        // pobieramy parametr i-ty z k-tej kolekcji
                        Data d = col.get(k)[i];
                        
                        // sprawdzamy czy jest typu int lub float
                        TypeCheckerNumberCollection tcnc = new TypeCheckerNumberCollection(d, funName, k, i, 
                                collections[k].getErrorInfo(), interpreter);
                        if (tcnc.isError())
                        {
                            return tcnc.getError();
                        }
                        
                        // jeżeli do tej pory były same inty
                        if (!isFloat)
                        {
                            // jeżeli zmiana na float
                            if (tcnc.isFloat())
                            {
                                isFloat = true;
                                // konwertujemy wszystkie dotychczasowe inty na float
                                for (int z = 0; z < k; z++)
                                {
                                    fparams[z] = iparams[z];
                                }
                                
                                // dodajemy floata
                                fparams[k] = (float)col.get(k)[i].getValue();
                            }
                            // inaczej dodajemy inta
                            else
                            {
                                iparams[k] = (int)col.get(k)[i].getValue();
                            }
                        }
                        // jeżeli już floaty
                        else
                        {
                            // dodajemy do tablicy parametrów
                            if (tcnc.isFloat())
                            {
                                fparams[k] = (float)col.get(k)[i].getValue();
                            }
                            else
                            {
                                fparams[k] = ((Integer)col.get(k)[i].getValue()).floatValue();
                            }
                        }
                    }
                    
                    // wykonujemy działanie
                    if (isFloat)
                    {
                        res = op.perform(fparams);
                    }
                    else
                    {
                        res = op.perform(iparams);
                    }
                    break;
                    
                default:
                    throw new RuntimeException("Unsupported data type " + dataType.toString());
            }
            
            results[i] = res;
        }
        
        // zwracamy array z wynikami
        return Data.createArrayData(results);
    }
}
