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

import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.LabelField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntStringField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pl.rcebula.analysis.semantic.BuiltinFunction;
import pl.rcebula.analysis.semantic.ParamType;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.CallParam;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.IdCallParam;
import pl.rcebula.analysis.tree.Param;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.UserFunction;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class CodeGenerator
{
    private final IntermediateCode ic;

    private final ProgramTree pt;
    private final List<BuiltinFunction> builtinFunctions;

    private final InterpreterFunctionsGenerator ifg;
    private final SpecialFunctionsGenerator sfg;

    private boolean firstFunction = true;
    
    private final MyFiles files;

    public CodeGenerator(ProgramTree pt, List<BuiltinFunction> builtinFunctions, MyFiles files)
    {
        Logger logger = Logger.getGlobal();
        logger.info("CodeGenerator");
        
        ifg = new InterpreterFunctionsGenerator();
        ic = new IntermediateCode();
        this.pt = pt;
        this.builtinFunctions = builtinFunctions;
        this.files = files;

        this.sfg = new SpecialFunctionsGenerator(this);

        for (UserFunction uf : pt.getUserFunctions())
        {
            eval(uf);
        }

        // dodajemy pustą linię na koniec
        ic.appendLine(Line.generateEmptyByteLine());
    }

    private void eval(UserFunction uf)
    {
        // deklaracja funkcji
        // nazwa, linia występowania, linia w kodzie źródłowym, znak w kodzie źródłowym, numer pliku źródłowego
        List<IField> fields = new ArrayList<>();
        // nazwa funkcji
        fields.add(new StringField(uf.getName()));
        // etykieta występowania funkcji
        Label functionOccurenceLabel = new Label();
        fields.add(new LabelField(functionOccurenceLabel));
        // linia w kodzie źródłowym
        fields.add(new IntStringField(uf.getErrorInfo().getLineNum()));
        // znak w kodzie źródłowym
        fields.add(new IntStringField(uf.getErrorInfo().getChNum()));
        // plik w kodzie źródłowym
        fields.add(new IntStringField(uf.getErrorInfo().getFile().getNum()));
        // stwórz linię
        Line line = new Line(fields);
        // dodaj do kodu na początku
        ic.insertLine(line, 0);

        if (firstFunction)
        {
            firstFunction = false;
            // dodaj pustą linię na koniec
            ic.appendLine(Line.generateEmptyStringLine());
        }
        else
        {
            // dodaj pustą linię na koniec
            ic.appendLine(Line.generateEmptyByteLine());
        }

        // definicja funkcji
        // pierwsza linia: nazwa funkcji, parametr1, parametr2, ..., parametrN, pusty string
        fields = new ArrayList<>();
        fields.add(new StringField(uf.getName()));
        // nazwy parametrów
        for (Param p : uf.getParams())
        {
            fields.add(new StringField(p.getName()));
        }
        fields.add(new StringField(""));

        // tworzymy linię
        line = new Line(fields);
        // dodajemy etykietę występowania funkcji
        line.addLabel(functionOccurenceLabel);
        // dodajemy na końcu
        ic.appendLine(line);

        // generujemy kod dla wywołań funkcji
        for (int i = 0; i < uf.getCalls().size(); ++i)
        {
            Call c = uf.getCalls().get(i);
            // tworzymy kod rekurencyjnie
            eval(c, null, null);
            // na końcu dodajemy komendę clear stack o ile nie jest to koniec funkcji
            if (i < uf.getCalls().size() - 1)
            {
                ic.appendLine(ifg.generateClearStack());
            }
        }
    }

    public void eval(CallParam cp, Label forStart, Label forEnd)
    {
        if (cp instanceof Call)
        {
            eval((Call)cp, forStart, forEnd);
        }
        else if (cp instanceof IdCallParam)
        {
            eval((IdCallParam)cp, true);
        }
        else if (cp instanceof ConstCallParam)
        {
            eval((ConstCallParam)cp);
        }
        else
        {
            throw new RuntimeException("Unknown CallParam subclass");
        }
    }

    private UserFunction isUserFunction(Call c)
    {
        for (UserFunction uf : pt.getUserFunctions())
        {
            if (uf.getName().equals(c.getName()))
            {
                return uf;
            }
        }

        return null;
    }

    private BuiltinFunction isBuiltinFunction(Call c)
    {
        for (BuiltinFunction bf : builtinFunctions)
        {
            if (bf.getName().equals(c.getName()))
            {
                return bf;
            }
        }

        return null;
    }

    public void eval(Call call, Label forStart, Label forEnd)
    {
        BuiltinFunction bf = isBuiltinFunction(call);
        if (bf != null)
        {
            // funkcja wbudowana
            if (bf.isSpecial())
            {
                // funkcja specjalna
                switch (bf.getName())
                {
                    case SpecialFunctionsName.forLoopFunctionName:
                        Call call1 = (Call)call.getCallParams().get(0);
                        CallParam cp = call.getCallParams().get(1);
                        Call call2 = (Call)call.getCallParams().get(2);
                        Call call3 = (Call)call.getCallParams().get(3);
                        sfg.generateFor(call1, cp, call2, call3, call.getErrorInfo());
                        break;
                    case SpecialFunctionsName.ifFunctionName:
                        cp = call.getCallParams().get(0);
                        call1 = (Call)call.getCallParams().get(1);
                        // jeżeli podano opcjonalny argument
                        if (call.getRepeatCycles() == 1)
                        {
                            call2 = (Call)call.getCallParams().get(2);
                            sfg.generateIf(cp, call1, call2, forStart, forEnd, call.getErrorInfo());
                        }
                        else
                        {
                            sfg.generateIf(cp, call1, forStart, forEnd, call.getErrorInfo());
                        }
                        break;
                    case SpecialFunctionsName.callFunctionName:
                        call1 = (Call)call.getCallParams().get(0);
                        List<Call> calls = new ArrayList<>();
                        for (int i = 1; i < call.getCallParams().size(); ++i)
                        {
                            calls.add((Call)call.getCallParams().get(i));
                        }
                        sfg.generateCall(call1, calls, forStart, forEnd);
                        break;
                    case SpecialFunctionsName.doNothingFunctionName:
                        sfg.generateDN();
                        break;
                    case SpecialFunctionsName.breakFunctionName:
                        sfg.generateBreak(forEnd, call.getErrorInfo());
                        break;
                    case SpecialFunctionsName.continueFunctionName:
                        sfg.generateContinue(forStart, call.getErrorInfo());
                        break;
                    default:
                        String message = "There is no special function generator for " + bf.getName() + " function";
                        throw new RuntimeException(message);
                }
            }
            else
            {
                // funkcja regularna
                List<CallParam> callParams = call.getCallParams();
                // push, id:x
                // call2
                // pop, 2
                // call_loc, nazwa_funkcji
                // początkowe parametry
                int i;
                for (i = 0; i < bf.getStartParams(); ++i)
                {
                    ParamType pt = bf.getParams().get(i);
                    CallParam cp = callParams.get(i);
                    eval(pt, cp, forStart, forEnd);
                }
                // cykliczne parametry
                List<ParamType> repeatPatternTypes = bf.getRepeatPatternTypes();
                // tyle razy ile cykl występuje
                for (int j = 0; j < call.getRepeatCycles(); ++j)
                {
                    // wykonaj jeden cykl
                    for (int k = 0; k < repeatPatternTypes.size(); ++k)
                    {
                        ParamType pt = repeatPatternTypes.get(k);
                        CallParam cp = callParams.get(i);
                        eval(pt, cp, forStart, forEnd);
                        ++i;
                    }
                }
                // to co zostało
                int k = bf.getStartParams() + repeatPatternTypes.size();
                for (; i < callParams.size(); ++i)
                {
                    ParamType pt = bf.getParams().get(k);
                    CallParam cp = callParams.get(i);
                    eval(pt, cp, forStart, forEnd);

                    ++k;
                }

                Line line;

                if (callParams.size() != 0)
                {
                    line = ifg.generatePop(callParams.size());
                    ic.appendLine(line);
                }

                line = ifg.generateCallLoc(call.getName(), call.getErrorInfo());
                ic.appendLine(line);
            } // koniec funkcja regularna
        }
        else
        {
            UserFunction uf = isUserFunction(call);
            if (uf != null)
            {
                // funkcja użytkownika
                // call1
                // call2
                // ...
                // pop, 2
                // call, nazwa_funkcji
                for (CallParam cp : call.getCallParams())
                {
                    eval(cp, forStart, forEnd);
                }

                Line line;

                if (uf.getParams().size() != 0)
                {
                    line = ifg.generatePop(uf.getParams().size());
                    ic.appendLine(line);
                }

                line = ifg.generateCall(call.getName(), call.getErrorInfo());
                ic.appendLine(line);
            }
            else
            {
                throw new RuntimeException("Unknown function " + call.getName());
            }
        }
    }

    private void eval(ParamType pt, CallParam cp, Label forStart, Label forEnd)
    {
        if (pt.equals(ParamType.ID))
        {
            eval((IdCallParam)cp, false);
        }
        else
        {
            eval(cp, forStart, forEnd);
        }
    }

    private void eval(IdCallParam icp, boolean isVar)
    {
        Line line = ifg.generatePush(icp, isVar);
        ic.appendLine(line);
    }

    private void eval(ConstCallParam ccp)
    {
        Line line = ifg.generatePush(ccp);
        ic.appendLine(line);
    }

    public MyFiles getFiles()
    {
        return files;
    }
    
    public IntermediateCode getIc()
    {
        return ic;
    }
}
