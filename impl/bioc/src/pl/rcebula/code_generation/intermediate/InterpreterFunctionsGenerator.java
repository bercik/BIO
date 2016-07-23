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

import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IdCallParamStringField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.LabelField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntStringField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.InterpreterFunctionStringField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.ConstCallParamStringField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import java.util.ArrayList;
import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.IdCallParam;

/**
 *
 * @author robert
 */
public class InterpreterFunctionsGenerator
{
    public Line generateCall(String functionName, ErrorInfo ei)
    {
        List<IField> fields = new ArrayList<>();

        // call, function_name, line, chNum, fnum
        fields.add(new InterpreterFunctionStringField(InterpreterFunction.CALL));
        fields.add(new StringField(functionName));
        fields.add(new IntStringField(ei.getLineNum()));
        fields.add(new IntStringField(ei.getChNum()));
        fields.add(new IntStringField(ei.getFile().getNum()));

        return new Line(fields);
    }

    public Line generateCallLoc(String functionName, ErrorInfo ei)
    {
        List<IField> fields = new ArrayList<>();

        // call_loc, function_name, line, chNum, fnum
        fields.add(new InterpreterFunctionStringField(InterpreterFunction.CALL_LOC));
        fields.add(new StringField(functionName));
        fields.add(new IntStringField(ei.getLineNum()));
        fields.add(new IntStringField(ei.getChNum()));
        fields.add(new IntStringField(ei.getFile().getNum()));

        return new Line(fields);
    }

    public Line generatePush(ConstCallParam ccp)
    {
        ConstCallParamStringField ccpsf = new ConstCallParamStringField(ccp);

        return generatePush(ccpsf, ccp.getErrorInfo());
    }

    public Line generatePush(IdCallParam icp, boolean isVar)
    {
        IdCallParamStringField icpsf = new IdCallParamStringField(icp, isVar);

        return generatePush(icpsf, icp.getErrorInfo());
    }

    private Line generatePush(StringField strField, ErrorInfo ei)
    {
        List<IField> fields = new ArrayList<>();

        // push, str, line, chNum, fnum
        fields.add(new InterpreterFunctionStringField(InterpreterFunction.PUSH));
        fields.add(strField);
        fields.add(new IntStringField(ei.getLineNum()));
        fields.add(new IntStringField(ei.getChNum()));
        fields.add(new IntStringField(ei.getFile().getNum()));

        return new Line(fields);
    }

    public Line generatePop(Integer number)
    {
        List<IField> fields = new ArrayList<>();

        // pop, number
        fields.add(new InterpreterFunctionStringField(InterpreterFunction.POP));
        fields.add(new IntStringField(number));

        return new Line(fields);
    }

    public Line generatePopc(Integer number)
    {
        List<IField> fields = new ArrayList<>();

        // pop, number
        fields.add(new InterpreterFunctionStringField(InterpreterFunction.POPC));
        fields.add(new IntStringField(number));

        return new Line(fields);
    }

    public Line generateJmp(Label label, ErrorInfo ei)
    {
        List<IField> fields = new ArrayList<>();

        // jmp, label, line, chNum, fnum
        fields.add(new InterpreterFunctionStringField(InterpreterFunction.JMP));
        fields.add(new LabelField(label));
        fields.add(new IntStringField(ei.getLineNum()));
        fields.add(new IntStringField(ei.getChNum()));
        fields.add(new IntStringField(ei.getFile().getNum()));

        return new Line(fields);
    }

    public Line generateJmpIfFalse(Label label, ErrorInfo ei)
    {
        List<IField> fields = new ArrayList<>();

        // jmp_if_false, label, line, chNum. fnum
        fields.add(new InterpreterFunctionStringField(InterpreterFunction.JMP_IF_FALSE));
        fields.add(new LabelField(label));
        fields.add(new IntStringField(ei.getLineNum()));
        fields.add(new IntStringField(ei.getChNum()));
        fields.add(new IntStringField(ei.getFile().getNum()));

        return new Line(fields);
    }

    public Line generateClearStack()
    {
        List<IField> fields = new ArrayList<>();

        // clear_stack
        fields.add(new InterpreterFunctionStringField(InterpreterFunction.CLEAR_STACK));

        return new Line(fields);
    }
}
