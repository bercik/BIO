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
import static org.hamcrest.CoreMatchers.is;
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
public class BuiltinFunctionsParserTest
{
    
    public BuiltinFunctionsParserTest()
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

    /**
     * Test of getBuiltinFunctions method, of class BuiltinFunctionsParser.
     */
    @Test
    public void testGetBuiltinFunctions()
            throws Exception
    {
        System.out.println("getBuiltinFunctions");
        
        BuiltinFunctionsParser instance = 
                new BuiltinFunctionsParser("/pl/rcebula/res/builtin_functions_test.xml", true);
        
        List<BuiltinFunction> expResult = new ArrayList<BuiltinFunction>()
        {{
                    add(new BuiltinFunction("ASSIGN_LOCAL", false, true, 
                            ParamType.ID, ParamType.ALL));
                    add(new BuiltinFunction("FOR", true, true, 
                            ParamType.CALL, ParamType.ALL, ParamType.CALL));
                    add(new BuiltinFunction("BREAK", true, true));
                    add(new BuiltinFunction("ADD", false, false, 
                            ParamType.ALL, ParamType.ALL));
        }};
        
        List<BuiltinFunction> result = instance.getBuiltinFunctions();
        
        assertThat(expResult, is(result));
    }
}
