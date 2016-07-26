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
public enum IdValueType
{
    // UWAGA! Upewnij się, że wartości opcode są różne od wartości w ValueType
    ID((byte)1),
    VAR((byte)2);

    private final byte opcode;

    private IdValueType(byte opcode)
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
        return name().toLowerCase();
    }
}
