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
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.rcebula.Constants;
import pl.rcebula.analysis.tree.Param;
import pl.rcebula.analysis.tree.ProgramTree;
import pl.rcebula.analysis.tree.UserFunction;

/**
 *
 * @author robert
 */
public class SemanticCheckerTest
{
    
    public SemanticCheckerTest()
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
    }
    
    @After
    public void tearDown()
    {
    }

    @Test
    public void testMultipleParametersName()
            throws Exception
    {
        System.out.println("testMultipleParametersName()");
        
        UserFunction uf = new UserFunction("foo", 1, 1);
        uf.addParam(new Param("a", 1, 5));
        uf.addParam(new Param("b", 1, 8));
        uf.addParam(new Param("a", 1, 11));
        
        ProgramTree pt = new ProgramTree();
        pt.addUserFunction(uf);
        
        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        
        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions);
        }
        catch (SemanticError ex)
        {
            catched = true;
            System.out.println(ex.getMessage());
        }
        
        assertEquals(true, catched);
    }
    
    @Test
    public void testUserFunctionRedeclared()
    {
        System.out.println("testUserFunctionRedeclared()");
        
        ProgramTree pt = new ProgramTree();
        
        UserFunction uf = new UserFunction("foo", 1, 1);
        pt.addUserFunction(uf);
        
        uf = new UserFunction("foo2", 2, 1);
        pt.addUserFunction(uf);
        
        uf = new UserFunction("foo", 3, 1);
        pt.addUserFunction(uf);
        
        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        
        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions);
        }
        catch (SemanticError ex)
        {
            System.out.println(ex.getMessage());
            catched = true;
        }
        
        assertEquals(true, catched);
    }
    
    @Test
    public void testBuiltinFunctionRedeclared()
    {
        System.out.println("testBuiltinFunctionRedeclared()");
        
        ProgramTree pt = new ProgramTree();
        
        UserFunction uf = new UserFunction("foo", 1, 1);
        pt.addUserFunction(uf);
        
        uf = new UserFunction("ASSIGN_LOCAL", 2, 1);
        pt.addUserFunction(uf);
        
        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        BuiltinFunction bf = new BuiltinFunction("ASSIGN_LOCAL", false, true);
        builtinFunctions.add(bf);
        
        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions);
        }
        catch (SemanticError ex)
        {
            System.out.println(ex.getMessage());
            catched = true;
        }
        
        assertEquals(true, catched);
    }
    
    @Test
    public void testMainFunctionExists()
    {
        System.out.println("testMainFunctionExists()");
        
        ProgramTree pt = new ProgramTree();
        
        UserFunction uf = new UserFunction("foo", 1, 1);
        pt.addUserFunction(uf);
        
        uf = new UserFunction("foo2", 2, 1);
        pt.addUserFunction(uf);
        
        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        
        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions);
        }
        catch (SemanticError ex)
        {
            System.out.println(ex.getMessage());
            catched = true;
        }
        
        assertEquals(true, catched);
    }
    
    @Test
    public void testMainFunctionGoodNumberOfParameters()
    {
        System.out.println("testMainFunctionGoodNumberOfParameters()");
        
        ProgramTree pt = new ProgramTree();
        
        UserFunction uf = new UserFunction("foo", 1, 1);
        pt.addUserFunction(uf);
        
        uf = new UserFunction(Constants.mainFunctionName, 2, 1);
        uf.addParam(new Param("a", 2, 10));
        pt.addUserFunction(uf);
        
        List<BuiltinFunction> builtinFunctions = new ArrayList<>();
        
        boolean catched = false;
        try
        {
            SemanticChecker sc = new SemanticChecker(pt, builtinFunctions);
        }
        catch (SemanticError ex)
        {
            System.out.println(ex.getMessage());
            catched = true;
        }
        
        assertEquals(true, catched);
    }
}
