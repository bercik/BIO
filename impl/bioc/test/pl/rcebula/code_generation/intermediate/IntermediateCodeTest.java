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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author robert
 */
public class IntermediateCodeTest
{
    private static IntermediateCode ic;
    private static Label l1; // początkowa
    private static Label l2; // środkowa
    private static Label l3; // końcowa
    
    public IntermediateCodeTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
        // L1
        // line
        // L2
        // line
        // L3
        
        l1 = new Label();
        l2 = new Label();
        l3 = new Label();
        
        ic = new IntermediateCode();
        Line l = new Line();
        l.addLabel(l1);
        ic.insertLine(l, ic.numberOfLines());
        
        ic.insertLine(new Line(), ic.numberOfLines());
        
        l = new Line();
        l.addLabel(l2);
        ic.insertLine(l, ic.numberOfLines());
        
        ic.insertLine(new Line(), ic.numberOfLines());
        
        l = new Line();
        l.addLabel(l3);
        ic.insertLine(l, ic.numberOfLines());
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testLabelsOk()
    {
        System.out.println("testLabelsOk()");
        
        assertEquals(0, l1.getLine());
        assertEquals(2, l2.getLine());
        assertEquals(4, l3.getLine());
    }
    
    @Test
    public void testInsertLineAtTheBeginning()
    {
        System.out.println("testInsertLineAtTheBeginning()");
        
        ic.insertLine(new Line(), 0);
        
        assertEquals(1, l1.getLine());
        assertEquals(3, l2.getLine());
        assertEquals(5, l3.getLine());
    }
    
    @Test
    public void testRemoveLineFromTheBeginning()
    {
        System.out.println("testRemoveLineFromTheBeginning()");
        
        ic.removeLine(0);
        
        assertEquals(0, l1.getLine());
        assertEquals(1, l2.getLine());
        assertEquals(3, l3.getLine());
    }
    
    @Test
    public void testInsertLineAtTheEnd()
    {
        System.out.println("testInsertLineAtTheEnd()");
        
        ic.insertLine(new Line(), ic.numberOfLines());
        
        assertEquals(0, l1.getLine());
        assertEquals(2, l2.getLine());
        assertEquals(4, l3.getLine());
    }
    
    @Test
    public void testRemoveLineFromTheEnd()
    {
        System.out.println("testRemoveLineFromTheEnd()");
        
        boolean catched = false;
        
        try
        {
            ic.removeLine(ic.numberOfLines()-1);
        }
        catch (RuntimeException ex)
        {
            catched = true;
            
            System.out.println(ex.getMessage());
        }
        
        assertEquals(true, catched);
    }
    
    @Test
    public void testInsertLineInTheMiddle()
    {
        System.out.println("testInsertLineInTheMiddle()");
        
        ic.insertLine(new Line(), 2);
        
        assertEquals(0, l1.getLine());
        assertEquals(3, l2.getLine());
        assertEquals(5, l3.getLine());
    }
    
    @Test
    public void testRemoveLineFromTheMiddle()
    {
        System.out.println("testRemoveLineFromTheMiddle()");
        
        ic.removeLine(2);
        
        assertEquals(0, l1.getLine());
        assertEquals(2, l2.getLine());
        assertEquals(3, l3.getLine());
    }
}
