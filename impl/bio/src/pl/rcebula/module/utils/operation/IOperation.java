/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.module.utils.operation;

import pl.rcebula.error_report.ErrorInfo;
import pl.rcebula.internals.data_types.Data;
import pl.rcebula.internals.interpreter.Interpreter;

/**
 *
 * @author beata
 */
public interface IOperation
{
    public Data perform(int[] nums, ErrorInfo[] errorInfos, Interpreter interpreter);
    
    public Data perform(float[] nums, ErrorInfo[] errorInfos, Interpreter interpreter);
    
    public Data perform(boolean[] bools, ErrorInfo[] errorInfos, Interpreter interpreter);
    
    public Data perform(String[] strings, ErrorInfo[] errorInfos, Interpreter interpreter);
}
