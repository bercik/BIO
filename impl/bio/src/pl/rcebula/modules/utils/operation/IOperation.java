/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules.utils.operation;

import pl.rcebula.internals.data_types.Data;

/**
 *
 * @author beata
 */
public interface IOperation
{
    public Data perform(int... nums);
    
    public Data perform(float... nums);
    
    public Data perform(boolean... bools);
    
    public Data perform(String... strings);
}
