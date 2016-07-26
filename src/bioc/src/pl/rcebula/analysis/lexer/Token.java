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
package pl.rcebula.analysis.lexer;

import java.util.Objects;
import pl.rcebula.error_report.ErrorInfo;

/**
 *
 * @author robert
 * @param <T>
 */
public class Token<T>
{
    private final TokenType tokenType;
    private final T value;
    private final ErrorInfo errorInfo;

    public Token(TokenType tokenType, T value, ErrorInfo errorInfo)
    {
        if (tokenType.getValueType() != null && 
                !tokenType.getValueType().equals(value.getClass()))
        {
            throw new RuntimeException("In Token passed value class and "
                    + "TokenType.getValueType doesn't match");
        }
        
        this.tokenType = tokenType;
        this.value = value;
        this.errorInfo = errorInfo;
    }

    public Token(TokenType tokenType, int i, int i0)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ErrorInfo getErrorInfo()
    {
        return errorInfo;
    }
    
    public TokenType getTokenType()
    {
        return tokenType;
    }

    public T getValue()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.tokenType);
        hash = 29 * hash + Objects.hashCode(this.value);
        hash = 29 * hash + Objects.hashCode(this.errorInfo);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Token<?> other = (Token<?>)obj;
        if (this.tokenType != other.tokenType)
        {
            return false;
        }
        if (!Objects.equals(this.value, other.value))
        {
            return false;
        }
        if (!Objects.equals(this.errorInfo, other.errorInfo))
        {
            return false;
        }
        return true;
    }
}