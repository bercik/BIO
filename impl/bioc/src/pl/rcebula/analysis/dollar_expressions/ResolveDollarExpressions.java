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
package pl.rcebula.analysis.dollar_expressions;

import java.util.ArrayList;
import java.util.List;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 */
public class ResolveDollarExpressions
{
    private final List<Token<?>> tokens;
    private final List<Token<?>> outputTokens = new ArrayList<>();

    public ResolveDollarExpressions(List<Token<?>> tokens)
    {
        this.tokens = tokens;

        analyse();
    }

    private void analyse()
    {
        // iteruj po wszystkich tokenach
        for (Token t : tokens)
        {
            // jeżeli token id lub id_struct
            if (t.getTokenType().equals(TokenType.ID) || t.getTokenType().equals(TokenType.ID_STRUCT))
            {
                String id = (String)t.getValue();
                ErrorInfo ei = t.getErrorInfo();

                // jeżeli zaczyna się od $
                if (id.startsWith("$"))
                {
                    String getFunName = null;
                    int offset = 0;
                    // jeżeli zaczyna się od $$
                    if (id.startsWith("$$"))
                    {
                        getFunName = "GET_GLOBAL";
                        offset = 2;
                    }
                    else
                    {
                        getFunName = "GET_STATIC";
                        offset = 1;
                    }
                    
                    // wyciągnij ID bez znaków dolara
                    String simpleId = id.substring(offset);
                    
                    // stwórz odpowiednie tokeny i dodaj do output tokens
                    // getFunName(simpleId)
                    Token newTok = new Token(TokenType.ID, getFunName, ei);
                    outputTokens.add(newTok);
                    
                    newTok = new Token(TokenType.OPEN_BRACKET, null, ei);
                    outputTokens.add(newTok);
                    
                    newTok = new Token(t.getTokenType(), simpleId, ei);
                    outputTokens.add(newTok);
                    
                    newTok = new Token(TokenType.CLOSE_BRACKET, null, ei);
                    outputTokens.add(newTok);
                }
                // inaczej
                else
                {
                    // dodaj do tokenów wyjściowych
                    outputTokens.add(t);
                }
            }
            // inaczej
            else
            {
                // dodaj do wyjściowych tokenów
                outputTokens.add(t);
            }
        }
    }

    public List<Token<?>> getTokens()
    {
        return outputTokens;
    }
}
