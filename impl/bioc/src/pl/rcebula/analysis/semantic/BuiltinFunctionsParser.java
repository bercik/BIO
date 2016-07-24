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
package pl.rcebula.analysis.semantic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.rcebula.preprocessor.Modules;

/**
 *
 * @author robert
 */
public class BuiltinFunctionsParser
{
    private final List<BuiltinFunction> builtinFunctions = new ArrayList<>();

    public BuiltinFunctionsParser(boolean internal, Modules modules)
            throws IOException, SAXException, ParserConfigurationException
    {
        for (String moduleName : modules.getModulesName())
        {
            InputStream is;

            String path = modules.constructModulePath(moduleName);
            
            if (internal)
            {
                is = readInternalFile(path, "UTF-8");
            }
            else
            {
                is = readExternalFile(path, "UTF-8");
            }
            
            readXMLFile(is);
        }
    }

    private void readXMLFile(InputStream is) 
            throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Load the input XML document, parse it and return an instance of the
        // Document class.
        Document document = builder.parse(is);

        NodeList nodeList = document.getDocumentElement().getElementsByTagName("function");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element elem = (Element)node;

                // Pobierz wartość wszystkich podelementów
                String name = elem.getElementsByTagName("name")
                        .item(0).getChildNodes().item(0).getNodeValue().trim();
                
                // Sprawdź czy ta nazwa się nie powtarza
                if (contains(builtinFunctions, name))
                {
                    // jeżeli tak to rzuć wyjątek
                    String msg = "Function " + name + " in builtin functions XML is already defined";
                    throw new RuntimeException(msg);
                }

                NodeList tmpNL = elem.getElementsByTagName("special");
                boolean special = false;
                if (tmpNL.getLength() > 0)
                {
                    special = Boolean.parseBoolean(tmpNL.item(0)
                            .getChildNodes().item(0).getNodeValue().trim());
                }

                List<ParamType> params = new ArrayList<>();
                List<Boolean> repeatPattern = new ArrayList<>();
                
                Element e = (Element)elem.getElementsByTagName("params").item(0);
                NodeList nl = e.getElementsByTagName("param");
                for (int j = 0; j < nl.getLength(); ++j)
                {
                    Node n = nl.item(j);
                    boolean repeat = false;
                    Node attr = n.getAttributes().getNamedItem("repeat");
                    if (attr != null)
                    {
                        repeat = Boolean.parseBoolean(attr.getNodeValue());
                    }
                    String s = n.getChildNodes().item(0).getNodeValue().trim().toUpperCase();
                    ParamType param = ParamType.valueOf(s);
                    
                    params.add(param);
                    repeatPattern.add(repeat);
                }
                
                builtinFunctions.add(new BuiltinFunction(name, special, params, repeatPattern));
            }
        }
    }
    
    private boolean contains(List<BuiltinFunction> bfs, String name)
    {
        for (BuiltinFunction bf : bfs)
        {
            if (bf.getName().equals(name))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public List<BuiltinFunction> getBuiltinFunctions()
    {
        return builtinFunctions;
    }

    private InputStream readExternalFile(String path, String encoding)
            throws IOException
    {
        String dirPath = System.getProperty("user.dir");
        InputStream is = new FileInputStream(dirPath + "/" + path);
        return is;
    }

    private InputStream readInternalFile(String path, String encoding)
            throws IOException
    {
        InputStream is = getClass().getResourceAsStream(path);
        return is;
    }
}
