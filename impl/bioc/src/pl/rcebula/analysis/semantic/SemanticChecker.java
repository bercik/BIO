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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import pl.rcebula.Constants;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.tree.Call;
import pl.rcebula.analysis.tree.CallParam;
import pl.rcebula.analysis.tree.ConstCallParam;
import pl.rcebula.analysis.tree.Param;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.UserFunction;
import pl.rcebula.code_generation.intermediate.SpecialFunctionsName;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.utils.Pair;

/**
 *
 * @author robert
 */
public class SemanticChecker
{
    private final ProgramTree programTree;
    private final List<BuiltinFunction> builtinFunctions;
    private final MyFiles files;

    private int insideLoopCounter = 0;

    private Call lastCallInCheckCall = null;
    
    public SemanticChecker(ProgramTree programTree, List<BuiltinFunction> builtinFunctions, MyFiles files)
            throws SemanticError
    {
//        Logger logger = Logger.getGlobal();
//        logger.info("SemanticChecker");

        this.programTree = programTree;
        this.builtinFunctions = builtinFunctions;
        this.files = files;

        checkUserFunctions();
        // try catch w przypadku nieskończonej rekurencji dla domyślnych parametrów z wywołaniem funkcji
        try
        {
            checkDefaultParameters();
        }
        catch (StackOverflowError ex)
        {
            String msg = "Endless recursion when checking default parameters";
            throw new SemanticError(lastCallInCheckCall.getErrorInfo(), msg);
        }
        checkUserFunctionsCalls();
    }

    private void checkDefaultParameters()
            throws SemanticError
    {
        List<UserFunction> userFunctions = programTree.getUserFunctions();
        for (int i = 0; i < userFunctions.size(); ++i)
        {
            UserFunction uf = userFunctions.get(i);

            // sprawdź czy parametry domyślne są poprawne
            List<Param> params = uf.getParams();
            for (int j = 0; j < params.size(); ++j)
            {
                Param p1 = params.get(j);

                if (p1.getDefaultCallParam() != null)
                {
                    CallParam cp = p1.getDefaultCallParam();
                    if (cp instanceof Call)
                    {
                        checkCall((Call)cp);
                    }
                }
            }
        }
    }

