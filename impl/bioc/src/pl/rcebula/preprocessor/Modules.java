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

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author robert
 */
public class Modules
{
    private final Set<String> modulesName = new HashSet<>();

    private final Set<String> defaultModules = new HashSet<String>()
    {
        {
            add("basic");
        }
    };
    
    private static final String modulesPath = "/pl/rcebula/res";
    private static final String moduleExtension = ".xml";

    public Modules()
    {
        // add default modules
        modulesName.addAll(defaultModules);
    }

    public String constructModulePath(String moduleName)
    {
        return modulesPath + "/" + moduleName + moduleExtension;
    }
    
    public void addModule(String moduleName)
    {
        modulesName.add(moduleName);
    }

    public Set<String> getModulesName()
    {
        return modulesName;
    }

    @Override
    public String toString()
    {
        String str = "";
        
        for (String m : modulesName)
        {
            str += m + ", ";
        }
        
        if (str.length() != 0)
        {
            str = str.substring(0, str.length() - 2);
        }
        
        return str;
    }
}
