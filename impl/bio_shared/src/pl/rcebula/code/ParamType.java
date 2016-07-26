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
public enum ParamType
{
    // UWAGA, opcode muszą pokrywać się z tymi w IdValueType i ValueType
    ID(String.class, (byte)IdValueType.ID.getOpcode()),
    VAR(String.class, (byte)IdValueType.VAR.getOpcode()),
    INT(Integer.class, (byte)ValueType.INT.getOpcode()),
    FLOAT(Float.class, (byte)ValueType.FLOAT.getOpcode()),
    STRING(String.class, (byte)ValueType.STRING.getOpcode()),
    BOOL(Boolean.class, (byte)ValueType.BOOL.getOpcode()),
    NONE(null, (byte)ValueType.NONE.getOpcode());
    
    private final Class type;
    private final byte opcode;

    private ParamType(Class type, byte opcode)
    {
        this.type = type;
        this.opcode = opcode;
    }

    public byte getOpcode()
    {
        return opcode;
    }

    public Class getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return this.name().toLowerCase().replace("_", " ");
    }
    
    public static ParamType getParamType(byte opcode)
    {
        for (ParamType pt : ParamType.values())
        {
            if (pt.getOpcode() == opcode)
            {
                return pt;
            }
        }
        
        throw new RuntimeException("No ParamType for " + opcode + " opcode");
    }
}
