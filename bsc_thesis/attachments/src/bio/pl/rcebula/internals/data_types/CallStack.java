/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.CallFrame;

/**
 *
 * @author robert
 */
public class CallStack
{
    // pierwszy element jest elementem najpóźniej wywoływanym
    private final List<CallStackEntry> callStack = new ArrayList<>();
    
    public CallStack(Stack<CallFrame> frameStack)
    {
        int size = frameStack.size();
        
        for (int i = size - 1; i >= 0; --i)
        {
            CallFrame cf = frameStack.get(i);
            callStack.add(new CallStackEntry(cf.getErrorInfo(), cf.getUserFunction().getName()));
        }
    }
    
    @Override
    public String toString()
    {
        String str = "";
        
        for (CallStackEntry entry : callStack)
        {
            str += entry.getErrorInfo().toString() + ": " + entry.getFunName() + "\n";
        }
        
        // usuwamy ostatni znak nowej linii
        str = str.substring(0, str.length() - 1);
        
        return str;
    }
    
    public String toStringNthLastTrace(int n)
    {
        String str = "";
        
        CallStackEntry cse = callStack.get(n);
        if (cse == null)
        {
            return null;
        }
        else
        {
            str += cse.getErrorInfo().toString() + ": " + cse.getFunName();
        }
        
        return str;
    }
    
    public class CallStackEntry
    {
        private final ErrorInfo errorInfo;
        private final String funName;

        public CallStackEntry(ErrorInfo errorInfo, String funName)
        {
            this.errorInfo = errorInfo;
            this.funName = funName;
        }

        public ErrorInfo getErrorInfo()
        {
            return errorInfo;
        }

        public String getFunName()
        {
            return funName;
        }
    }
}
