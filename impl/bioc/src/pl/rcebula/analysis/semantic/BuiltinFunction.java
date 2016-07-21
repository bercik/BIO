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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author robert
 */
public class BuiltinFunction
{
    private final String name;
    private final boolean special;
    private final List<ParamType> params;
    private final List<ParamType> repeatPatternTypes;

    private final List<Boolean> repeatPattern;

    private final int startParams;
    private final int minParams;
    private final int additionalParams;
    
    private final boolean isRepeated;

    public BuiltinFunction(String name, boolean special, List<ParamType> params)
    {
        this.name = name;
        this.special = special;
        this.params = new ArrayList<>(params);

        Boolean[] rp = new Boolean[params.size()];
        Arrays.fill(rp, false);
        this.repeatPattern = Arrays.asList(rp);
        this.minParams = params.size();
        this.startParams = this.minParams;
        this.additionalParams = 0;
        this.repeatPatternTypes = new ArrayList<>();
        this.isRepeated = false;
    }

    public BuiltinFunction(String name, boolean special, ParamType... params)
    {
        this(name, special, Arrays.asList(params));
    }

    public BuiltinFunction(String name, boolean special, List<ParamType> params, List<Boolean> repeatPattern)
    {
        this.name = name;
        this.special = special;
        this.params = params;
        this.repeatPattern = repeatPattern;

        // sprawdzamy czy repeatPattern jest poprawna, tzn. zawiera tyle samo elementów co params i 
        // wartości true koło siebie
        String message = "Repeat pattern for " + name + " function is bad";
        if (params.size() != repeatPattern.size())
        {
            throw new RuntimeException(message);
        }

        this.repeatPatternTypes = new ArrayList<>();
        
        // 0 - nie ropoczęto sekwencji true
        // 1 - rozpoczęto sekwencję true i ciągle w niej jesteśmy
        // 2 - zakończono sekwencję true
        int state = 0;
        int it = 0;
        int sp = 0;
        for (Boolean b : repeatPattern)
        {
            if (b)
            {
                repeatPatternTypes.add(params.get(it));
            }
            
            if (state == 0 && !b)
            {
                sp++;
            }
            
            if (state == 0 && b)
            {
                state = 1;
            }
            if (state == 1 && !b)
            {
                state = 2;
            }
            if (state == 2 && b)
            {
                throw new RuntimeException(message);
            }
            
            ++it;
        }
        
        this.startParams = sp;

        // policz minimalną ilość parametrów i dodatkową ilość parametrów
        this.additionalParams = repeatPatternTypes.size();
        this.minParams = params.size() - this.additionalParams;
        this.isRepeated = (repeatPatternTypes.size() > 0);
    }

    public boolean isRepeated()
    {
        return isRepeated;
    }
    
    public int getStartParams()
    {
        return startParams;
    }

    public List<ParamType> getRepeatPatternTypes()
    {
        return Collections.unmodifiableList(repeatPatternTypes);
    }

    public List<Integer> getGoodNumberOfParamsList(int howMuch)
    {
        Integer[] tab = new Integer[howMuch];

        for (int i = 0; i < howMuch; ++i)
        {
            tab[i] = minParams + (additionalParams * i);
        }

        return Arrays.asList(tab);
    }

    // sprawdza czy ilość parametrów zgadza się z funkcją
    public boolean isGoodNumberOfParams(int numberOfParams)
    {
        if (numberOfParams < minParams)
        {
            return false;
        }
        
        if (additionalParams > 0)
        {
            return ((numberOfParams - minParams) % (additionalParams) == 0);
        }
        else
        {
            return numberOfParams == minParams;
        }
    }

    public String getName()
    {
        return name;
    }

    public boolean isSpecial()
    {
        return special;
    }

    public List<ParamType> getParams()
    {
        return Collections.unmodifiableList(params);
    }

    public List<Boolean> getRepeatPattern()
    {
        return Collections.unmodifiableList(repeatPattern);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + (this.special ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.params);
        hash = 97 * hash + Objects.hashCode(this.repeatPattern);
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
        final BuiltinFunction other = (BuiltinFunction)obj;
        if (this.special != other.special)
        {
            return false;
        }
        if (!Objects.equals(this.name, other.name))
        {
            return false;
        }
        if (!Objects.equals(this.params, other.params))
        {
            return false;
        }
        if (!Objects.equals(this.repeatPattern, other.repeatPattern))
        {
            return false;
        }
        return true;
    }
}
