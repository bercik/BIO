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
package pl.rcebula.analysis.semantic;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import pl.rcebula.Constants;
import pl.rcebula.analysis.ErrorInfo;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.CallParam;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.Param;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.UserFunction;
import pl.rcebula.code_generation.intermediate.SpecialFunctionsName;
import pl.rcebula.preprocessor.MyFiles;

/**
 *
 * @author robert
 */
public class SemanticChecker
{
    private final ProgramTree programTree;
    private final List<BuiltinFunction> builtinFunctions;
    private final MyFiles files;

    private int insideForLoopCounter = 0;

    public SemanticChecker(ProgramTree programTree, List<BuiltinFunction> builtinFunctions, MyFiles files)
            throws SemanticError
    {
        Logger logger = Logger.getGlobal();
        logger.info("SemanticChecker");
        
        this.programTree = programTree;
        this.builtinFunctions = builtinFunctions;
        this.files = files;

        checkUserFunctions();
        checkUserFunctionsCalls();
    }

    private void checkUserFunctions()
            throws SemanticError
    {
        boolean mainFunction = false;

        List<UserFunction> userFunctions = programTree.getUserFunctions();
        for (int i = 0; i < userFunctions.size(); ++i)
        {
            UserFunction uf = userFunctions.get(i);

            // sprawdź czy funkcja nie jest redeklarowana
            // wśród funkcji użytkownika
            for (int j = 0; j < i; ++j)
            {
                UserFunction auf = userFunctions.get(j);

                if (uf.getName().equals(auf.getName()))
                {
                    String message1 = "Function " + uf.getName() + " is already declared at: ";
                    throw new SemanticError(uf.getErrorInfo(), message1, auf.getErrorInfo(), "");
                }
            }
            // wśród funkcji wbudowanych
            for (int j = 0; j < builtinFunctions.size(); ++j)
            {
                BuiltinFunction bf = builtinFunctions.get(j);

                if (uf.getName().equals(bf.getName()))
                {
                    String message = "Function " + uf.getName() + " is already declared as builtin function";
                    throw new SemanticError(uf.getErrorInfo(), message);
                }
            }

            // sprawdź czy nazwy parametrów się nie powtarzają
            List<Param> params = uf.getParams();
            for (int j = 0; j < params.size(); ++j)
            {
                Param p1 = params.get(j);

                for (int k = 0; k < j; ++k)
                {
                    Param p2 = params.get(k);

                    if (p1.getName().equals(p2.getName()))
                    {
                        String message = "In function " + uf.getName() + " parameter " + p1.getName()
                                + " occurs more than one time";
                        throw new SemanticError(uf.getErrorInfo(), message);
                    }
                }
            }

            // sprawdzenie czy funkcja main występuje i czy przyjmuje dobrą ilość argumentów
            if (uf.getName().equals(Constants.mainFunctionName))
            {
                mainFunction = true;

                if (!Arrays.asList(Constants.mainFunctionParameters).contains(uf.getParams().size()))
                {
                    String message = "Function " + Constants.mainFunctionName + " should accept ";

                    for (int v : Constants.mainFunctionParameters)
                    {
                        message += v + ", ";
                    }
                    message = message.substring(0, message.length() - 2);

                    message += " parameters, instead it takes " + uf.getParams().size();

                    throw new SemanticError(uf.getErrorInfo(), message);
                }
            }

            // dodajemy instrukcję RETURN(none) na koniec funkcji jeżeli na końcu nie znajduje się już
            // instrukcja RETURN
            boolean lastCallReturn = false;
            Call lastCall = uf.getCalls().get(uf.getCalls().size() - 1);
            if (lastCall instanceof Call)
            {
                Call c = (Call)lastCall;
                if (c.getName().equals(Constants.returnFunctionName))
                {
                    lastCallReturn = true;
                }
            }

            if (!lastCallReturn)
            {
                ErrorInfo mockErrorInfo = new ErrorInfo(-1, -1, files.getFileGeneratedByCompiler());
                Call returnCall = new Call(Constants.returnFunctionName, null, mockErrorInfo);
                returnCall.addCallParam(new ConstCallParam(new Token<>(TokenType.NONE, null, mockErrorInfo), 
                        mockErrorInfo));
                uf.addCall(returnCall);
            }
        }

        // sprawdzamy czy funkcja main występuje w programie
        if (!mainFunction)
        {
            throw new SemanticError("There is no " + Constants.mainFunctionName + " function in your program");
        }
    }

    private void checkUserFunctionsCalls()
            throws SemanticError
    {
        List<UserFunction> userFunctions = programTree.getUserFunctions();
        for (int i = 0; i < userFunctions.size(); ++i)
        {
            UserFunction uf = userFunctions.get(i);

            // sprawdź wywołania
            checkCalls(uf);
        }
    }

    private void checkCalls(UserFunction uf)
            throws SemanticError
    {
        for (Call c : uf.getCalls())
        {
            checkCall(c);
        }
    }

