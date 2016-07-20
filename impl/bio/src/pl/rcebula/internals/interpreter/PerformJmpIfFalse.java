/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import java.util.Arrays;
import java.util.List;
import pl.rcebula.Constants;
import pl.rcebula.intermediate_code.line.JmpLine;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;
import pl.rcebula.internals.type_check.TypeChecker;

/**
 *
 * @author robert
 */
public class PerformJmpIfFalse
{
    public PerformJmpIfFalse(Interpreter interpreter, Line line)
    {
        JmpLine jmpLine = (JmpLine)line;
        int lineNum = jmpLine.getLine();
        int chNum = jmpLine.getChNum();
        
        // pobieramy parametr z stack parameters
        Data cond = interpreter.currentFrame.getStackParameters().get(0);
        // sprawdzamy czy jest typu bool
        TypeChecker tc = new TypeChecker(cond, "JMP_IF_FALSE", 1, lineNum, chNum, interpreter, 
                DataType.BOOL);
        if (!tc.isError())
        {
            // sprawdzamy czy false, jeżeli tak to robimy skok
            boolean val = (boolean)cond.getValue();
            if (!val)
            {
                int dest = jmpLine.getDest();
                interpreter.currentFrame.setIp(dest);
            }
        }
        else
        {
            List<Data> params = Arrays.asList(new Data[] { tc.getError() });
            // jeżeli wystąpił błąd to wychodzimy z aktualnej funkcji przekazując błąd wyżej
            new PerformCallLoc(interpreter, Constants.returnFunctionName, params, lineNum, chNum);
        }
    }
}
