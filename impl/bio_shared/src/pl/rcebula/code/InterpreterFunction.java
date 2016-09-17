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
    CALL((byte)1),
    CALL_LOC((byte)2),
    PUSH((byte)3),
    POP((byte)4),
    POPC((byte)5),
    JMP((byte)6),
    JMP_IF_FALSE((byte)7),
    CLEAR_STACK((byte)8),
    ORDER((byte)9);
    
    private final byte opcode;

    private InterpreterFunction(byte opcode)
    {
        this.opcode = opcode;
    }

    public byte getOpcode()
    {
        return opcode;
    }
    
    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
    
    public static InterpreterFunction getInterpreterFunction(byte opcode)
    {
        for (InterpreterFunction ifun : InterpreterFunction.values())
        {
            if (ifun.getOpcode() == opcode)
            {
                return ifun;
            }
        }
        
        throw new RuntimeException("No InterpreterFunction for " + opcode + " opcode");
    }
}
