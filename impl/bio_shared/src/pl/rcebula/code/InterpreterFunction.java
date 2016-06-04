/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.code;

/**
 *
 * @author robert
 */
public enum InterpreterFunction
{
    CALL,
    CALL_LOC,
    PUSH,
    POP,
    POPC,
    JMP,
    JMP_IF_FALSE,
    CLEAR_STACK;

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
