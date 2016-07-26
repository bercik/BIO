/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.intermediate_code.line.PopLine;

/**
 *
 * @author robert
 */
public class PerformPopc
{
    public PerformPopc(Interpreter interpreter, Line line)
    {
        PopLine popLine = (PopLine)line;
        int amount = popLine.getAmount();
        
        // ściągamy ze stosu amount obiektów
        while (amount-- > 0)
        {
            interpreter.currentFrame.getVariableStack().pop();
        }
    }
}
