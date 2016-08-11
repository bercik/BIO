/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.Arrays;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.intermediate_code.line.PopLine;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;

/**
 *
 * @author robert
 */
public class PerformPop
{
    public PerformPop(Interpreter interpreter, Line line)
    {
        PopLine popLine = (PopLine)line;
        int amount = popLine.getAmount();
        
        // tworzymy tymczasową tablicę na parametry
        Data[] tmpDatas = new Data[amount];
        while (amount-- > 0)
        {
            Data data = interpreter.currentFrame.getVariableStack().pop();
            // jeżeli typ to VAR to pobierz wartość
            if (data.getDataType().equals(DataType.VAR))
            {
                ErrorInfo ei = data.getErrorInfo();
                // wywołaj wbudowaną funkcję GET_LOCAL, która zwraca wartość zmiennej lokalnej
                // lub błąd jeżeli ta zmienna nie isntieje
                data = interpreter.getBuiltinFunctions().callFunction("GET_LOCAL", Arrays.asList(data), 
                        interpreter.getCurrentFrame(), interpreter, data.getErrorInfo());
                // przypisz error info
                data.setErrorInfo(ei);
            }
            
            // dodaj do stack_parameters w odwrotnej kolejności co ściągamy
            tmpDatas[amount] = data;
        }
        
        // dodaj do stack_parameters
        interpreter.currentFrame.getStackParameters().addAll(Arrays.asList(tmpDatas));
    }
}
