package analysis.lexer;

/**
 *
 * @author robert
 */
public class FiniteStateAutomata
{
    // separatory po których następuje rozpoznanie tokenu
    private final String[] separators = 
            new String[] { "(", ")", " ", "\t", "\n", "%", "," };
    // stany w których dozwolone jest wystąpienie separatora
    private final Integer[] allowSeparatorStates = 
            new Integer[] { 0, 2, 3, 4, 12 };
    // stany w których dozwolone jest wystąpienie stanu nie znaku, czyli
    // jeżeli dla danego znaku nie ma funkcji przejścia to wywoływana jest
    // funkcja przejścia dla każdego znaku
    private final Integer[] allowNotCharStates = 
            new Integer[] { 5, 8, 9 };
    
    private final Integer states = 21;
    private final Integer symbols = 20;
    // tablica przejść, zawiera informację o następnym stanie dla danej 
    // pary stan-symbol. -1 oznacza zabronione przejście
    private final Integer[][] transitionsTable = new Integer[states][symbols];

    public FiniteStateAutomata()
    {
        fillTransitionsTable();
    }
    
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
        transitionsTable[0][getCharCol('1')] = 2; // dowolna cyfra poza zerem
        transitionsTable[0][getCharCol('a')] = 3; // dowolna litera z wyjątkiem 
                                                  // liter n, E, O, F
        transitionsTable[0][getCharCol('_')] = 4; // podkreślnik
        transitionsTable[0][getCharCol('<')] = 16; // znak mniejszości
        transitionsTable[0][getCharCol('"')] = 5; // cudzysłów
        transitionsTable[0][getCharCol(',')] = 13; // przecinek
        transitionsTable[0][getCharCol('n')] = 3; // litera n
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
        transitionsTable[3][getCharCol('n')] = 3; // litera n
        transitionsTable[3][getCharCol('E')] = 3; // litera E
        transitionsTable[3][getCharCol('O')] = 3; // litera O
        transitionsTable[3][getCharCol('F')] = 3; // litera F
        
        // stan 4
        transitionsTable[4][getCharCol('0')] = 4; // cyfra zero
        transitionsTable[4][getCharCol('1')] = 4; // dowolna cyfra poza zerem
        transitionsTable[4][getCharCol('a')] = 4; // dowolna litera z wyjątkiem
        transitionsTable[4][getCharCol('_')] = 4; // podkreślnik
        transitionsTable[4][getCharCol('n')] = 4; // litera n
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
        transitionsTable[8][getCharCol('\\')] = 9; // slash
        transitionsTable[8][getEveryCharCol()] = 8; // co innego
        
        // stan 9
        transitionsTable[9][getCharCol('n')] = 10; // litera n
        transitionsTable[9][getEveryCharCol()] = 8; // co innego
        
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
        if (ch == '(') { return 0; }
        if (ch == ')') { return 1; }
        if (ch == '+') { return 2; }
        if (ch == '-') { return 3; }
        if (ch == '0') { return 4; }
        if (Character.isDigit(ch)) { return 5; }
        if (ch == '_') { return 6; }
        if (ch == '.') { return 7; }
        if (ch == '<') { return 8; }
        if (ch == '"') { return 9; }
        if (ch == ',') { return 10; }
        if (ch == 'n') { return 11; }
        if (ch == '\\') { return 12; }
        if (ch == '%') { return 13; }
        if (ch == 'E') { return 14; }
        if (ch == 'O') { return 15; }
        if (ch == 'F') { return 16; }
        if (Character.isLetter(ch)) { return 17; }
        if (ch == '>') { return 18; }
        
        return getEveryCharCol();
    }
    
    // funkcja która zwraca kolumnę dla każdego znaku 
    // (ostatni symbol w tablicy przejść)
    private Integer getEveryCharCol()
    {
        return symbols - 1;
    }
}
