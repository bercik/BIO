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
package analysis.parser;

import analysis.lexer.Lexer;
import analysis.lexer.Token;
import analysis.lexer.TokenComparator;
import analysis.lexer.TokenType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author robert
 */
public class Parser
{
    // kroki podjęte do sparsowania wejścia
    private final List<Integer> steps = new ArrayList<>();
    // aktualny token na wejściu
    private Token<?> currentToken;

    // porównywacz tokenów, który porównuje zarówno typy jak i wartości
    private final TokenComparator comparator = new TokenComparator();
    // iterator tokenów wejściowych
    private final Iterator<Token<?>> tokensIterator;

    // typy tokenów uznawane jako stałe wartości
    private final List<TokenType> constValuesTokenTypes = new ArrayList<TokenType>()
    {
        {
            add(TokenType.BOOL);
            add(TokenType.INT);
            add(TokenType.STRING);
            add(TokenType.FLOAT);
            add(TokenType.NONE);
        }
    };

    // niektóre z częściej używanych tokenów
    private final Token defKeywordToken = getKeywordToken(Lexer.defKeyword);
    private final Token endKeywordToken = getKeywordToken(Lexer.endKeyword);
    private final Token idToken = new Token(TokenType.ID, "", 0, 0);
    private final Token openBracketToken = new Token(TokenType.OPEN_BRACKET, null, 0, 0);
    private final Token closeBracketToken = new Token(TokenType.CLOSE_BRACKET, null, 0, 0);
    private final Token commaToken = new Token(TokenType.COMMA, null, 0, 0);
    private final Token endToken = new Token(TokenType.END, null, 0, 0);

    public Parser(List<Token<?>> tokens)
            throws ParserError
    {
        tokensIterator = tokens.iterator();
        currentToken = tokensIterator.next();

        D();
    }

    private void D()
            throws ParserError
    {
        if (comparator.compare(currentToken, defKeywordToken) == 0)
        {
            steps.add(0);
            D_2();
            D_1();
        }
        else
        {
            fail(false, defKeywordToken);
        }
    }

    private void D_1()
            throws ParserError
    {
        if (comparator.compare(currentToken, defKeywordToken) == 0)
        {
            steps.add(1);
            D_2();
            D_1();
        }
        else if (compareTokenType(currentToken, TokenType.END))
        {
            steps.add(2);
        }
        else
        {
            fail(false, defKeywordToken, endKeywordToken);
        }
    }

    private void D_2()
            throws ParserError
    {
        if (comparator.compare(currentToken, defKeywordToken) == 0)
        {
            steps.add(3);
            readTokenAndMove(defKeywordToken);
            readTokenAndMove(idToken);
            readTokenAndMove(openBracketToken);
            P();
            readTokenAndMove(closeBracketToken);
            F();
            readTokenAndMove(endKeywordToken);
        }
        else
        {
            fail(false, defKeywordToken);
        }
    }
    
    private void P()
            throws ParserError
    {
        if (compareTokenType(currentToken, TokenType.CLOSE_BRACKET))
        {
            steps.add(5);
        }
        else if (compareTokenType(currentToken, TokenType.ID))
        {
            steps.add(4);
            P_1();
        }
        else
        {
            fail(false, closeBracketToken, idToken);
        }
    }
    
    private void P_1()
            throws ParserError
    {
        if (compareTokenType(currentToken, TokenType.ID))
        {
            steps.add(6);
            readTokenAndMove(idToken);
            P_2();
        }
        else
        {
            fail(false, idToken);
        }
    }
    
    private void P_2()
            throws ParserError
    {
        if (compareTokenType(currentToken, TokenType.CLOSE_BRACKET))
        {
            steps.add(8);
        }
        else if (compareTokenType(currentToken, TokenType.COMMA))
        {
            steps.add(7);
            readTokenAndMove(commaToken);
            P_1();
        }
        else
        {
            fail(false, closeBracketToken, commaToken);
        }
    }
    
    private void F()
            throws ParserError
    {
        if (compareTokenType(currentToken, TokenType.ID))
        {
            steps.add(9);
            C();
            F_1();
        }
        else
        {
            fail(false, idToken);
        }
    }
    
    private void F_1()
            throws ParserError
    {
        if (comparator.compare(currentToken, endKeywordToken) == 0)
        {
            steps.add(11);
        }
        else if (compareTokenType(currentToken, TokenType.ID))
        {
            steps.add(10);
            C();
            F_1();
        }
        else
        {
            fail(false, endKeywordToken, idToken);
        }
    }

    private void C()
            throws ParserError
    {
        if (compareTokenType(currentToken, TokenType.ID))
        {
            steps.add(12);
            readTokenAndMove(idToken);
            readTokenAndMove(openBracketToken);
            C_1();
            readTokenAndMove(closeBracketToken);
        }
    }
    
    private void fail(boolean constValue, Token... expectedTokens)
            throws ParserError
    {
        String message = "Expected: ";

        for (Token t : expectedTokens)
        {
            message += getTokenString(t) + ", ";
        }

        if (constValue)
        {
            message += "constant value, ";
        }

        message = message.substring(0, message.length() - 2);

        message += " got " + getTokenString(currentToken);

        throw new ParserError(currentToken.getLine(), currentToken.getChNum(), message);
    }

    private boolean isConstValue(Token<?> token)
    {
        return (constValuesTokenTypes.contains(token.getTokenType()));
    }

    private boolean compareTokenType(Token<?> token, TokenType tokenType)
    {
        return (token.getTokenType().equals(tokenType));
    }

    private Token getKeywordToken(String keyword)
    {
        return new Token(TokenType.KEYWORD, keyword, 0, 0);
    }

    private void readConstValueAndMove()
            throws ParserError
    {
        if (isConstValue(currentToken))
        {
            currentToken = tokensIterator.next();
        }
        else
        {
            throw new ParserError(currentToken.getLine(), currentToken.getChNum(),
                    "Expected constant value got " + getTokenString(currentToken));
        }
    }

    private void readTokenAndMove(Token<?> tokenToRead)
            throws ParserError
    {
        if (tokenToRead.getTokenType().equals(TokenType.KEYWORD))
        {
            if (comparator.compare(currentToken, tokenToRead) == 0)
            {
                currentToken = tokensIterator.next();
            }
            else
            {
                throw new ParserError(currentToken.getLine(), currentToken.getChNum(),
                        "Expected " + getTokenString(tokenToRead) + " got " + getTokenString(currentToken));
            }
        }
        else if (tokenToRead.getTokenType().equals(currentToken.getTokenType()))
        {
            currentToken = tokensIterator.next();
        }
        else
        {
            throw new ParserError(currentToken.getLine(), currentToken.getChNum(),
                    "Expected " + getTokenString(tokenToRead) + " got " + getTokenString(currentToken));
        }
    }

    private String getTokenString(Token<?> token)
    {
        String toReturn = "";

        if (token.getTokenType().equals(TokenType.KEYWORD))
        {
            toReturn = token.getValue() + " " + token.getTokenType().toString();
        }
        else if (isConstValue(token))
        {
            toReturn = "constant value";
        }
        else
        {
            toReturn = token.getTokenType().toString();
        }

        return toReturn;
    }

    public List<Integer> getSteps()
    {
        return steps;
    }
}
