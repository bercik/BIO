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
package analysis.tree;

import analysis.lexer.Token;
import java.util.List;

/**
 *
 * @author robert
 */
public class ProgramTreeCreator
{
    private final ProgramTree programTree = new ProgramTree();
    
    public ProgramTreeCreator(List<Token<?>> tokens, List<Integer> steps)
    {
        // aktualny token
        int ct = 0;
        // referencje
        UserFunction uf = null;
        Call c = null;
        // zmienne pomocnicze
        Token<?> tokenStep18 = null;
        
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
                    uf = new UserFunction(name, token.getLine(), token.getChNum());
                    // dodajemy do programTree
                    programTree.addUserFunction(uf);
                    // koniec
                    break;
                // koniec parametrów funkcji użytkownika
                case 5:
                case 8:
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
                    Param param = new Param(name, token.getLine(), token.getChNum());
                    uf.addParam(param);
                    // koniec
                    break;
                // kolejny parametr funkcji użytkownika
                case 7:
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
                    c = new Call(name, null, token.getLine(), token.getChNum());
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
                    ConstCallParam ccp = new ConstCallParam(token, token.getLine(), token.getChNum());
                    c.addCallParam(ccp);
                    // koniec
                    break;
                // prametrem funkcji jest wywołanie funkcyjne (rekursywne wejście)
                case 20:
                    // przesuwamy o 1
                    ct += 1;
                    // wyciągamy nazwę
                    name = (String)tokenStep18.getValue();
                    // tworzymy nowe wywołanie funkcyjne i dodajemy
                    Call tmpC = new Call(name, c, tokenStep18.getLine(), tokenStep18.getChNum());
                    c.addCallParam(tmpC);
                    c = tmpC;
                    // koniec
                    break;
                // parametrem funkcji jest identyfikator
                case 21:
                    // nie przesuwamy, wyciągamy nazwę
                    name = (String)tokenStep18.getValue();
                    // tworzymy parametr i dodajemy
                    IdCallParam icp = new IdCallParam(name, tokenStep18.getLine(), tokenStep18.getChNum());
                    c.addCallParam(icp);
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
