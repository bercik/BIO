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

    @Test
    public void testOptionalParameter()
            throws Exception
    {
        System.out.println("testOptionalParameter()");
        
        BuiltinFunctionsParser instance
                = new BuiltinFunctionsParser(true, "/pl/rcebula/res/builtin_functions_test_optional.xml");
        
        List<BuiltinFunction> expResult = new ArrayList<BuiltinFunction>()
        {
            {
                add(new BuiltinFunction("IF", true, Arrays.asList(ParamType.ALL, ParamType.CALL, ParamType.CALL),
                        Arrays.asList(false, false, true), true));
            }
        };

        List<BuiltinFunction> result = instance.getBuiltinFunctions();

        assertThat(expResult, is(result));
    }
    
    /**
     * Test of getBuiltinFunctions method, of class BuiltinFunctionsParser.
     */
    @Test
    public void testGetBuiltinFunctions()
            throws Exception
    {
        System.out.println("getBuiltinFunctions");

        BuiltinFunctionsParser instance
                = new BuiltinFunctionsParser(true, "/pl/rcebula/res/builtin_functions_test.xml");

        List<BuiltinFunction> expResult = new ArrayList<BuiltinFunction>()
        {
            {
                add(new BuiltinFunction("ASSIGN_LOCAL", false, ParamType.ID, ParamType.ALL));
                add(new BuiltinFunction("FOR", true, ParamType.CALL, ParamType.ALL, ParamType.CALL));
                add(new BuiltinFunction("BREAK", true));
                add(new BuiltinFunction("ADD", false, Arrays.asList(ParamType.ALL, ParamType.ALL, ParamType.ALL), 
                        Arrays.asList(false, false, true)));
            }
        };

        List<BuiltinFunction> result = instance.getBuiltinFunctions();

        assertThat(expResult, is(result));
    }

    @Test
    public void testBuiltinFunctionsDuplicateName()
            throws Exception
    {
        System.out.println("testBuiltinFunctionsDuplicateName");

        boolean catched = false;
        try
        {
            BuiltinFunctionsParser instance
                    = new BuiltinFunctionsParser(true, "/pl/rcebula/res/builtin_functions_test_duplicate.xml");
        }
        catch (RuntimeException ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertTrue(catched);
    }
    
    @Test
    public void testBadRepeatPattern()
            throws Exception
    {
        System.out.println("testBadRepeatPattern");

        boolean catched = false;
        try
        {
            BuiltinFunctionsParser instance
                    = new BuiltinFunctionsParser(true, "/pl/rcebula/res/builtin_functions_test_bad_repeat_pattern.xml");
        }
        catch (RuntimeException ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertTrue(catched);
    }
    
    @Test
    public void testEventWithRepeat()
            throws Exception
    {
        System.out.println("testEventWithRepeat");

        boolean catched = false;
        try
        {
            BuiltinFunctionsParser instance
                    = new BuiltinFunctionsParser(true, "/pl/rcebula/res/builtin_functions_test_event_repeat.xml");
        }
        catch (RuntimeException ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertTrue(catched);
    }
    
    @Test
    public void testEventWithDiffrentParamType()
            throws Exception
    {
        System.out.println("testEventWithDiffrentParamType");

        boolean catched = false;
        try
        {
            BuiltinFunctionsParser instance
                    = new BuiltinFunctionsParser(true, 
                            "/pl/rcebula/res/builtin_functions_test_event_diffrent_param_type.xml");
        }
        catch (RuntimeException ex)
        {
            System.err.println(ex.getMessage());
            catched = true;
        }

        assertTrue(catched);
    }
}
