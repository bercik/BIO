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
package analysis.lexer;

/**
 *
 * @author robert
 * @param <T>
 */
public class Token<T>
{
    private final TokenType tokenType;
    private final T value;
    private final int line;
    private final int ch;

    public Token(TokenType tokenType, T value, int line, int ch)
    {
        if (tokenType.getValueType() != null && 
                !tokenType.getValueType().equals(value.getClass()))
        {
            throw new RuntimeException("In Token passed value class and "
                    + "TokenType.getValueType doesn't match");
        }
        
        this.tokenType = tokenType;
        this.value = value;
        this.line = line;
        this.ch = ch;
    }

    public TokenType getTokenType()
    {
        return tokenType;
    }

    public T getValue()
    {
        return value;
    }

    public int getLine()
    {
        return line;
    }

    public int getCh()
    {
        return ch;
    }
}
