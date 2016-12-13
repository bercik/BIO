/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code;

import pl.rcebula.Constants;
import pl.rcebula.code.ParamType;
import pl.rcebula.utils.StringUtils;

/**
 *
 * @author robert
 */
public class Param
{
    private final ParamType paramType;
    private final Object value;

    public Param(ParamType paramType, Object value)
    {
        this.paramType = paramType;
        this.value = value;
    }

    public ParamType getParamType()
    {
        return paramType;
    }

    public Object getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        String result = paramType.toString() + Constants.valueSeparator;
        if (paramType.equals(ParamType.STRING))
        {
            String str = value.toString();
            str = StringUtils.addSlashToSpecialCharacters(str);
            result += "\"" + str + "\"";
        }
        else
        {
            result += (value != null ? value.toString() : "");
        }
        
        return result;
    }
}
