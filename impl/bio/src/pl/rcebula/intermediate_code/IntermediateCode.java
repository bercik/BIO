/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.intermediate_code;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import pl.rcebula.code.InterpreterFunction;
import pl.rcebula.code.ParamType;
import pl.rcebula.intermediate_code.line.CallLine;
import pl.rcebula.intermediate_code.line.ClearStackLine;
import pl.rcebula.intermediate_code.line.JmpLine;
import pl.rcebula.intermediate_code.line.Line;
import pl.rcebula.intermediate_code.line.PopLine;
import pl.rcebula.intermediate_code.line.PushLine;

/**
 *
 * @author robert
 */
public class IntermediateCode
{
    private final Map<String, UserFunction> userFunctions = new HashMap<>();
    private final Logger logger = Logger.getGlobal();

    public IntermediateCode(String path)
            throws IOException
    {
        logger.info("IntermediateCode");
        logger.fine("path: " + path);
        
        DataInputStream dis = new DataInputStream(new FileInputStream(path));
        read(dis);
    }

    private void read(DataInputStream dis)
            throws IOException
    {
        int currentLnr = 0;

        // przeczytaj informacje o funkcjach użytkownikach
        String funName = dis.readUTF();
        while (!funName.equals(""))
        {
            int lnr = dis.readInt();
            int line = dis.readInt();
            int chNum = dis.readInt();
            
            UserFunction uf = new UserFunction(funName, line, chNum);
            userFunctions.put(funName, uf);

            funName = dis.readUTF();
            ++currentLnr;
        }
        ++currentLnr;
        
        // dopóki nie będzie końca pliku
        while (dis.available() > 0)
        {
            // przeczytaj nagłówek funkcji
            // nazwa funkcji
            funName = dis.readUTF();
            // pobierz obiekt związany z funkcją
            UserFunction uf = userFunctions.get(funName);
            // nazwa parametru 1
            String paramName = dis.readUTF();
            // lista parametrów
            List<String> paramsName = new ArrayList<>();
            while (!paramName.equals(""))
            {
                paramsName.add(paramName);
                // nazwa kolejnego parametru
                paramName = dis.readUTF();
            }
            // dodaj parametry
            uf.addParams(paramsName);
            ++currentLnr;
            // linijka w której zaczynamy czytać kod funkcji
            int startLnr = currentLnr;

            // czytaj kod linia po linijce
            byte opcode = dis.readByte();
            while (opcode != 0)
            {
                InterpreterFunction interpreterFunction = InterpreterFunction.getInterpreterFunction(opcode);
                
                int lineNum = -1;
                int chNum = -1;
                Line line = null;
                
                switch (interpreterFunction)
                {
                    case CALL:
                    case CALL_LOC:
                        funName = dis.readUTF();
                        lineNum = dis.readInt();
                        chNum = dis.readInt();
                        line = new CallLine(interpreterFunction, funName, lineNum, chNum);
                        break;
                    case PUSH:
                        byte paramOpcode = dis.readByte();
                        ParamType paramType = ParamType.getParamType(paramOpcode);
                        Object value = null;
                        switch (paramType)
                        {
                            case ID:
                                value = dis.readUTF();
                                lineNum = dis.readInt();
                                chNum = dis.readInt();
                                break;
                            case VAR:
                                value = dis.readUTF();
                                lineNum = dis.readInt();
                                chNum = dis.readInt();
                                break;
                            case INT:
                                value = dis.readInt();
                                lineNum = dis.readInt();
                                chNum = dis.readInt();
                                break;
                            case FLOAT:
                                value = dis.readFloat();
                                lineNum = dis.readInt();
                                chNum = dis.readInt();
                                break;
                            case STRING:
                                value = dis.readUTF();
                                lineNum = dis.readInt();
                                chNum = dis.readInt();
                                break;
                            case BOOL:
                                value = dis.readBoolean();
                                lineNum = dis.readInt();
                                chNum = dis.readInt();
                                break;
                            case NONE:
                                lineNum = dis.readInt();
                                chNum = dis.readInt();
                                break;
                            default:
                                throw new RuntimeException("Don't know what to do with " + paramType.toString());
                        }
                        
                        Param param = new Param(paramType, value);
                        line = new PushLine(interpreterFunction, param, lineNum, chNum);
                        
                        break;
                    case POP:
                    case POPC:
                        int amount = dis.readInt();
                        line = new PopLine(interpreterFunction, amount, lineNum, chNum);
                        break;
                    case JMP:
                    case JMP_IF_FALSE:
                        int dest = dis.readInt();
                        // przesuwamy cel skoku tak jakbyśmy linie dla każdej funkcji numerowali od zera
                        dest -= startLnr;
                        lineNum = dis.readInt();
                        chNum = dis.readInt();
                        line = new JmpLine(interpreterFunction, dest, lineNum, chNum);
                        break;
                    case CLEAR_STACK:
                        line = new ClearStackLine(interpreterFunction, lineNum, chNum);
                        break;
                    default:
                        throw new RuntimeException("Don't know what to do with " + interpreterFunction.toString());
                }
                
                // dodajemy linię do funkcji
                uf.addLine(line);
                
                // wczytujemy kolejny opcode
                opcode = dis.readByte();
                ++currentLnr;
            }
            ++currentLnr;
        }
    }

    public String toStringWithLineNumbers()
    {
        String result = "";
        for (UserFunction uf : userFunctions.values())
        {
            result += uf.toStringWithLineNumbers() + "\n";
        }
        
        return result;
    }
}
