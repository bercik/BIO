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
package pl.rcebula.code_generation.final_steps;

import java.util.ArrayList;
import java.util.List;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;

/**
 *
 * @author robert
 */
public class AddInformationsAboutModules
{
    private final IntermediateCode ic;
    private final List<String> modulesName;

    public AddInformationsAboutModules(IntermediateCode ic, List<String> modulesName)
    {
        this.ic = ic;
        this.modulesName = modulesName;
        
        analyse();
    }
    
    private void analyse()
    {
        // tworzymy pola
        List<IField> fields = new ArrayList<>();
        
        for (String m : modulesName)
        {
            IField f = new StringField(m);
            fields.add(f);
        }
        
        // wstawiamy pustą linię na początek
        ic.insertLine(Line.generateEmptyStringLine(), 0);
        
        // tworzymy linię i wstawiamy na początek
        Line line = new Line(fields);
        ic.insertLine(line, 0);
    }
}
