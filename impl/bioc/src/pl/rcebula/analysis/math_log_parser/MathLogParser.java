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
package pl.rcebula.analysis.math_log_parser;

import java.util.ArrayList;
import java.util.List;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.math_log_parser.javacc.*;
import pl.rcebula.error_report.MyFiles;

/**
 *
 * @author robert
 */
public class MathLogParser
{
    private final List<Token<?>> tokens;
    private final MyFiles files;
    private final List<Token<?>> newTokens = new ArrayList<>();

    public MathLogParser(List<Token<?>> tokens, MyFiles files)
            throws Exception
    {
        this.tokens = tokens;
        this.files = files;

        process();
    }

    // TODO change throws Exception
    private void process()
            throws Exception
    {
        for (Token t : tokens)
        {
            if (t.getTokenType().equals(TokenType.EXPRESSION))
            {
                List<Token<?>> nTokens = Parser.process(t, files);
                
                newTokens.addAll(nTokens);
            }
            else
            {
                newTokens.add(t);
            }
        }
    }

    public List<Token<?>> getTokens()
    {
        return newTokens;
    }
}
