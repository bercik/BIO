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
import pl.rcebula.internals.ErrorCodes;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.data_types.MyError;

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
        
        // czyścimy stos parametrów
        interpreter.currentFrame.getStackParameters().clear();
        // tworzymy tymczasową tablicę na parametry
        Data[] tmpDatas = new Data[amount];
        while (amount-- > 0)
        {
            Data data = interpreter.currentFrame.getVariableStack().pop();
            // jeżeli typ to VAR to pobierz wartość
            if (data.getDataType().equals(DataType.VAR))
            {
                String id = (String)data.getValue();
                ErrorInfo ei = data.getErrorInfo();
                // pobierz zmienną lokalną
                data = interpreter.currentFrame.getLocalVariables().get(id);
                // jeżeli istnieje to utwórz nową zmienną i ustaw jej linię i znak
                if (data != null)
                {
                    data = new Data(data);
                    data.setErrorInfo(ei);
                }
                // jeżeli nie istnieje to utwórz zmienną błędu zamiast niej
                else
                {
                    String message = "There is no local variable " + id;
                    MyError myError = new MyError(message, ErrorCodes.NO_LOCAL_VARIABLE.getCode(),
                            null, ei, interpreter);
                    data = Data.createDataError(myError);
                }
            }
            
            // dodaj do stack_parameters w odwrotnej kolejności co ściągamy
            tmpDatas[amount] = data;
        }
        
        // dodaj do stack_parameters
        interpreter.currentFrame.getStackParameters().addAll(Arrays.asList(tmpDatas));
    }
}
