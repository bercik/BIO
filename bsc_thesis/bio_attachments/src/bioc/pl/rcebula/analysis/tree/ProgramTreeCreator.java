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
package pl.rcebula.analysis.tree;

import pl.rcebula.analysis.lexer.Token;
import java.util.List;
import java.util.logging.Logger;
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public class ProgramTreeCreator
{
    private final ProgramTree programTree = new ProgramTree();
    
    public ProgramTreeCreator(List<Token<?>> tokens, List<Integer> steps)
    {
        Logger logger = Logger.getGlobal();
        logger.info("ProgramTreeCreator");
        
        // aktualny token
        int ct = 0;
        // referencje
        UserFunction uf = null;
        Param p = null;
        Call c = null;
        // zmienne pomocnicze
        Token<?> tokenStep18 = null;
        Token<?> tokenStep39 = null;
        String parName = null;
        ErrorInfo parNameErrorInfo = null;
        
        for (int cs = 0; cs < steps.size(); ++cs)
        {
            Token<?> token;
            String name;
            Integer currentStep = steps.get(cs);
            
            switch (currentStep)
            {
                // funkcja użytkownika
                case 3:
                    // token z informacją na drugim miejscu
                    token = tokens.get(ct + 1);
                    // przesuwamy o 3
                    ct += 3;
                    // wyciągamy informacje
                    name = (String)token.getValue();
                    // tworzymy funkcję użytkownika
                    uf = new UserFunction(name, token.getErrorInfo());
                    // dodajemy do programTree
                    programTree.addUserFunction(uf);
                    // koniec
                    break;
                // koniec parametrów funkcji użytkownika
                case 5:
                case 8:
                case 25:
                    // przesuwamy o 1
                    ct += 1;
                    // koniec
                    break;
                // parametr funkcji użytkownika
                case 6:
                    // token z informacją na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // wyciągamy nazwę
                    name = (String)token.getValue();
                    // dodajemy do parametrów funkcji użytkownika
                    p = new Param(name, token.getErrorInfo());
                    uf.addParam(p);
                    // koniec
                    break;
                // kolejny parametr funkcji użytkownika
                case 7:
                    // przesuwamy o 1
                    ct += 1;
                    // koniec
                    break;
                // parametr z wart. domyślną
                case 22:
                    // przesuwamy o 1
                    ct += 1;
                    // koniec
                    break;
                // kolejny parametr z wart. domyślną
                case 24:
                    // token z informacją na drugim miejscu
                    token = tokens.get(ct + 1);
                    // przesuwamy o 3
                    ct += 3;
                    // wyciągamy nazwę
                    name = (String)token.getValue();
                    // dodajemy parametr funkcji użytkownika
                    p = new Param(name, token.getErrorInfo());
                    uf.addParam(p);
                    // koniec
                    break;
                // domyślna wartość parametru funkcji użytkownika jako stała
                case 26:
                    // informacja na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // tworzymy const call param
                    ConstCallParam ccp = new ConstCallParam(token, token.getErrorInfo());
                    // dodajemy jako domyślny parametr
                    p.setDefaultCallParam(ccp);
                    // koniec
                    break;
                // domyślna wartość parametru funkcji użytkownika jako wywołanie funkcyjne
                case 27:
                    // informacja na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 2
                    ct += 2;
                    // wyciągamy nazwę funkcji
                    name = (String)token.getValue();
                    // tworzymy nowe wywołanie
                    c = new Call(name, null, token.getErrorInfo());
                    // koniec
                    break;
                // koniec wywołania funkcyjnego parametru domyślnego (rekursywne wyjście)
                case 29:
                case 33:
                    // przesuwamy o 1
                    ct += 1;
                    // jeżeli to korzeń to dodajemy jako domyślny parametr
                    if (c.getParentCall() == null)
                    {
                        p.setDefaultCallParam(c);
                    }
                    // wyjście
                    c = c.getParentCall();
                    // koniec
                    break;
                // stała wartość jako kolejny parametr wywołania funkcyjnego dla parametru domyślnego
                case 31:
                    // informacja na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // tworzymy const call param
                    ccp = new ConstCallParam(token, token.getErrorInfo());
                    // dodajemy jako kolejny parametr wywołania funkcyjnego
                    c.addCallParam(ccp);
                    // koniec
                    break;
                // wywołanie funkcyjne jako kolejny parametr wywołania funkcyjnego dla parametru domyślnego
                case 32:
                    // informacja na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 2
                    ct += 2;
                    // wyciągamy nazwę
                    name = (String)token.getValue();
                    // tworzymy nowy call i dodajemy jako parametr obecnego
                    Call tmpCall = new Call(name, c, token.getErrorInfo());
                    c.addCallParam(tmpCall);
                    // ustawiamy jako obecny (rekursywne wejście)
                    c = tmpCall;
                    // koniec
                    break;
                // kolejny parametr wywołania funkcyjnego dla parametru domyślnego
                case 34:
                    // przesuwamy o 1
                    ct += 1;
                    // koniec
                    break;
                // koniec funkcji użytkownika
                case 11:
                    // przesuwamy o 1
                    ct += 1;
                    // ustawiamy funkcję użytkownika jako null
                    uf = null;
                    // koniec
                    break;
                // wywołanie funkcji bezzwrotnej
                case 12:
                    // token z informacją na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 2
                    ct += 2;
                    // wyciągamy nazwę
                    name = (String)token.getValue();
                    // dodajemy do funkcji użytkownika
                    c = new Call(name, null, token.getErrorInfo());
                    uf.addCall(c);
                    // koniec
                    break;
                // koniec parametrów wywołania funkcji (rekursywne wyjście)
                case 14:
                case 17:
                    // przesuwamy o 1
                    ct += 1;
                    // rekursywne wyjście
                    c = c.getParentCall();
                    // koniec
                    break;
                // kolejny parametr wywołania funkcyjnego
                case 16:
                    // przesuwamy o 1
                    ct += 1;
                    // koniec
                    break;
                // parametrem funkcji jest identyfikator lub wywołanie funkcyjne
                case 18:
                    // wyciągamy token z informacją z pierwszego miejsca
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // zapisujemy token
                    tokenStep18 = token;
                    // koniec
                    break;
                // parametrem funkcji jest wartość
                case 19:
                    // wyciągamy token z informacją z pierwszego miejsca
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // tworzymy nowy parametr o stałej wartości i dodajemy
                    ccp = new ConstCallParam(token, token.getErrorInfo());
                    c.addCallParam(ccp);
                    // koniec
                    break;
                // parametrem funkcji jest identyfikator struktury
                case 35:
                    // informacja na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // wyciągamy nazwę
                    name = (String)token.getValue();
                    // tworzymy parametr i dodajemy
                    IdCallParam icp = new IdCallParam(name, token.getErrorInfo());
                    c.addCallParam(icp);
                    // koniec
                    break;
                // prametrem funkcji jest wywołanie funkcyjne (rekursywne wejście)
                case 20:
                    // przesuwamy o 1
                    ct += 1;
                    // wyciągamy nazwę
                    name = (String)tokenStep18.getValue();
                    // tworzymy nowe wywołanie funkcyjne i dodajemy
                    Call tmpC = new Call(name, c, tokenStep18.getErrorInfo());
                    c.addCallParam(tmpC);
                    c = tmpC;
                    // koniec
                    break;
                // parametrem funkcji jest identyfikator
                case 21:
                    // nie przesuwamy, wyciągamy nazwę
                    name = (String)tokenStep18.getValue();
                    // tworzymy parametr i dodajemy
                    icp = new IdCallParam(name, tokenStep18.getErrorInfo());
                    c.addCallParam(icp);
                    // koniec
                    break;
                // nazywany parametr funkcji
                case 36:
                    // przesuwamy o 1
                    ct += 1;
                    // ustawiamy zmienną pomocniczą
                    parName = (String)tokenStep18.getValue();
                    parNameErrorInfo = tokenStep18.getErrorInfo();
                    // koniec
                    break;
                // parametr nazywany to stała
                case 37:
                    // informacja na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // tworzymy const call param
                    ccp = new ConstCallParam(token, token.getErrorInfo(), parName, parNameErrorInfo);
                    // dodajemy do parametrów funkcji
                    c.addCallParam(ccp);
                    // koniec
                    break;
                // parametr nazywany to identyfikator struktury
                case 38:
                    // informacja na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // wyciągamy nazwę
                    name = (String)token.getValue();
                    // tworzymy id call param
                    icp = new IdCallParam(name, token.getErrorInfo(), parName, parNameErrorInfo);
                    // dodajemy do parametrów funkcji
                    c.addCallParam(icp);
                    // koniec
                    break;
                // nazywanym parametrem funkcji jest identyfikator lub wywołanie funkcyjne
                case 39:
                    // informacja na pierwszym miejscu
                    token = tokens.get(ct);
                    // przesuwamy o 1
                    ct += 1;
                    // zapisujemy
                    tokenStep39 = token;
                    // koniec
                    break;
                // parametrem nazywanym jest identyfikator
                case 40:
                    // nie przesuwamy, wyciągamy nazwę
                    name = (String)tokenStep39.getValue();
                    // tworzymy id call param
                    icp = new IdCallParam(name, tokenStep39.getErrorInfo(), parName, parNameErrorInfo);
                    // dodajemy do parametrów
                    c.addCallParam(icp);
                    // koniec
                    break;
                // parametrem nazywanym jest wywołanie funkcyjne (rekursywne wejście)
                case 41:
                    // przesuwamy o 1
                    ct += 1;
                    // wyciągamy nazwę
                    name = (String)tokenStep39.getValue();
                    // tworzymy nowe wywołanie funkcyjne i dodajemy
                    tmpC = new Call(name, c, tokenStep39.getErrorInfo(), parName, parNameErrorInfo);
                    c.addCallParam(tmpC);
                    c = tmpC;
                    // koniec
                    break;
            }
        }
    }
    
    public ProgramTree getProgramTree()
    {
        return programTree;
    }
}
