/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.interpreter;

import pl.rcebula.intermediate_code.line.JmpLine;
import pl.rcebula.intermediate_code.line.Line;

/**
 *
 * @author robert
 */
public class PerformJmp
{
    public void perform(Interpreter interpreter, Line line)
    {
        JmpLine jmpLine = (JmpLine)line;
        // ustawiamy instruction pointer na dest
        int dest = jmpLine.getDest();
        interpreter.currentFrame.setIp(dest);
    }
}
