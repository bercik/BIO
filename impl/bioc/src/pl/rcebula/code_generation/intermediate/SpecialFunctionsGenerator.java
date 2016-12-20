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

import java.util.List;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Label;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.CallParam;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class SpecialFunctionsGenerator
{
    private final CodeGenerator cg;
    private final IntermediateCode ic;
    private final InterpreterFunctionsGenerator ifg;

    private final MyFiles.File mockFile;

    public SpecialFunctionsGenerator(CodeGenerator cg)
    {
        this.cg = cg;
        this.ic = cg.getIc();
        this.ifg = new InterpreterFunctionsGenerator();

        this.mockFile = cg.getFiles().getFileGeneratedByCompiler();
    }

    // wersja z 3 parametrami
    public void generateFor(Call call1, CallParam callParam, Call call2, ErrorInfo ei)
    {
        Label forStart = new Label();
        Label forContinue = new Label();
        Label forEnd = new Label();
        Label error = new Label();
        Label outside = new Label();

        // call1
        // popc, 1
        // forStart:
        // forContinue:
        // callParam
        // peek_jmp_if_not_bool, error
        // pop, 1
        // jmp_if_false, forEnd
        // call2
        // popc, 1
        // jmp, forStart
        // forEnd:
        // push, none:
        // jmp, outside
        // error:
        // push_error_bad_parameter_type_not_bool, 1
        // outside:
        // call1
        cg.eval(call1, forContinue, forEnd);
        // popc, 1
        Line line = ifg.generatePopc(1);
        ic.appendLine(line);

        // dodajemy fałszywą linię, która posłuży nam jedynie do dodania etykiety
        // potem ją usuniemy, a nasza etykieta "wskoczy" w odpowiednie miejsce
        Line fakeLine = new Line();
        Integer fakeLineLine = ic.numberOfLines();
        fakeLine.addLabel(forStart);
        fakeLine.addLabel(forContinue);
        ic.appendLine(fakeLine);

        // callParam
        cg.eval(callParam, forContinue, forEnd);

        // usuwamy wcześniej dodaną fałszywą linię
        ic.removeLine(fakeLineLine);

        // peek_jmp_if_not_bool, error
        line = ifg.generatePeekJmpIfNotBool(error, ei);
        ic.appendLine(line);

        // pop, 1
        line = ifg.generatePop(1);
        ic.appendLine(line);

        // jmp_if_false, forEnd
        line = ifg.generateJmpIfFalse(forEnd, ei);
        ic.appendLine(line);

        // call2
        cg.eval(call2, forContinue, forEnd);
        // popc, 1
        line = ifg.generatePopc(1);
        ic.appendLine(line);

        // jmp, forStart
        line = ifg.generateJmp(forStart, ei);
        ic.appendLine(line);

        // push, none:
        ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, mockFile);
        line = ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, mockErrorInfo),
                mockErrorInfo));
        line.addLabel(forEnd);
        ic.appendLine(line);

        // jmp, outside
        line = ifg.generateJmp(outside, ei);
        ic.appendLine(line);

        // dodajemy instrukcję nop, która posłuży nam jedynie do dodania etykiety
        // potem zostanie ona usunięta, a nasza etykieta "wskoczy" w odpowiednie miejsce
        line = ifg.generateNop();
        line.addLabel(error);
        ic.appendLine(line);

        // push_error_bad_parameter_type_not_bool (1 ponieważ warunek stoi na drugim miejscu, a numerujemy od zera)
        line = ifg.generatePushErrorBadParameterTypeNotBool("FOR", 1);
        ic.appendLine(line);

        // dodajemy instrukcję nop, która posłuży nam jedynie do dodania etykiety
        // potem zostanie ona usunięta, a nasza etykieta "wskoczy" w odpowiednie miejsce
        line = ifg.generateNop();
        line.addLabel(outside);
        ic.appendLine(line);
    }

    // wersja z 4 parametrami
    public void generateFor(Call call1, CallParam callParam, Call call2, Call call3, ErrorInfo ei)
    {
        Label forStart = new Label();
        Label forContinue = new Label();
        Label forEnd = new Label();
        Label error = new Label();
        Label outside = new Label();

        // call1
        // popc, 1
        // forStart:
        // callParam
        // peek_jmp_if_not_bool, error
        // pop, 1
        // jmp_if_false, forEnd
        // call2
        // popc, 1
        // forContinue:
        // call3
        // popc, 1
        // jmp, forStart
        // error:
        // push_error_bad_parameter_type_not_bool
        // jmp, outside
        // forEnd:
        // push, none:
        // outside:
        // call1
        // popc, 1
        cg.eval(call1, forContinue, forEnd);
        Line line = ifg.generatePopc(1);
        ic.appendLine(line);

        // dodajemy fałszywą linię, która posłuży nam jedynie do dodania etykiety
        // potem ją usuniemy, a nasza etykieta "wskoczy" w odpowiednie miejsce
        Line fakeLine = new Line();
        Integer fakeLineLine = ic.numberOfLines();
        fakeLine.addLabel(forStart);
        ic.appendLine(fakeLine);

        // callParam
        cg.eval(callParam, forContinue, forEnd);

        // usuwamy wcześniej dodaną fałszywą linię
        ic.removeLine(fakeLineLine);

        // peek_jmp_if_not_bool, error
        line = ifg.generatePeekJmpIfNotBool(error, ei);
        ic.appendLine(line);
        // pop, 1
        line = ifg.generatePop(1);
        ic.appendLine(line);

        // jmp_if_false, forEnd
        line = ifg.generateJmpIfFalse(forEnd, ei);
        ic.appendLine(line);

        // call2
        // popc, 1
        cg.eval(call2, forContinue, forEnd);
        line = ifg.generatePopc(1);
        ic.appendLine(line);

        // dodajemy fałszywą linię, która posłuży nam jedynie do dodania etykiety
        // potem ją usuniemy, a nasza etykieta "wskoczy" w odpowiednie miejsce
        fakeLine = new Line();
        fakeLineLine = ic.numberOfLines();
        fakeLine.addLabel(forContinue);
        ic.appendLine(fakeLine);

        // call3
        cg.eval(call3, forContinue, forEnd);

        // usuwamy wcześniej dodaną fałszywą linię
        ic.removeLine(fakeLineLine);

        // popc, 1
        line = ifg.generatePopc(1);
        ic.appendLine(line);

        // jmp, forStart
        line = ifg.generateJmp(forStart, ei);
        ic.appendLine(line);

        // dodajemy instrukcję nop, która posłuży nam jedynie do dodania etykiety
        // potem zostanie ona usunięta, a nasza etykieta "wskoczy" w odpowiednie miejsce
        line = ifg.generateNop();
        line.addLabel(error);
        ic.appendLine(line);

        // push_error_bad_parameter_type_not_bool (1 ponieważ warunek stoi na drugim miejscu, a numerujemy od zera)
        line = ifg.generatePushErrorBadParameterTypeNotBool("FOR", 1);
        ic.appendLine(line);

        // jmp, outside
        line = ifg.generateJmp(outside, ei);
        ic.appendLine(line);

        // push, none:
        ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, mockFile);
        line = ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, mockErrorInfo),
                mockErrorInfo));
        line.addLabel(forEnd);
        ic.appendLine(line);

        // dodajemy instrukcję nop, która posłuży nam jedynie do dodania etykiety
        // potem zostanie ona usunięta, a nasza etykieta "wskoczy" w odpowiednie miejsce
        line = ifg.generateNop();
        line.addLabel(outside);
        ic.appendLine(line);
    }

    // wersja z jedną funkcją
    public void generateIf(CallParam callParam, Call call1, Label forStart, Label forEnd, ErrorInfo ei)
    {
        // callParam
        // peek_jmp_if_not_bool, error
        // pop, 1
        // jmp_if_false, etykieta1
        // call1
        // popc, 1
        // etykieta1:
        // push, none:
        // jmp, outside
        // error:
        // push_error_bad_parameter_type_not_bool, 0
        // outside:
        Label l1 = new Label();
        Label error = new Label();
        Label outside = new Label();

        // callParam
        cg.eval(callParam, forStart, forEnd);

        // peek_jmp_if_not_bool, error
        Line line = ifg.generatePeekJmpIfNotBool(error, ei);
        ic.appendLine(line);

        // pop, 1
        line = ifg.generatePop(1);
        ic.appendLine(line);

        // jmp_if_false, etykieta1
        line = ifg.generateJmpIfFalse(l1, ei);
        ic.appendLine(line);

        // call1
        cg.eval(call1, forStart, forEnd);
        
        // popc, 1
        line = ifg.generatePopc(1);
        ic.appendLine(line);

        // push, none:
        ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, mockFile);
        line = ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, mockErrorInfo),
                mockErrorInfo));
        line.addLabel(l1);
        ic.appendLine(line);
        
        // jmp, outside
        line = ifg.generateJmp(outside, ei);
        ic.appendLine(line);
      
        // dodajemy instrukcję nop, która posłuży nam jedynie do dodania etykiety
        // potem zostanie ona usunięta, a nasza etykieta "wskoczy" w odpowiednie miejsce
        line = ifg.generateNop();
        line.addLabel(error);
        ic.appendLine(line);
        
        // push_error_bad_parameter_type_not_bool (0 ponieważ warunek stoi na pierwszym miejscu, a numerujemy od zera)
        line = ifg.generatePushErrorBadParameterTypeNotBool("IF", 0);
        ic.appendLine(line);
        
        // dodajemy instrukcję nop, która posłuży nam jedynie do dodania etykiety
        // potem zostanie ona usunięta, a nasza etykieta "wskoczy" w odpowiednie miejsce
        line = ifg.generateNop();
        line.addLabel(outside);
        ic.appendLine(line);
    }

    // wersja z dwoma funkcjami
    public void generateIf(CallParam callParam, Call call1, Call call2, Label forStart, Label forEnd,
            ErrorInfo ei)
    {
        // callParam
        // peek_jmp_if_not_bool, error
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
        // jmp, outside
        // error:
        // push_error_bad_parameter_type_not_bool, 0
        // outside:
        Label l1 = new Label();
        Label l2 = new Label();
        Label error = new Label();
        Label outside = new Label();

        // callParam
        cg.eval(callParam, forStart, forEnd);

        // peek_jmp_if_not_bool, error
        Line line = ifg.generatePeekJmpIfNotBool(error, ei);
        ic.appendLine(line);

        // pop, 1
        line = ifg.generatePop(1);
        ic.appendLine(line);

        // jmp_if_false, etykieta1
        line = ifg.generateJmpIfFalse(l1, ei);
        ic.appendLine(line);

        // call1
        cg.eval(call1, forStart, forEnd);

        // popc, 1
        line = ifg.generatePopc(1);
        ic.appendLine(line);

        // jmp, etykieta2
        line = ifg.generateJmp(l2, ei);
        ic.appendLine(line);

        // dodajemy fałszywą linię, która posłuży nam jedynie do dodania etykiety
        // potem ją usuniemy, a nasza etykieta "wskoczy" w odpowiednie miejsce
        Line fakeLine = new Line();
        Integer fakeLineLine = ic.numberOfLines();
        fakeLine.addLabel(l1);
        ic.appendLine(fakeLine);

        // call2
        cg.eval(call2, forStart, forEnd);

        // usuwamy wcześniej dodaną fałszywą linię
        ic.removeLine(fakeLineLine);

        // popc, 1
        line = ifg.generatePopc(1);
        ic.appendLine(line);

        // push, none:
        ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, mockFile);
        line = ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, mockErrorInfo),
                mockErrorInfo));
        line.addLabel(l2);
        ic.appendLine(line);

        // jmp, outside
        line = ifg.generateJmp(outside, ei);
        ic.appendLine(line);

        // dodajemy instrukcję nop, która posłuży nam jedynie do dodania etykiety
        // potem zostanie ona usunięta, a nasza etykieta "wskoczy" w odpowiednie miejsce
        line = ifg.generateNop();
        line.addLabel(error);
        ic.appendLine(line);

        // push_error_bad_parameter_type_not_bool (0 ponieważ warunek stoi na pierwszym miejscu, a numerujemy od zera)
        line = ifg.generatePushErrorBadParameterTypeNotBool("IF", 0);
        ic.appendLine(line);

        // dodajemy instrukcję nop, która posłuży nam jedynie do dodania etykiety
        // potem zostanie ona usunięta, a nasza etykieta "wskoczy" w odpowiednie miejsce
        line = ifg.generateNop();
        line.addLabel(outside);
        ic.appendLine(line);
    }

    public void generateCall(Call call1, List<Call> calls, Label forStart, Label forEnd)
    {
        // call1
        // popc, 1
        // call2
        // popc, 1
        // ...
        // calln
        cg.eval(call1, forStart, forEnd);

        for (Call c : calls)
        {
            Line line = ifg.generatePopc(1);
            ic.appendLine(line);
            cg.eval(c, forStart, forEnd);
        }
    }

    public void generateDN()
    {
        ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, mockFile);
        // push, none:
        Line line = ifg.generatePush(new ConstCallParam(new Token(TokenType.NONE, null, mockErrorInfo),
                mockErrorInfo));
        ic.appendLine(line);
    }

    public void generateBreak(Label forEnd, ErrorInfo ei)
    {
        // jmp, forEnd
        Line line = ifg.generateJmp(forEnd, ei);
        ic.appendLine(line);
    }

    public void generateContinue(Label forStart, ErrorInfo ei)
    {
        // jmp, forStart
        Line line = ifg.generateJmp(forStart, ei);
        ic.appendLine(line);
    }
}
