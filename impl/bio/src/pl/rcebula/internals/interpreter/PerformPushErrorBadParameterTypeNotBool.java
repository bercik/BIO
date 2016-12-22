/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.Arrays;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.intermediate_code.line.PushErrorBadParameterTypeLine;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.module.utils.error_codes.ErrorConstruct;

/**
 *
 * @author robert
 */
public class PerformPushErrorBadParameterTypeNotBool
{
    public void perform(Interpreter interpreter, Line line)
    {
        PushErrorBadParameterTypeLine pebptLine = (PushErrorBadParameterTypeLine)line;
        String funName = pebptLine.getFunName();
        int paramNum = pebptLine.getParamNum();
        
        // ściągamy parametr z parameter stack
        Data param = interpreter.currentFrame.getStackParameters().get(0);
        // czyścimi stack parameters
        interpreter.currentFrame.getStackParameters().clear();
        
        // tworzymy error
        Data error = ErrorConstruct.BAD_PARAMETER_TYPE(funName, param.getErrorInfo(), interpreter, 
                param, paramNum, Arrays.asList(DataType.BOOL));
        
        // odkładamy na stos wartości aktualnej ramki
        interpreter.currentFrame.getVariableStack().push(error);
    }
}
