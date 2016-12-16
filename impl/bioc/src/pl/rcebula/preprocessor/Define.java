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
package pl.rcebula.preprocessor;

import java.util.List;
import pl.rcebula.analysis.lexer.Token;

/**
 *
 * @author robert
 */
public class Define
{
    private final String id;
    private final String expr;
    private final String file;
    private final int line;
    private final int startChars;
    private final String wholeDirective;
    private List<Token<?>> tokens;

    public Define(String id, String expr, String file, int line, int startChars, String wholeDirective)
    {
        this.id = id;
        this.expr = expr;
        this.file = file;
        this.startChars = startChars;
        this.line = line;
        this.wholeDirective = wholeDirective;
    }

    public List<Token<?>> getTokens()
    {
        return tokens;
    }

    public void setTokens(List<Token<?>> tokens)
    {
        this.tokens = tokens;
    }

    public int getStartChars()
    {
        return startChars;
    }
    
    public String getId()
    {
        return id;
    }

    public String getExpr()
    {
        return expr;
    }

    public String getFile()
    {
        return file;
    }

    public int getLine()
    {
        return line;
    }

    public String getWholeDirective()
    {
        return wholeDirective;
    }
}
