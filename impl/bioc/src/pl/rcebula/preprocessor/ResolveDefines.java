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
import pl.rcebula.analysis.lexer.TokenType;
import pl.rcebula.analysis.math_log_parser.MathLogParser;
import pl.rcebula.analysis.parser.ParserError;
import pl.rcebula.analysis.semantic.BuiltinFunction;
import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.error_report.MyFiles.File;
import pl.rcebula.utils.Pair;

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
        resolveDefineTokens();
        resolveOutputTokens();
    }
    
    public List<Token<?>> getTokens()
    {
        return outputTokens;
    }
    
    private void resolveOutputTokens()
    {
        for (Token t : tokens)
        {
            // sprawdź czy typu id
            if (t.getTokenType().equals(TokenType.ID))
            {
                // pobierz id
                String id = (String)t.getValue();
                
                // sprawdź czy zgadza się z jakimś definem
                if (definesMap.containsKey(id))
                {
                    // pobierz define
                    Define define = definesMap.get(id);
                    
                    // zapisz error info tokenu
                    ErrorInfo ei = t.getErrorInfo();
                    
                    // pobierz wszystkie tokeny z define
                    List<Token<?>> defineTokens = new ArrayList<>();
                    
                    // ustaw im error info na error info tokenu
                    for (Token dt : define.getTokens())
                    {
                        defineTokens.add(new Token<>(dt.getTokenType(), dt.getValue(), ei));
                    }
                    
                    // dodaj wszystkie tokeny z define do outputTokens
                    outputTokens.addAll(defineTokens);
                }
                // inaczej dodaj
                else
                {
                    outputTokens.add(t);
                }
            }
            // inaczej dodaj
            else
            {
                outputTokens.add(t);
            }
        }
    }
    
    private void resolveDefineTokens() throws PreprocessorError
    {
        for (Define define : definesMap.values())
        {
            resolveDefineToken(define);
        }
    }
    
    private void resolveDefineToken(Define define) throws PreprocessorError
    {
        List<Token<?>> newTokens = _resolveDefineToken(define, define);
        define.setTokens(newTokens);
    }
    
    private List<Token<?>> _resolveDefineToken(Define origDefine, Define currDefine) 
            throws PreprocessorError
    {
        List<Token<?>> tokens = new ArrayList<>();
        
        for (Token t : currDefine.getTokens())
        {
            // sprawdź czy token to nie id
            if (t.getTokenType().equals(TokenType.ID))
            {
                String id = (String)t.getValue();
                // sprawdź czy jest define o takim samym id
                if (definesMap.containsKey(id))
                {
                    Define newDefine = definesMap.get(id);
                    // sprawdź czy nowy define nie jest taki sam jak początkowy
                    if (newDefine.equals(origDefine))
                    {
                        // wyrzuć błąd
                        String msg = "For directive: " + origDefine.getWholeDirective() + 
                                " recursive appeal to same define id: " + id + ", in directive: " + 
                                currDefine.getWholeDirective() + ": ";
                        int currChNum = origDefine.getStartChars() + t.getErrorInfo().getChNum();
                        
                        throw new PreprocessorError(origDefine.getFile(), msg, origDefine.getLine(),
                                currDefine.getFile(), "", currDefine.getLine(), currChNum);
                    }
                    else
                    {
                        // rozwiń w tym miejscu i dodaj do tokenów
                        tokens.addAll(_resolveDefineToken(origDefine, definesMap.get((String)t.getValue())));
                    }
                }
                else
                {
                    // inaczej dodaj do tokenów
                    tokens.add(t);
                }
            }
            // inaczej dodaj do tokenów
            else
            {
                tokens.add(t);
            }
        }
        
        // Prawdopodobnie można tutaj zoptymalizować dodając:
        // currDefine.setTokens(tokens)
        return tokens;
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
        if (checkIfIdExistsInBuiltinFunctions(define.getId()))
        {
            String msg = "Id " + define.getId() + " is builtin function name";
            throw new PreprocessorError(define.getFile(), msg, define.getLine());
        }
        
        try
        {
            Lexer lexer = new Lexer(define.getExpr() + "\n<EOF>", files);
            List<Token<?>> tokens = lexer.getTokens();
            // usuń ostatni token jakim jest <END>
            tokens = tokens.subList(0, tokens.size() - 1);
            MathLogParser mathLogParser = new MathLogParser(tokens, files);
            tokens = mathLogParser.getTokens();
            
            Pair<Boolean, Integer> pbi = checkIfParenthesisMatch(tokens);
            if (!pbi.getLeft())
            {
                int ch = define.getStartChars() + pbi.getRight();
                String msg = "Parenthesis don't match in directive: " + define.getWholeDirective();
                throw new PreprocessorError(define.getFile(), msg, define.getLine(), ch);
            }
            
            define.setTokens(tokens);
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
    
    private Pair<Boolean, Integer> checkIfParenthesisMatch(List<Token<?>> tokens)
    {
        int counter = 0;
        
        for (int i = 0; i < tokens.size(); ++i)
        {
            Token<?> t = tokens.get(i);
            
            if (t.getTokenType().equals(TokenType.OPEN_BRACKET))
            {
                ++counter;
            }
            else if (t.getTokenType().equals(TokenType.CLOSE_BRACKET))
            {
                --counter;
                
                // "ostatni" nawias zamykający
                if (counter <= 0)
                {
                    // sprawdzamy, czy rzeczywiście ostatni
                    if (i != tokens.size() - 1)
                    {
                        // nawias zamykający pojawił się nie na końcu całego wyrażenia
                        return new Pair<>(false, t.getErrorInfo().getChNum());
                    }
                }
            }
        }
        
        // jeżeli counter jest różny od zera, to znaczy, że brakuje nawiasu zamykającego lub otwierającego
        // (wtedy zwróć false)
        if (counter != 0)
        {
            // dodajemy długość tokenu
            Token lastToken = tokens.get(tokens.size() - 1);
            int chNum = lastToken.getErrorInfo().getChNum();
            // jeżeli taki o typie null to dodajemy jeden
            if (TokenType.getTokenTypesWithNullType().contains(lastToken.getTokenType()))
            {
                chNum += 1;
            }
            // inaczej pobieramy wartość, konwertujemy do stringa i pobieramy długość
            else
            {
                chNum += lastToken.getValue().toString().length();
            }
            
            // zwróc
            return new Pair<>(false, chNum);
        }
        // inaczej wszystko O.K.
        else
        {
            return new Pair<>(true, null);
        }
    }
    
    private static void printTokens(List<Token<?>> tokens)
    {
        for (Token<?> t : tokens)
        {
            System.out.println(t);
        }
    }
}
