/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
            callStack.add(new CallStackEntry(cf.getLine(), cf.getChNum(), cf.getUserFunction().getName()));
        }
    }
    
    public class CallStackEntry
    {
        private final int line;
        private final int chNum;
        private final String funName;

        public CallStackEntry(int line, int chNum, String funName)
        {
            this.line = line;
            this.chNum = chNum;
            this.funName = funName;
        }

        public int getLine()
        {
            return line;
        }

        public int getChNum()
        {
            return chNum;
        }

        public String getFunName()
        {
            return funName;
        }
    }
}
