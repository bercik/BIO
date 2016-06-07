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
import pl.rcebula.analysis.semantic.BuiltinFunction;
import pl.rcebula.analysis.semantic.ParamType;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.CallParam;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.IdCallParam;
import pl.rcebula.analysis.tree.Param;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.UserFunction;

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

    public CodeGenerator(ProgramTree pt, List<BuiltinFunction> builtinFunctions)
    {
        ifg = new InterpreterFunctionsGenerator();
        ic = new IntermediateCode();
        this.pt = pt;
        this.builtinFunctions = builtinFunctions;

        this.sfg = new SpecialFunctionsGenerator(this);

        for (UserFunction uf : pt.getUserFunctions())
        {
            eval(uf);
        }

        // dodajemy pustą linię na koniec
        ic.appendLine(new Line());
    }

    private void eval(UserFunction uf)
    {
        // deklaracja funkcji
        // nazwa, linia występowania, linia w kodzie źródłowym, znak w kodzie źródłowym
        List<IField> fields = new ArrayList<>();
        // nazwa funkcji
        fields.add(new StringField(uf.getName()));
        // etykieta występowania funkcji
        Label functionOccurenceLabel = new Label();
        fields.add(new LabelField(functionOccurenceLabel));
        // linia w kodzie źródłowym
        fields.add(new StringField(uf.getLine().toString()));
        // znak w kodzie źródłowym
        fields.add(new StringField(uf.getChNum().toString()));
        // stwórz linię
        Line line = new Line(fields);
        // dodaj do kodu na początku
        ic.insertLine(line, 0);

        // dodaj pustą linię na koniec
        ic.appendLine(new Line());

        // definicja funkcji
        // pierwsza linia: nazwa funkcji, parametr1, parametr2, ..., parametrN
        fields = new ArrayList<>();
        fields.add(new StringField(uf.getName()));
        // nazwy parametrów
        for (Param p : uf.getParams())
        {
            fields.add(new StringField(p.getName()));
        }

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
                        sfg.generateFor(call1, cp, call2, call3, call.getLine(), call.getChNum());
                        break;
                    case SpecialFunctionsName.ifFunctionName:
                        cp = call.getCallParams().get(0);
                        call1 = (Call)call.getCallParams().get(1);
                        call2 = (Call)call.getCallParams().get(2);
                        sfg.generateIf(cp, call1, call2, forStart, forEnd, call.getLine(), call.getChNum());
                        break;
                    case SpecialFunctionsName.call2FunctionName:
                        call1 = (Call)call.getCallParams().get(0);
                        call2 = (Call)call.getCallParams().get(1);
                        sfg.generateCall2(call1, call2, forStart, forEnd);
                        break;
                    case SpecialFunctionsName.doNothingFunctionName:
                        sfg.generateDN();
                        break;
                    case SpecialFunctionsName.breakFunctionName:
                        sfg.generateBreak(forEnd, call.getLine(), call.getChNum());
                        break;
                    case SpecialFunctionsName.continueFunctionName:
                        sfg.generateContinue(forStart, call.getLine(), call.getChNum());
                        break;
                    default:
                        String message = "There is no special function generator for " + bf.getName() + " function";
                        throw new RuntimeException(message);
                }
            }
            else
            {
                // funkcja regularna
                for (int i = 0; i < bf.getParams().size(); ++i)
                {
                    // push, id:x
                    // call2
                    // ...
                    // pop, 2
                    // call_loc, nazwa_funkcji

                    ParamType pt = bf.getParams().get(i);
                    CallParam cp = call.getCallParams().get(i);

                    if (pt.equals(ParamType.ID))
                    {
                        eval((IdCallParam)cp, false);
                    }
                    else
                    {
                        eval(cp, forStart, forEnd);
                    }
                }

                Line line;

                if (bf.getParams().size() != 0)
                {
                    line = ifg.generatePop(bf.getParams().size());
                    ic.appendLine(line);
                }

                line = ifg.generateCallLoc(call.getName(), call.getLine(), call.getChNum());
                ic.appendLine(line);
            }
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

                line = ifg.generateCall(call.getName(), call.getLine(), call.getChNum());
                ic.appendLine(line);
            }
            else
            {
                throw new RuntimeException("Unknown function " + call.getName());
            }
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

    public IntermediateCode getIc()
    {
        return ic;
    }
}
