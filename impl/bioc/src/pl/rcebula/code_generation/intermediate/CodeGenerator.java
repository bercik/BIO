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

    public CodeGenerator(ProgramTree pt, List<BuiltinFunction> builtinFunctions)
    {
        ifg = new InterpreterFunctionsGenerator();
        ic = new IntermediateCode();
        this.pt = pt;
        this.builtinFunctions = builtinFunctions;
        
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
        for (Call c : uf.getCalls())
        {
            // tworzymy kod rekurencyjnie
            eval(c, null, null);
            // na końcu dodajemy komendę clear stack
            ic.appendLine(ifg.generateClearStack());
        }
    }
    
    private void eval(CallParam cp, Label forStart, Label forEnd)
    {
        if (cp instanceof Call)
        {
            eval((Call)cp, forStart, forEnd);
        }
        else if (cp instanceof IdCallParam)
        {
            eval((IdCallParam)cp);
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
    
    private void eval(Call call, Label forStart, Label forEnd)
    {
        BuiltinFunction bf = isBuiltinFunction(call);
        if (bf != null)
        {
            // funkcja wbudowana
            if (bf.isSpecial())
            {
                // funkcja specjalna
            }
            else
            {
                // funkcja regularna
            }
        }
        else
        {
            UserFunction uf = isUserFunction(call);
            if (uf != null)
            {
                // funkcja użytkownika
            }
            else
            {
                throw new RuntimeException("Unknown function " + call.getName());
            }
        }
    }
    
    private void eval(IdCallParam icp, boolean isVar)
    {
        Line line = ifg.generatePush(icp, isVar, icp.getLine(), icp.getChNum());
        ic.appendLine(line);
    }
    
    private void eval(ConstCallParam ccp)
    {
        Line line = ifg.generatePush(ccp, ccp.getLine(), ccp.getChNum());
        ic.appendLine(line);
    }

    public IntermediateCode getIc()
    {
        return ic;
    }
}
