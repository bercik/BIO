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
package pl.rcebula.code_generation.intermediate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robert
 */
public class IntermediateCode
{
    private final List<Line> lines = new ArrayList<>();

    public IntermediateCode()
    {
    }
    
    public void insertLine(Line l, int index)
    {
        l.setLine(index);
        lines.add(index, l);
        // przesuń etykiety o 1, które są za dodaną linią
        for (int i = index + 1; i < lines.size(); ++i)
        {
            Line line = lines.get(i);
            line.move(1);
        }
    }
    
    public Line getLine(int index)
    {
        return lines.get(index);
    }
    
    public void removeLine(int index)
    {
        // dodaj wszystkie etykiety z usuwanej linii do linii poniżej
        List<Label> labels = lines.get(index).getLabels();
        // jeżeli usuwamy ostatnią linię która zawiera etykiety to jest to błąd
        if (labels.size() > 0 && index == lines.size() - 1)
        {
            String message = "Deleting last line with labels are prohibited";
            throw new RuntimeException(message);
        }
        
        lines.get(index + 1).addLabels(labels);
        
        lines.remove(index);
        // przesuń etykiety o -1, które są za usuniętą linią
        for (int i = index; i < lines.size(); ++i)
        {
            Line line = lines.get(i);
            line.move(-1);
        }
    }
    
    public int numberOfLines()
    {
        return lines.size();
    }
}
