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
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author robert
 */
public class Modules
{
    private final Set<Module> modules = new HashSet<>();

    private final Set<Module> defaultModules = new HashSet<Module>()
    {
        {
            add(new Module("arrays"));
            add(new Module("basic"));
            add(new Module("compare"));
            add(new Module("conversion"));
            add(new Module("errors"));
//            add(new Module("floats"));
            add(new Module("ints"));
            add(new Module("io"));
            add(new Module("logic"));
            add(new Module("math"));
            add(new Module("observer"));
            add(new Module("special"));
//            add(new Module("strings"));
            add(new Module("type_check"));
        }
    };
    
    public static final String modulesPath = "/pl/rcebula/res";
    public static final String moduleExtension = ".xml";

    public Modules()
    {
        // add default modules
        modules.addAll(defaultModules);
    }

    public String constructModulePath(String moduleName)
    {
        return modulesPath + "/" + moduleName + moduleExtension;
    }
    
    public void addModule(Module m)
    {
        modules.add(m);
    }

    public Set<Module> getModules()
    {
        return modules;
    }

    @Override
    public String toString()
    {
        String str = "";
        
        for (Module m : modules)
        {
            str += m.toString() + ", ";
        }
        
        if (str.length() != 0)
        {
            str = str.substring(0, str.length() - 2);
        }
        
        return str;
    }
    
    public static class Module
    {
        private final String name;
        private final String file;
        private final int line;

        public Module(String name)
        {
            this.name = name;
            this.file = "";
            this.line = -1;
        }
        
        public Module(String name, String file, int line)
        {
            this.name = name;
            this.file = file;
            this.line = line;
        }

        public String getName()
        {
            return name;
        }

        public String getFile()
        {
            return file;
        }

        public int getLine()
        {
            return line;
        }

        @Override
        public String toString()
        {
            return name;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 59 * hash + Objects.hashCode(this.name);
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            final Module other = (Module)obj;
            if (!Objects.equals(this.name, other.name))
            {
                return false;
            }
            return true;
        }
    }
}
