/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import pl.rcebula.code.ParamType;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.Param;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.intermediate_code.line.PushLine;
import pl.rcebula.internals.data_types.Data;

/**
 *
 * @author robert
 */
public class PerformPush
{
    public void perform(Interpreter interpreter, Line line)
    {
        PushLine pushLine = (PushLine)line;
        Param param = pushLine.getParam();
        ErrorInfo ei = pushLine.getErrorInfo();
        
        ParamType pt = param.getParamType();
        Data data;
        switch (pt)
        {
            case BOOL:
                data = Data.createBoolData((boolean)param.getValue());
                break;
            case FLOAT:
                data = Data.createFloatData((float)param.getValue());
                break;
            case ID:
                data = Data.createIdData((String)param.getValue());
                break;
            case INT:
                data = Data.createIntData((int)param.getValue());
                break;
            case NONE:
                data = Data.createNoneData();
                break;
            case STRING:
                data = Data.createStringData((String)param.getValue());
                break;
            case VAR:
                data = Data.createVarData((String)param.getValue());
                break;
            default:
                throw new RuntimeException("Unknown type " + pt.toString());
        }
        
        data.setErrorInfo(ei);
        interpreter.currentFrame.getVariableStack().push(data);
    }
}
