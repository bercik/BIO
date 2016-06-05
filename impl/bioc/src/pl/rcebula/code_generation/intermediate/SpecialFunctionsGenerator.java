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

import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.CallParam;
import pl.rcebula.analysis.tree.ConstCallParam;

/**
 *
 * @author robert
 */
public class SpecialFunctionsGenerator
{
    private final CodeGenerator cg;
    private final IntermediateCode ic;
    private final InterpreterFunctionsGenerator ifg;

    public SpecialFunctionsGenerator(CodeGenerator cg)
    {
        this.cg = cg;
        this.ic = cg.getIc();
        this.ifg = new InterpreterFunctionsGenerator();
    }
    
    public void generateFor(Call call1, CallParam callParam, Call call2)
    {
        Label forStart = new Label();
        Label forEnd = new Label();
        
        // call1
        // popc, 1
        // forStart:
        // callParam
        // pop, 1
        // jmp_if_false, forEnd
        // call2
        // popc, 1
        // jmp, forStart
        // forEnd:
        // push, none:
        cg.eval(call1, forStart, forEnd);
        Line line = ifg.generatePopc(1);
        ic.appendLine(line);
        
        // dodajemy fałszywą linię, która posłuży nam jedynie do dodania etykiety
        // potem ją usuniemy, a nasza etykieta "wskoczy" w odpowiednie miejsce
        Line fakeLine = new Line();
        Integer fakeLineLine = ic.numberOfLines();
        fakeLine.addLabel(forStart);
        ic.appendLine(fakeLine);
        
        cg.eval(callParam, forStart, forEnd);
        line = ifg.generatePop(1);
        ic.appendLine(line);
        
        // usuwamy wcześniej dodaną fałszywą linię
        ic.removeLine(fakeLineLine);
        
        line = ifg.generateJmpIfFalse(forEnd);
        ic.appendLine(line);
        
        cg.eval(call2, forStart, forEnd);
        line = ifg.generatePopc(1);
        ic.appendLine(line);
        
        line = ifg.generateJmp(forStart);
        ic.appendLine(line);
        
        line = ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, -1, -1), -1, -1));
        line.addLabel(forEnd);
        ic.appendLine(line);
    }
    
    public void generateIf(CallParam callParam, Call call1, Call call2, Label forStart, Label forEnd)
    {
        // callParam
        // pop, 1
        // jmp_if_false, etykieta1
        // call1
        // popc, 1
        // jmp, etykieta2
        // etykieta1:
        // call2
        // popc, 1
        // etykieta2:
        // push, none:
        Label l1 = new Label();
        Label l2 = new Label();
        
        cg.eval(callParam, forStart, forEnd);
        Line line = ifg.generatePop(1);
        ic.appendLine(line);
        
        line = ifg.generateJmpIfFalse(l1);
        ic.appendLine(line);
        
        cg.eval(call1, forStart, forEnd);
        line = ifg.generatePopc(1);
        ic.appendLine(line);
        
        line = ifg.generateJmp(l2);
        ic.appendLine(line);
        
        // dodajemy fałszywą linię, która posłuży nam jedynie do dodania etykiety
        // potem ją usuniemy, a nasza etykieta "wskoczy" w odpowiednie miejsce
        Line fakeLine = new Line();
        Integer fakeLineLine = ic.numberOfLines();
        fakeLine.addLabel(l1);
        ic.appendLine(fakeLine);
        
        cg.eval(call2, forStart, forEnd);
        
        // usuwamy wcześniej dodaną fałszywą linię
        ic.removeLine(fakeLineLine);
        
        line = ifg.generatePopc(1);
        ic.appendLine(line);
        
        line = ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, -1, -1), -1, -1));
        line.addLabel(l2);
        ic.appendLine(line);
    }
    
    public void generateCall2(Call call1, Call call2, Label forStart, Label forEnd)
    {
        // call1
        // popc, 1
        // call2
        cg.eval(call1, forStart, forEnd);
        Line line = ifg.generatePopc(1);
        ic.appendLine(line);
        cg.eval(call2, forStart, forEnd);
    }
    
    public void generateDN()
    {
        // push, none:
        Line line = ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, -1, -1), -1, -1));
        ic.appendLine(line);
    }
    
    public void generateBreak(Label forEnd)
    {
        // jmp, forEnd
        Line line = ifg.generateJmp(forEnd);
        ic.appendLine(line);
    }
    
    public void generateContinue(Label forStart)
    {
        // jmp, forStart
        Line line = ifg.generateJmp(forStart);
        ic.appendLine(line);
    }
}
