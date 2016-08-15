/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils;

import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.data_types.DataType;

/**
 *
 * @author robert
 */
public class Numbers
{
    public static boolean isNumber(Data data)
    {
        return data.getDataType().equals(DataType.INT) 
                || data.getDataType().equals(DataType.FLOAT) ;
    }
    
    public static float getFloat(Data number)
    {
        if (number.getDataType().equals(DataType.FLOAT))
        {
            return (float)number.getValue();
        }
        else if (number.getDataType().equals(DataType.INT))
        {
            return ((Integer)number.getValue()).floatValue();
        }
        else
        {
            String message = "Passed parameter is not number";
            throw new RuntimeException(message);
        }
    }
    
    public static int getInt(Data number)
    {
        return (int)number.getValue();
    }
    
    public enum NumberType
    {
        INT,
        FLOAT;
    }
    
    public static class Number implements Comparable<Number>
    {
        private final NumberType type;
        private final int i;
        private final float f;

        public Number(int i)
        {
            this.f = i;
            this.i = i;
            this.type = NumberType.INT;
        }

        public Number(float f)
        {
            this.i = (int)f;
            this.f = f;
            this.type = NumberType.FLOAT;
        }

        public NumberType getType()
        {
            return type;
        }

        public int getInt()
        {
            return i;
        }

        public float getFloat()
        {
            return f;
        }

        @Override
        public int compareTo(Number o)
        {
            if (this.f == o.f)
            {
                return 0;
            }
            else if (this.f < o.f)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    }
}
