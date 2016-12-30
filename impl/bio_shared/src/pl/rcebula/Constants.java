/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula;

/**
 *
 * @author robert
 */
public class Constants
{
    public static final String VERSION_STRING = "1.2.0";
    
    public static final String mainFunctionName = "onSTART";
    public static final Integer[] mainFunctionParameters = { 0, 1 };
    
    public static final String unhandledErrorFunctionName = "onUNHANDLED_ERROR";
    public static final String exitFunctionName = "EXIT";
    
    public static final String returnFunctionName = "RETURN";
    
    // rozgranicznik pól
    public static final String fieldsSeparator = ",";
    // rozgranicznik wartości
    public static final String valueSeparator = ":";
}
