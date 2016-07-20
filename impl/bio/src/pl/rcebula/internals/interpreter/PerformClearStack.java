/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import pl.rcebula.intermediate_code.line.Line;

/**
 *
 * @author robert
 */
public class PerformClearStack
{
    public PerformClearStack(Interpreter interpreter, Line line)
    {
        interpreter.currentFrame.getVariableStack().clear();
    }
}