    private void checkCall(Call call)
            throws SemanticError
    {
        List<CallParam> callParams = call.getCallParams();
        List<UserFunction> userFunctions = programTree.getUserFunctions();

        boolean forLoopFunction = false;

        boolean functionExists = false;
        // szukaj czy funkcja występuje wśród funkcji użytkownika
        for (int i = 0; i < userFunctions.size(); ++i)
        {
            UserFunction uf = userFunctions.get(i);
            if (call.getName().equals(uf.getName()))
            {
                functionExists = true;

                // sprawdź czy ilość przekazywanych parametrów się zgadza
                if (callParams.size() != uf.getParams().size())
                {
                    String message = "Function " + call.getName() + " takes " + uf.getParams().size()
                            + " parameters, got " + callParams.size();
                    throw new SemanticError(call.getErrorInfo(), message);
                }

                break;
            }
        }

        if (!functionExists)
        {
            // szukaj czy funkcja występuje wśród funkcji wbudowanych
            for (int i = 0; i < builtinFunctions.size(); ++i)
            {
                BuiltinFunction bf = builtinFunctions.get(i);
                if (call.getName().equals(bf.getName()))
                {
                    functionExists = true;

                    // sprawdź czy ilość i typy przekazywanych argumentów się zgadzają 
                    // biorąc pod uwagę repeatPattern
                    // sprawdź ilość
                    if (!bf.isGoodNumberOfParams(callParams.size()))
                    {
                        if (bf.isRepeated())
                        {
                            List<Integer> gnop = bf.getGoodNumberOfParamsList(3);
                            String goodParamsStr = gnop.get(0).toString() + ", " + gnop.get(1).toString() + ", "
                                    + gnop.get(2).toString() + "...";

                            String message = "Function " + call.getName() + " takes " + goodParamsStr
                                    + " parameters, got " + callParams.size();
                            throw new SemanticError(call.getErrorInfo(), message);
                        }
                        else
                        {
                            String message = "Function " + call.getName() + " takes " + bf.getParams().size()
                                    + " parameters, got " + callParams.size();
                            throw new SemanticError(call.getErrorInfo(), message);
                        }
                    }
                    // sprawdź typy
                    List<Boolean> repeatPattern = bf.getRepeatPattern();
                    // <startCycle, endCycle>
                    // jeżeli nie ma żadnego repeatPattern to dzięki tym wartościom warunek w 
                    // trzeciej pętli for nie zostanie spełniony
                    int startCycle = -1;
                    int endCycle = -2;
                    // idź od początku do napotkania repeat true
                    for (int k = 0; k < callParams.size(); ++k)
                    {
                        boolean repeat = repeatPattern.get(k);
                        if (repeat)
                        {
                            startCycle = k;
                            break;
                        }

                        ParamType pt1 = bf.getParams().get(k);
                        ParamType pt2 = ParamType.convert(callParams.get(k));

                        compareThrowIfNotEqual(pt1, pt2, call, k);
                    }
                    // jeżeli napotkano repeat true to
                    // idź od końca do napotkania repeat true
                    if (startCycle >= 0)
                    {
                        int x = repeatPattern.size() - 1;
                        for (int k = callParams.size() - 1; k >= 0; --k)
                        {
                            if (x < 0)
                            {
                                break;
                            }

                            boolean repeat = repeatPattern.get(x);
                            if (repeat)
                            {
                                endCycle = k;
                                break;
                            }

                            ParamType pt1 = bf.getParams().get(x);
                            ParamType pt2 = ParamType.convert(callParams.get(k));

                            compareThrowIfNotEqual(pt1, pt2, call, k);

                            --x;
                        }
                    }
                    // idź od startCycle do endCycle sprawdzając ilość cykli i parametry
                    int cycles = 0;
                    List<ParamType> repeatPatternTypes = bf.getRepeatPatternTypes();
                    for (int k = startCycle; k <= endCycle;)
                    {
                        for (int z = 0; z < repeatPatternTypes.size(); ++z)
                        {
                            ParamType pt1 = repeatPatternTypes.get(z);
                            ParamType pt2 = ParamType.convert(callParams.get(k));

                            compareThrowIfNotEqual(pt1, pt2, call, k);
                            
                            ++k;
                        }
                        ++cycles;
                    }
                    call.setRepeatCycles(cycles);

                    // warunki specjalne
                    // sprawdź czy funkcja jest pętlą FOR
                    if (call.getName().equals(SpecialFunctionsName.forLoopFunctionName))
                    {
                        forLoopFunction = true;
                        ++insideForLoopCounter;
                    }
                    // sprawdź czy funkcja jest instrukcją BREAK lub CONTINUE
                    else if (call.getName().equals(SpecialFunctionsName.breakFunctionName)
                            || call.getName().equals(SpecialFunctionsName.continueFunctionName))
                    {
                        // sprawdź czy występuje wewnątrz pętli FOR
                        if (insideForLoopCounter <= 0)
                        {
                            String message = "Function " + call.getName() + " must occurs inside "
                                    + SpecialFunctionsName.forLoopFunctionName + " loop";
                            throw new SemanticError(call.getErrorInfo(), message);
                        }
                    }

                    break;
                }
            }
        }

        if (!functionExists)
        {
            String message = "Function " + call.getName() + " doesn't exist";
            throw new SemanticError(call.getErrorInfo(), message);
        }

        // wywołaj rekurencyjnie metodę dla następnych call
        for (CallParam cp : callParams)
        {
            if (cp instanceof Call)
            {
                checkCall((Call)cp);
            }
        }

        // jeżeli funkcja for to zdekrementuj licznik
        if (forLoopFunction)
        {
            --insideForLoopCounter;
        }
    }

    private void compareThrowIfNotEqual(ParamType pt1, ParamType pt2, Call call, int k)
            throws SemanticError
    {
        if (!ParamType.compare(pt1, pt2))
        {
            String message = "In function " + call.getName() + " expected " + (k + 1)
                    + " parameter to be " + pt1.toString() + ", got " + pt2.toString();
            CallParam cp = call.getCallParams().get(k);
            throw new SemanticError(cp.getErrorInfo(), message);
        }
    }
}
