package analysis.lexer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author robert
 */
public class FiniteStateAutomata
{
    // stan początkowy
    private final Integer startState = 0;
    // stany w których dozwolone jest wystąpienie separatora
    private final Integer[] allowSeparatorStates
            = new Integer[]
            {
                startState, 2, 3, 4, 10, 12
            };
    // stany akceptujące końcowe (nie posiadające dalszych przejść)
    private final Integer[] acceptedEndStates
            = new Integer[]
            {
                7, 9, 13, 14, 15, 20
            };
    // słownik stan:typ tokenu (jeżeli typ tokenu to null oznacza to, że
    // dany stan jest specjalny (np. komentarz))
    private final Map<Integer, TokenType> stateTokenTypeMap
            = new HashMap<Integer, TokenType>()
    {
        {
            put(2, TokenType.INT);
            put(3, null); // id, bool, keyword, none
            put(4, TokenType.ID);
            put(7, TokenType.STRING);
            put(9, null); // komentarz
            put(10, TokenType.INT);
            put(12, TokenType.FLOAT);
            put(13, TokenType.COMMA);
            put(14, TokenType.OPEN_BRACKET);
            put(15, TokenType.CLOSE_BRACKET);
            put(20, TokenType.END);
        }
    };
    // stan komentarza
    private final Integer commentState = 9;

    // liczba stanów
    private final Integer states = 21;
    // liczba symboli (maksymalna wartość zwracana przez getCharCol() plus dwa)
    private final Integer symbols = 20;
    // tablica przejść, zawiera informację o następnym stanie dla danej 
    // pary stan-symbol. -1 oznacza zabronione przejście
    private final Integer[][] transitionsTable = new Integer[states][symbols];

    private Integer state;
    private String tokenValue;
    private Integer line;
    private Integer chNum;
    private Integer currentTokenChNum;
    private Integer currentTokenLine;

    public FiniteStateAutomata()
    {
        fillTransitionsTable();
        fullReset();
    }

    // resetuje stan
    private void reset()
    {
        state = 0;
        tokenValue = "";
        currentTokenChNum = chNum;
        currentTokenLine = line;
    }
    
    public final void fullReset()
    {
        line = chNum = 0;
        reset();
    }

