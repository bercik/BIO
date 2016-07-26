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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.rcebula.preprocessor.Modules;
import pl.rcebula.preprocessor.Modules.Module;
import pl.rcebula.preprocessor.PreprocessorError;

/**
 *
 * @author robert
 */
public class BuiltinFunctionsParser
{
    private final List<BuiltinFunction> builtinFunctions = new ArrayList<>();
    private final List<String> modulesName = new ArrayList<>();

    private static final String allModule = "all";
    private static final String specialModule = "special";

    public BuiltinFunctionsParser(Modules modules)
            throws IOException, SAXException, ParserConfigurationException, PreprocessorError,
            URISyntaxException
    {
        // jeżeli #IMPORT("all") to wczytujemy wszystkie pliki xml z folderu
        if (modules.getModules().contains(new Module(allModule)))
        {
            URI uri = getClass().getResource(Modules.modulesPath).toURI();
            Path myPath;
            if (uri.getScheme().equals("jar"))
            {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath = fileSystem.getPath(Modules.modulesPath);
            }
            else
            {
                myPath = Paths.get(uri);
            }
            Stream<Path> walk = Files.walk(myPath, 1);
            for (Iterator<Path> it = walk.iterator(); it.hasNext();)
            {
                String path = it.next().toString();

                if (path.endsWith(Modules.moduleExtension))
                {
                    InputStream is = readInternalFile(path);
                    String moduleName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                    // jeżeli nazwa modułu to special to nie dodajemy
                    if (!moduleName.equals(specialModule))
                    {
                        modulesName.add(moduleName);
                    }

                    readXMLFile(is);
                }
            }
        }
        else
        {
            for (Module module : modules.getModules())
            {
                String path = modules.constructModulePath(module.getName());

                InputStream is = readInternalFile(path);

                if (is == null)
                {
                    String message = "Module " + module.getName() + " doesn't exist";
                    throw new PreprocessorError(module.getFile(), message, module.getLine());
                }

                // jeżeli nazwa modułu to special to nie dodajemy
                if (!module.getName().equals(specialModule))
                {
                    modulesName.add(module.getName());
                }

                readXMLFile(is);
            }
        }
    }

    // used only in tests
    public BuiltinFunctionsParser(boolean internal, String... paths)
            throws IOException, SAXException, ParserConfigurationException
    {
        for (String path : paths)
        {
            InputStream is;

            if (internal)
            {
                is = readInternalFile(path);
            }
            else
            {
                is = readExternalFile(path);
            }

            readXMLFile(is);
        }
    }

    public List<String> getModulesName()
    {
        return modulesName;
    }

    private void readXMLFile(InputStream is)
            throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Load the input XML document, parse it and return an instance of the
        // Document class.
        Document document = builder.parse(is);

        readFunctions(document, "function", true, true, true);
        readFunctions(document, "event", false, false, false);
    }

    private void readFunctions(Document document, String tagName, boolean allowRepeat,
            boolean allowSpecial, boolean allowDiffrentParamTypes)
    {
        NodeList nodeList = document.getDocumentElement().getElementsByTagName(tagName);
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

                boolean special = false;
                if (allowSpecial)
                {
                    NodeList tmpNL = elem.getElementsByTagName("special");
                    if (tmpNL.getLength() > 0)
                    {
                        special = Boolean.parseBoolean(tmpNL.item(0)
                                .getChildNodes().item(0).getNodeValue().trim());
                    }
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
                        if (allowRepeat)
                        {
                            repeat = Boolean.parseBoolean(attr.getNodeValue());
                        }
                        else
                        {
                            String message = "In event " + name + " repeat attribute isn't allowed";
                            throw new RuntimeException(message);
                        }
                    }

                    String s = n.getChildNodes().item(0).getNodeValue().trim().toUpperCase();
                    ParamType param = ParamType.valueOf(s);
                    if (!allowDiffrentParamTypes)
                    {
                        if (!param.equals(ParamType.ALL))
                        {
                            String message = "In event " + name + " param type " + param + " isn't allowed";
                            throw new RuntimeException(message);
                        }
                    }

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

    private InputStream readExternalFile(String path)
            throws IOException
    {
        String dirPath = System.getProperty("user.dir");
        InputStream is = new FileInputStream(dirPath + "/" + path);
        return is;
    }

    private InputStream readInternalFile(String path)
            throws IOException
    {
        InputStream is = getClass().getResourceAsStream(path);
        return is;
    }
}
