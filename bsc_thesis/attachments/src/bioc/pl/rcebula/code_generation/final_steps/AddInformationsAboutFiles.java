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
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntStringField;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.IntermediateCode;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.Line;
import pl.rcebula.code_generation.intermediate.intermediate_code_structure.StringField;
import pl.rcebula.error_report.MyFiles;
import pl.rcebula.error_report.MyFiles.File;

/**
 *
 * @author robert
 */
public class AddInformationsAboutFiles
{
    private final IntermediateCode ic;
    private final MyFiles files;

    public AddInformationsAboutFiles(IntermediateCode ic, MyFiles files)
    {
        this.ic = ic;
        this.files = files;
        
        analyse();
    }
    
    private void analyse()
    {
        // nowa linia na początku
        ic.insertLine(Line.generateEmptyIntLine(), 0);
        
        for (File f : files.getFiles())
        {
            // dodajemy na początek informację o pliku
            // numer, nazwa, skąd1, skąd2, ..., skądn, nowa_linia
            ic.insertLine(generateLine(f), 0);
        }
    }
    
    private Line generateLine(File f)
    {
        List<IField> fields = new ArrayList<>();
        
        IntStringField fnum = new IntStringField(f.getNum());
        fields.add(fnum);
        StringField fname = new StringField(f.getName());
        fields.add(fname);
        
        for (File from : f.getFromList())
        {
            IntStringField fromField = new IntStringField(from.getNum());
            fields.add(fromField);
        }
        
        fields.add(new IntStringField(0, ""));
        
        return new Line(fields);
    }
}
