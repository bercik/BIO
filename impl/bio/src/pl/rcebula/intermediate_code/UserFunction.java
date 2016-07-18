/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import pl.rcebula.Constants;
import pl.rcebula.intermediate_code.line.Line;

/**
 *
 * @author robert
 */
public class UserFunction
{
    private final String name;
    private final List<String> params = new ArrayList<>();
    private final int line;
    private final int chNum;
    private final List<Line> lines = new ArrayList<Line>();
    
    private final HashSet<String> observers = new HashSet<>();

    public UserFunction(String name, int line, int chNum)
    {
        this.name = name;
        this.line = line;
        this.chNum = chNum;
    }
    
    public void addParams(List<String> params)
    {
        this.params.addAll(params);
    }
    
    public void addLine(Line line)
    {
        lines.add(line);
    }

    public List<String> getParams()
    {
        return params;
    }

    public String getName()
    {
        return name;
    }

    public int getLine()
    {
        return line;
    }

    public int getChNum()
    {
        return chNum;
    }

    public List<Line> getLines()
    {
        return lines;
    }

    public HashSet<String> getObservers()
    {
        return observers;
    }
    
    public String toStringWithLineNumbers()
    {
        String result = "";
        
        result += name + Constants.fieldsSeparator;
        for (String param : params)
        {
            result += param + Constants.fieldsSeparator;
        }
        result = result.substring(0, result.length() - Constants.fieldsSeparator.length());
        result += "\n";
        
        int lnr = 0;
        for (Line line : lines)
        {
            result += "[" + lnr + "] " + line.toString() + "\n";
            ++lnr;
        }
        
        return result;
    }
}