    private void checkUserFunctions()
            throws SemanticError
    {
        boolean mainFunction = false;

        List<UserFunction> userFunctions = programTree.getUserFunctions();
        for (int i = 0; i < userFunctions.size(); ++i)
        {
            UserFunction uf = userFunctions.get(i);
            uf.countNonDefaultParams();

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

                if (uf.getName().equals(bf.getName()) || uf.getName().equals(bf.getAlias()))
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

    private void checkUserFunctionCall(Call call, UserFunction uf)
            throws SemanticError
    {
        List<CallParam> callParams = call.getCallParams();

        // sprawdź czy najpierw występują parametry nienazywane, a potem nazywane
        boolean namedParameter = false;
        for (int i = 0; i < callParams.size(); ++i)
        {
            CallParam cp = callParams.get(i);
            if (cp.getParName().equals("") && namedParameter)
            {
                String message = "Unnamed parameter after named one";
                throw new SemanticError(cp.getErrorInfo(), message);
            }
            if (!cp.getParName().equals(""))
            {
                namedParameter = true;
            }
        }

        // sprawdź czy podano wszyskie parametry nie domyślne, czy parametry nie powtarzają się i ustal
        // kolejność ich przekazywania do funkcji, a także uzupełnij wartości domyślne
        // pomocnicza lista zawierająca nazwy parametrów i czy zostały przekazane ich wartości w wywołaniu
        List<Pair<String, Boolean>> helpList = new ArrayList<>();
        for (int i = 0; i < uf.getParams().size(); ++i)
        {
            Param p = uf.getParams().get(i);
            helpList.add(new Pair<>(p.getName(), false));
        }
        // kolejność parametrów
        List<Integer> orderList = new ArrayList<>();

        for (int i = 0; i < callParams.size(); ++i)
        {
            CallParam cp = callParams.get(i);

            // parametr nienazywany
            if (cp.getParName().equals(""))
            {
                // sprawdzamy czy ilość parametrów nie jest większa niż przyjmowana przez funkcję
                if (i >= uf.getParams().size())
                {
                    String message = "Function " + call.getName() + " takes " + uf.getNonDefaultParams()
                            + " to " + uf.getParams().size() + " parameters, got " + call.getCallParams().size();
                    throw new SemanticError(call.getErrorInfo(), message);
                }

                Pair<String, Boolean> p = helpList.get(i);
                p.setRight(true);
                orderList.add(i + 1);
            }
            // inaczej parametr nazywany
            else
            {
                String parName = cp.getParName();
                // sprawdzamy czy istnieje wśród parametrów funkcji
                boolean ok = false;
                for (int k = 0; k < helpList.size(); ++k)
                {
                    Pair<String, Boolean> p = helpList.get(k);
                    if (p.getLeft().equals(parName))
                    {
                        // sprawdzamy czy już nie przekazano tego parametru
                        if (p.getRight())
                        {
                            String message = "In function " + call.getName() + " there are multiple values for argument "
                                    + p.getLeft();
                            throw new SemanticError(cp.getParNameErrorInfo(), message);
                        }
                        p.setRight(true);
                        orderList.add(k + 1);
                        ok = true;
                        break;
                    }
                }

                if (!ok)
                {
                    String message = "In function " + call.getName() + " there is no argument " + parName;
                    throw new SemanticError(cp.getParNameErrorInfo(), message);
                }
            }
        }

        // sprawdź czy zostały podane wszystkie parametry nie domyślne
        for (int i = 0; i < helpList.size(); ++i)
        {
            Pair<String, Boolean> p = helpList.get(i);
            // jeżeli nie przekazano takiego parametru
            if (!p.getRight())
            {
                // sprawdzamy czy jest domyślny
                Param par = uf.getParams().get(i);
                // jest domyślny
                if (par.getDefaultCallParam() != null)
                {
                    // dodajemy do listy kolejności
                    orderList.add(i + 1);
                    // dodajemy parametr domyślny do wywołania funkcyjnego
                    call.addCallParam(par.getDefaultCallParam());
                }
                // nie jest domyślny
                else
                {
                    String message = "In function " + call.getName() + " missing required argument "
                            + p.getLeft();
                    throw new SemanticError(call.getErrorInfo(), message);
                }
            }
        }

        // sprawdź czy order list nie jest uporządkowaną listą, jeżeli nie to ustaw w call
        List tmp = new ArrayList(orderList);
        Collections.sort(tmp);
        boolean sorted = tmp.equals(orderList);
        if (!sorted)
        {
            call.setOrderList(orderList);
        }
    }

    private boolean checkBuiltinFunctionCall(Call call, BuiltinFunction bf, boolean loopFunction)
            throws SemanticError
    {
        List<CallParam> callParams = call.getCallParams();

        // sprawdź czy nie przekazano nazywanego parametru
        for (int i = 0; i < callParams.size(); ++i)
        {
            CallParam cp = callParams.get(i);
            if (!cp.getParName().equals(""))
            {
                String message = "In function " + call.getName() + " you can't pass named parameter into "
                        + "builtin function";
                throw new SemanticError(cp.getParNameErrorInfo(), message);
            }
        }
        
        // sprawdź czy ilość i typy przekazywanych argumentów się zgadzają 
        // biorąc pod uwagę repeatPattern
        // sprawdź ilość
        if (!bf.isGoodNumberOfParams(callParams.size()))
        {
            if (bf.isOptional())
            {
                List<Integer> list = bf.getGoodNumberOfParamsList(2);
                String message = "Function " + call.getName() + " takes " + list.get(0) + " or " + list.get(1)
                        + " parameters, got " + callParams.size();
                throw new SemanticError(call.getErrorInfo(), message);
            }
            else if (bf.isRepeated())
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
        // sprawdź czy funkcja jest pętlą FOR lub WHILE
        if (call.getName().equals(SpecialFunctionsName.forLoopFunctionName) 
                || call.getName().equals(SpecialFunctionsName.whileLoopFunctionName))
        {
            loopFunction = true;
            ++insideLoopCounter;
        }
        // sprawdź czy funkcja jest instrukcją BREAK lub CONTINUE
        else if (call.getName().equals(SpecialFunctionsName.breakFunctionName)
                || call.getName().equals(SpecialFunctionsName.continueFunctionName))
        {
            // sprawdź czy występuje wewnątrz pętli FOR
            if (insideLoopCounter <= 0)
            {
                String message = "Function " + call.getName() + " must occurs inside "
                        + SpecialFunctionsName.forLoopFunctionName + " or " + 
                        SpecialFunctionsName.whileLoopFunctionName + " loop";
                throw new SemanticError(call.getErrorInfo(), message);
            }
        }

        return loopFunction;
    }
    
    private void checkCall(Call call)
            throws SemanticError
    {
        lastCallInCheckCall = call;
        
        List<CallParam> callParams = call.getCallParams();
        List<UserFunction> userFunctions = programTree.getUserFunctions();

        boolean loopFunction = false;

        boolean functionExists = false;
        // szukaj czy funkcja występuje wśród funkcji użytkownika
        for (int i = 0; i < userFunctions.size(); ++i)
        {
            UserFunction uf = userFunctions.get(i);
            if (call.getName().equals(uf.getName()))
            {
                functionExists = true;
                
                checkUserFunctionCall(call, uf);

                break;
            }
        }

        if (!functionExists)
        {
            // szukaj czy funkcja występuje wśród funkcji wbudowanych
            for (int i = 0; i < builtinFunctions.size(); ++i)
            {
                BuiltinFunction bf = builtinFunctions.get(i);
                // sprawdź czy jest aliasem
                if (call.getName().equals(bf.getAlias()))
                {
                    // podmień alias na prawdziwą nazwę
                    call.setName(bf.getName());
                }

                if (call.getName().equals(bf.getName()))
                {
                    functionExists = true;

                    loopFunction = checkBuiltinFunctionCall(call, bf, loopFunction);

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
                Call c = (Call)cp;
                
                checkCall(c);
            }
        }

        // jeżeli funkcja for lub while to zdekrementuj licznik
        if (loopFunction)
        {
            --insideLoopCounter;
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
