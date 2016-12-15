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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pl.rcebula.analysis.lexer.Lexer;
import pl.rcebula.analysis.lexer.LexerError;
import pl.rcebula.analysis.lexer.Token;
import pl.rcebula.analysis.math_log_parser.MathLogParser;
import pl.rcebula.analysis.parser.ParserError;
import pl.rcebula.analysis.semantic.BuiltinFunction;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.error_report.MyFiles.File;

/**
 *
 * @author robert
 */
public class ResolveDefines
{
    private final Map<String, Define> definesMap;
    private final List<Token<?>> tokens;
    private final List<BuiltinFunction> builtinFunctions;
    
    private final List<Token<?>> outputTokens = new ArrayList<>();
    
    // obiekt wymagany dla leksera
    private final MyFiles files = new MyFiles();

    public ResolveDefines(Map<String, Define> definesMap, List<Token<?>> tokens, 
            List<BuiltinFunction> builtinFunctions)
            throws UnsupportedEncodingException, PreprocessorError
    {
        this.definesMap = definesMap;
        this.tokens = tokens;
        this.builtinFunctions = builtinFunctions;
        
        // wypełnij jakimś mockowym plikiem
        File file = files.addFile("mock");
        file.addInterval(new MyFiles.File.Interval(0, Integer.MAX_VALUE));
        
        resolveExpressions();
    }
    
    public List<Token<?>> getTokens()
    {
        return outputTokens;
    }
    
    private void resolveExpressions() throws UnsupportedEncodingException, PreprocessorError
    {
        for (Define define : definesMap.values())
        {
            resolveExpression(define);
        }
    }
    
    private void resolveExpression(Define define) throws UnsupportedEncodingException, PreprocessorError
    {
        // TODELETE
        System.out.println("id: " + define.getId());
        System.out.println("expr: " + define.getExpr());
        
        if (checkIfIdExistsInBuiltinFunctions(define.getId()))
        {
            String msg = "Id " + define.getId() + " is builtin function name";
            throw new PreprocessorError(define.getFile(), msg, define.getLine());
        }
        
        try
        {
            Lexer lexer = new Lexer(define.getExpr() + "\n<EOF>", files);
            List<Token<?>> tokens = lexer.getTokens();
            MathLogParser mathLogParser = new MathLogParser(tokens, files);
            tokens = mathLogParser.getTokens();
            define.setTokens(tokens);
            
            // TODELETE
            printTokens(tokens);
            System.out.println("---------------");
        }
        catch (LexerError | ParserError ex)
        {
            ErrorInfo ei = ex.getErrorInfo();
            int line = define.getLine();
            int chNum = ei.getChNum() + define.getStartChars();
            
            String msg = ex.getOnlyMessage()+ " in directive " + define.getWholeDirective();
            throw new PreprocessorError(define.getFile(), msg, line, chNum);
        }
    }
    
    private boolean checkIfIdExistsInBuiltinFunctions(String id)
    {
        for (BuiltinFunction bf : builtinFunctions)
        {
            if (bf.getName().equals(id) || (bf.getAlias() != null && bf.getAlias().equals(id)))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private static void printTokens(List<Token<?>> tokens)
    {
        for (Token<?> t : tokens)
        {
            System.out.println(t);
        }
    }
}
