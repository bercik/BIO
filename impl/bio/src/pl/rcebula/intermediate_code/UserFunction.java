/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import pl.rcebula.Constants;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.intermediate_code.line.Line;

/**
 *
 * @author robert
 */
public class UserFunction
{
    private final String name;
    private final List<String> params = new ArrayList<>();
    private final ErrorInfo errorInfo;
    private final List<Line> lines = new ArrayList<>();
    
    private final LinkedHashSet<String> observers = new LinkedHashSet<>();

    public UserFunction(String name, ErrorInfo errorInfo)
    {
        this.name = name;
        this.errorInfo = errorInfo;
    }
    
    public void addParams(List<String> params)
    {
        this.params.addAll(params);
    }
    
    public void addLine(Line line)
    {
        lines.add(line);
    }

    public ErrorInfo getErrorInfo()
    {
        return errorInfo;
    }
    
    public List<String> getParams()
    {
        return params;
    }

    public String getName()
    {
        return name;
    }

    public List<Line> getLines()
    {
        return lines;
    }

    public LinkedHashSet<String> getObservers()
    {
        return observers;
    }
    
    @Override
    public String toString()
    {
        return toStringWithLineNumbers();
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
