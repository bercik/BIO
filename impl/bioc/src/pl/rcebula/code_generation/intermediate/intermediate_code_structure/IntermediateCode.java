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
package pl.rcebula.code_generation.intermediate.intermediate_code_structure;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author robert
 */
public class IntermediateCode
{
    private final List<Line> lines = new ArrayList<>();

    // fields for showing diffrence between unoptimized and optimized code
    private List<Line> linesBeforeOptimization = new ArrayList<>();
    private boolean isFrozeForOptimization = false;

    public IntermediateCode()
    {
    }

    public void frozeForOptimization()
    {
        isFrozeForOptimization = true;

        for (Line line : lines)
        {
            line.frozeForOptimization();
            this.linesBeforeOptimization.add(new Line(line));
        }
    }

    public void insertLine(Line l, int index)
    {
        l.setLine(index);
        
        insertLineForOptimizationDiffrence(l, index);
        
        lines.add(index, l);
        // przesuń etykiety o 1, które są za dodaną linią
        for (int i = index + 1; i < lines.size(); ++i)
        {
            Line line = lines.get(i);
            line.move(1);
        }
    }

    public void appendLine(Line l)
    {
        insertLine(l, numberOfLines());
    }

    public Line getLine(int index)
    {
        return lines.get(index);
    }

    public void insertLineForOptimizationDiffrence(Line line, int index)
    {
        if (isFrozeForOptimization)
        {
            // ustaw jako zamrożoną
            line.frozeForOptimization();

            // weź oryginalną linię lini znajdującej się na tym miejscu
            int origLine = lines.get(index).getLineBeforeOptimization();

            // dodaj linię do linii przed optymalizacją i przesuń wszystkie poniżej o jeden
            // przesuń także oryginalną linię o jeden w lines od linii oznaczonej indexem
            Line newLine = new Line(line);
            newLine.markAsAdded();
            linesBeforeOptimization.add(origLine, newLine);
            for (int i = origLine + 1; i < linesBeforeOptimization.size(); ++i)
            {
                linesBeforeOptimization.get(i).move(1);
            }
            
            for (int i = index; i < lines.size(); ++i)
            {
                lines.get(i).moveLineBeforeOptimization(1);
            }
        }
    }
    
    public void removeLineForOptimizationDiffrence(int index)
    {
        if (isFrozeForOptimization)
        {
            // pobierz linię przed optymalizacjami
            int origLine = lines.get(index).getLineBeforeOptimization();
            
            // TODELETE
            System.out.println("origLine: " + origLine);
            System.out.println("currLine: " + index);
            System.out.println("-----------------");
            
            // oznacz jako usuniętą
            linesBeforeOptimization.get(origLine).markAsRemoved();
        }
    }

    public void removeLine(int index)
    {
        removeLineForOptimizationDiffrence(index);

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

    public void removeLineWithLabels(int index)
    {
        removeLineForOptimizationDiffrence(index);

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

    @Override
    public String toString()
    {
        String result = "";

        for (Line l : lines)
        {
            result += l.toString() + "\n";
        }

        return result;
    }

    private List<String> toLines()
    {
        List<String> stringLines = new ArrayList<>();

        for (Line l : lines)
        {
            stringLines.add(l.toString());
        }

        return stringLines;
    }

    public String toStringOptimizationDiffrence()
    {
        if (isFrozeForOptimization)
        {
            String result = "";

            Integer c = 0;
            for (Line l : linesBeforeOptimization)
            {
                result += (l.isRemoved() ? "-" : " ");
                result += (l.isAdded() ? "+" : " ");
                result += "[" + c.toString() + "] " + l.toString() + "\n";
                ++c;
            }

            return result;
        }
        else
        {
            throw new RuntimeException("You must first call frozeForOptimization() method");
        }
    }

    public String toStringWithLinesNumber()
    {
        String result = "";

        Integer c = 0;
        for (Line l : lines)
        {
            result += "[" + c.toString() + "] " + l.toString() + "\n";
            ++c;
        }

        return result;
    }

    public void writeToBinaryFile(DataOutputStream dos)
            throws IOException
    {
        for (Line l : lines)
        {
            l.writeToBinaryFile(dos);
        }
    }

    public void writeToFile(String path) throws IOException
    {
        Path p = Paths.get(path);
        Files.write(p, toLines(), Charset.forName("UTF-8"));
    }

    public void writeToBinaryFile(String path) throws IOException
    {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(path));

        writeToBinaryFile(dos);
    }
}
