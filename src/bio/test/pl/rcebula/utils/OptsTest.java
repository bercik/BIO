/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.utils;

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
public class OptsTest
{
    
    public OptsTest()
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
     * Test of getPassedArgs method, of class Opts.
     */
    @Test
    public void test()
            throws Exception
    {
        System.out.println("OptsTest.test()");
        
        String[] args = { "-d", "input", "-a", "-b" };
        Opts opts = new Opts(args);
        
        assertTrue(opts.isDisassemble());
        assertFalse(opts.isProfiler());
        assertFalse(opts.isRun());
        assertFalse(opts.isTimes());
        
        assertEquals("input", opts.getInputFilePath());
        
        String[] expectedPassedArgs = { "-a", "-b" };
        assertThat(opts.getPassedArgs(), is(expectedPassedArgs));
    }
    
}
