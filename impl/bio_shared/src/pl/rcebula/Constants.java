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
    public static final String mainFunctionName = "onSTART";
    public static final Integer[] mainFunctionParameters = { 0, 2 };
    
    public static final String returnFunctionName = "RETURN";
    
    public static final String doNothingFunctionName = "DN";
    
    public static final String forLoopFunctionName = "FOR";
    public static final String breakFunctionName = "BREAK";
    public static final String continueFunctionName = "CONTINUE";
    
    // rozgranicznik pól
    public static final String fieldsSeparator = ",";
    // rozgranicznik wartości
    public static final String valueSeparator = ":";
}
