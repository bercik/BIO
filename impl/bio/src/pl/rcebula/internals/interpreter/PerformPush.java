/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import pl.rcebula.code.ParamType;
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
    public PerformPush(Interpreter interpreter, Line line)
    {
        PushLine pushLine = (PushLine)line;
        Param param = pushLine.getParam();
        int lineNum = pushLine.getLine();
        int chNum = pushLine.getChNum();
        
        ParamType pt = param.getParamType();
        Data data;
        switch (pt)
        {
            case BOOL:
                data = Data.createDataBool((boolean)param.getValue());
                break;
            case FLOAT:
                data = Data.createDataFloat((float)param.getValue());
                break;
            case ID:
                data = Data.createDataId((String)param.getValue());
                break;
            case INT:
                data = Data.createDataInt((int)param.getValue());
                break;
            case NONE:
                data = Data.createDataNone();
                break;
            case STRING:
                data = Data.createDataString((String)param.getValue());
                break;
            case VAR:
                data = Data.createDataVar((String)param.getValue());
                break;
            default:
                throw new RuntimeException("Unknown type " + pt.toString());
        }
        
        data.setLineAndChNum(lineNum, chNum);
        interpreter.currentFrame.getVariableStack().push(data);
    }
}
