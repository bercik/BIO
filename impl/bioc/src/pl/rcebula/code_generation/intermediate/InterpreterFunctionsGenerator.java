/*
 * Copyright (C) 2016 robert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.rcebula.code_generation.intermediate;

import java.util.ArrayList;
import java.util.List;
import pl.rcebula.Constants;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.IdCallParam;
import pl.rcebula.code.IdValueType;

/**
 *
 * @author robert
 */
public class InterpreterFunctionsGenerator
{
    public Line generateCall(String functionName, Integer line, Integer chNum)
    {
        List<IField> fields = new ArrayList<>();

        // call, function_name, line, chNum
        fields.add(new StringField(InterpreterFunction.CALL.toString()));
        fields.add(new StringField(functionName));
        fields.add(new StringField(line.toString()));
        fields.add(new StringField(chNum.toString()));

        return new Line(fields);
    }

    public Line generateCallLoc(String functionName, Integer line, Integer chNum)
    {
        List<IField> fields = new ArrayList<>();

        // call_loc, function_name, line, chNum
        fields.add(new StringField(InterpreterFunction.CALL_LOC.toString()));
        fields.add(new StringField(functionName));
        fields.add(new StringField(line.toString()));
        fields.add(new StringField(chNum.toString()));

        return new Line(fields);
    }

    public Line generatePush(ConstCallParam ccp)
    {
        String str = "";

        str += ccp.getValueType().toString();
        str += Constants.valueSeparator;

        Object val = ccp.getValue();
        if (val != null)
        {
            str += val.toString();
        }

        return generatePush(str, ccp.getLine(), ccp.getChNum());
    }

    public Line generatePush(IdCallParam icp, boolean isVar)
    {
        // id/var:value
        String str = "";
        if (isVar)
        {
            str += IdValueType.VAR.toString();
        }
        else
        {
            str += IdValueType.ID.toString();
        }

        str += Constants.valueSeparator;
        str += icp.getName();

        return generatePush(str, icp.getLine(), icp.getChNum());
    }

    private Line generatePush(String str, Integer line, Integer chNum)
    {
        List<IField> fields = new ArrayList<>();

        // push, str, line, chNum
        fields.add(new StringField(InterpreterFunction.PUSH.toString()));
        fields.add(new StringField(str));
        fields.add(new StringField(line.toString()));
        fields.add(new StringField(chNum.toString()));

        return new Line(fields);
    }

    public Line generatePop(Integer number)
    {
        List<IField> fields = new ArrayList<>();

        // pop, number
        fields.add(new StringField(InterpreterFunction.POP.toString()));
        fields.add(new StringField(number.toString()));

        return new Line(fields);
    }
    
    public Line generatePopc(Integer number)
    {
        List<IField> fields = new ArrayList<>();

        // pop, number
        fields.add(new StringField(InterpreterFunction.POPC.toString()));
        fields.add(new StringField(number.toString()));

        return new Line(fields);
    }

    public Line generateJmp(Label label)
    {
        List<IField> fields = new ArrayList<>();
        
        // jmp, label
        fields.add(new StringField(InterpreterFunction.JMP.toString()));
        fields.add(new LabelField(label));
        
        return new Line(fields);
    }
    
    public Line generateJmpIfFalse(Label label)
    {
        List<IField> fields = new ArrayList<>();
        
        // jmp_if_false, label
        fields.add(new StringField(InterpreterFunction.JMP_IF_FALSE.toString()));
        fields.add(new LabelField(label));
        
        return new Line(fields);
    }
    
    public Line generateClearStack()
    {
        List<IField> fields = new ArrayList<>();
        
        // clear_stack
        fields.add(new StringField(InterpreterFunction.CLEAR_STACK.toString()));
        
        return new Line(fields);
    }
}