    // podanie kolejnego znaku, zwraca parę token lub null i wartość bool
    // informującą czy ostatni znak został zwrócony
    public Pair<Token<?>, Boolean> putChar(char ch) throws LexerError
    {
        if (ch == '\n')
        {
            ++line;
            chNum = 0;
        }
        else
        {
            ++chNum;
        }

        tokenValue += ch;
        Integer col = getCharCol(ch);

        Integer newState = transitionsTable[state][col];

        Token token;
        Boolean retCh;

        if (newState != -1)
        {
            retCh = false;
            state = newState;
            // sprawdź czy stan jest jednym ze stanów akceptujących końcowych
            if (Arrays.asList(acceptedEndStates).contains(state))
            {
                // pobierz typ tokenu dla danego stanu
                TokenType tokenType = stateTokenTypeMap.get(state);
                // sprawdź czy null, jeżeli tak to specjalna procedura obsługi stanu
                if (tokenType == null)
                {
                    if (Objects.equals(state, commentState))
                    {
                        token = null;
                    }
                    else
                    {
                        throw new RuntimeException(
                                "Unknown accepted end state " + state);
                    }
                }
                else
                {
                    switch (tokenType)
                    {
                        case CLOSE_BRACKET:
                        case OPEN_BRACKET:
                        case COMMA:
                        case END:
                            token = new Token(tokenType, null, currentTokenLine, currentTokenChNum);
                            break;
                        case STRING:
                            // usuwamy początkowy i końcowy cudzysłów
                            token = new Token(tokenType, tokenValue.substring(1, tokenValue.length() - 1), 
                                    currentTokenLine, currentTokenChNum);
                            break;
                        default:
                            throw new RuntimeException(
                                    "Unknown accepted end token type " + tokenType);
                    }
                }
                
                reset();
            }
            else
            {
                token = null;
            }
        }
        else
        {
            // jeżeli stan jest stanem pozwalającym na wystąpienie separatora i wystąpił separator
            if (Arrays.asList(allowSeparatorStates).contains(state)
                    && Arrays.asList(Lexer.separators).contains(ch))
            {  
                // jeżeli stan nie jest stanem początkowym
                if (!Objects.equals(state, startState))
                {
                    // zwracamy ostatni znak (separator), usuwamy go z tokenValue
                    retCh = true;
                    if (ch == '\n')
                    {
                        --line;
                    }
                    else
                    {
                        --chNum;
                    }
                    tokenValue = tokenValue.substring(0, tokenValue.length() - 1);
                    
                    TokenType tokenType = stateTokenTypeMap.get(state);
                    
                    // jeżeli null oznacza to, że jesteśmy w stanie id, keyword, none, true lub false
                    if (tokenType == null)
                    {
                        token = recognizeStateID(state, tokenValue, currentTokenLine, currentTokenChNum);
                    }
                    else
                    {
                        switch (tokenType)
                        {
                            case INT:
                                try
                                {
                                    int i = Integer.parseInt(tokenValue);
                                    token = new Token(tokenType, i, currentTokenLine, currentTokenChNum);
                                }
                                catch (NumberFormatException ex)
                                {
                                    throw new LexerError(line, chNum, ex.getMessage());
                                }
                                break;
                            case FLOAT:
                                try
                                {
                                    float f = Float.parseFloat(tokenValue);
                                    token = new Token(tokenType, f, currentTokenLine, currentTokenChNum);
                                }
                                catch (NumberFormatException ex)
                                {
                                    throw new LexerError(line, chNum, ex.getMessage());
                                }
                                break;
                            case ID:
                                token = new Token(tokenType, tokenValue, currentTokenLine, currentTokenChNum);
                                break;
                            default:
                                throw new RuntimeException(
                                        "Unknown allow separator token type " + tokenType);
                        }
                    }
                }
                else
                {
                    retCh = false;
                    token = null;
                }
                
                reset();
            }
            else
            {
                // sprawdzamy czy stan pozwala na wystąpienie jakiegokolwiek znaku (w tym nie znaku)
                newState = transitionsTable[state][getEveryCharCol()];
                if (newState != -1)
                {
                    state = newState;
                    retCh = false;
                    token = null;
                }
                else
                {
                    throw new LexerError(line, chNum, 
                            "Unexpected character " + ch + " in token \"" + 
                                    tokenValue.substring(0, tokenValue.length() - 1) + "\"");
                }
            }
        }

        return new Pair<>(token, retCh);
    }
    
    private Token recognizeStateID(Integer state, String token,
            Integer line, Integer chNum)
    {
        if (Arrays.asList(Lexer.keywords).contains(token))
        {
            return new Token(TokenType.KEYWORD, token, line, chNum);
        }
        if (Arrays.asList(Lexer.trues).contains(token))
        {
            return new Token(TokenType.BOOL, true, line, chNum);
        }
        if (Arrays.asList(Lexer.falses).contains(token))
        {
            return new Token(TokenType.BOOL, false, line, chNum);
        }
        if (Arrays.asList(Lexer.nones).contains(token))
        {
            return new Token(TokenType.NONE, null, line, chNum);
        }

        // jeżeli nic z powyższych to traktujemy jako ID
        return new Token(TokenType.ID, token, line, chNum);
    }

    // wypełnia tablicę przejść
    private void fillTransitionsTable()
    {
        // ustawiamy -1 w całej tablicy przejść
        for (int w = 0; w < states; ++w)
        {
            for (int k = 0; k < symbols; ++k)
            {
                transitionsTable[w][k] = -1;
            }
        }

        // stan 0
        transitionsTable[0][getCharCol('(')] = 14; // nawias otwierający
        transitionsTable[0][getCharCol(')')] = 15; // nawias zamykający
        transitionsTable[0][getCharCol('+')] = 1; // plus
        transitionsTable[0][getCharCol('-')] = 1; // minus
        transitionsTable[0][getCharCol('0')] = 10; // cyfra zero
        transitionsTable[0][getCharCol('1')] = 2; // dowolna cyfra poza zerem
        transitionsTable[0][getCharCol('a')] = 3; // dowolna litera z wyjątkiem 
        // liter n, E, O, F
        transitionsTable[0][getCharCol('_')] = 4; // podkreślnik
        transitionsTable[0][getCharCol('<')] = 16; // znak mniejszości
        transitionsTable[0][getCharCol('"')] = 5; // cudzysłów
        transitionsTable[0][getCharCol(',')] = 13; // przecinek
        transitionsTable[0][getCharCol('%')] = 8; // procent
        transitionsTable[0][getCharCol('E')] = 3; // litera E
        transitionsTable[0][getCharCol('O')] = 3; // litera O
        transitionsTable[0][getCharCol('F')] = 3; // litera F

        // stan 1
        transitionsTable[1][getCharCol('1')] = 2; // dowolna cyfra poza zerem

        // stan 2
        transitionsTable[2][getCharCol('0')] = 2; // cyfra zero
        transitionsTable[2][getCharCol('1')] = 2; // dowolna cyfra poza zerem
        transitionsTable[2][getCharCol('.')] = 11; // kropka

        // stan 3
        transitionsTable[3][getCharCol('0')] = 4; // cyfra zero
        transitionsTable[3][getCharCol('1')] = 4; // dowolna cyfra poza zerem
        transitionsTable[3][getCharCol('a')] = 3; // dowolna litera z wyjątkiem
        transitionsTable[3][getCharCol('_')] = 4; // podkreślnik
        transitionsTable[3][getCharCol('E')] = 3; // litera E
        transitionsTable[3][getCharCol('O')] = 3; // litera O
        transitionsTable[3][getCharCol('F')] = 3; // litera F

        // stan 4
        transitionsTable[4][getCharCol('0')] = 4; // cyfra zero
        transitionsTable[4][getCharCol('1')] = 4; // dowolna cyfra poza zerem
        transitionsTable[4][getCharCol('a')] = 4; // dowolna litera z wyjątkiem
        transitionsTable[4][getCharCol('_')] = 4; // podkreślnik
        transitionsTable[4][getCharCol('E')] = 4; // litera E
        transitionsTable[4][getCharCol('O')] = 4; // litera O
        transitionsTable[4][getCharCol('F')] = 4; // litera F

        // stan 5
        transitionsTable[5][getCharCol('"')] = 7; // cudzysłów
        transitionsTable[5][getCharCol('\\')] = 6; // slash
        transitionsTable[5][getEveryCharCol()] = 5; // co innego

        // stan 6
        transitionsTable[6][getEveryCharCol()] = 5; // wszystko

        // stan 8
        transitionsTable[8][getCharCol('\n')] = 9; // znak nowej linii
        transitionsTable[8][getEveryCharCol()] = 8; // co innego

        // stan 10
        transitionsTable[10][getCharCol('.')] = 11; // kropka
        
        // stan 11
        transitionsTable[11][getCharCol('0')] = 12; // cyfra zero
        transitionsTable[11][getCharCol('1')] = 12; // dowolna cyfra poza zerem

        // stan 12
        transitionsTable[12][getCharCol('0')] = 12; // cyfra zero
        transitionsTable[12][getCharCol('1')] = 12; // dowolna cyfra poza zerem

        // stan 16
        transitionsTable[16][getCharCol('E')] = 17; // litera E

        // stan 17
        transitionsTable[17][getCharCol('O')] = 18; // litera O

        // stan 18
        transitionsTable[18][getCharCol('F')] = 19; // litera F

        // stan 19
        transitionsTable[19][getCharCol('>')] = 20; // znak większości
    }

    // funkcja która mapuje znak na kolumnę w tablicy przejść
    private Integer getCharCol(char ch)
    {
        if (ch == '(')
        {
            return 0;
        }
        if (ch == ')')
        {
            return 1;
        }
        if (ch == '+')
        {
            return 2;
        }
        if (ch == '-')
        {
            return 3;
        }
        if (ch == '0')
        {
            return 4;
        }
        if (Character.isDigit(ch))
        {
            return 5;
        }
        if (ch == '_')
        {
            return 6;
        }
        if (ch == '.')
        {
            return 7;
        }
        if (ch == '<')
        {
            return 8;
        }
        if (ch == '"')
        {
            return 9;
        }
        if (ch == ',')
        {
            return 10;
        }
        if (ch == '\n')
        {
            return 11;
        }
        if (ch == '\\')
        {
            return 12;
        }
        if (ch == '%')
        {
            return 13;
        }
        if (ch == 'E')
        {
            return 14;
        }
        if (ch == 'O')
        {
            return 15;
        }
        if (ch == 'F')
        {
            return 16;
        }
        if (Character.isLetter(ch))
        {
            return 17;
        }
        if (ch == '>')
        {
            return 18;
        }

        return getEveryCharCol();
    }

    // funkcja która zwraca kolumnę dla każdego znaku 
    // (ostatni symbol w tablicy przejść)
    private Integer getEveryCharCol()
    {
        return symbols - 1;
    }
}