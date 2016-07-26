/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.internals.data_types;

/**
 *
 * @author robert
 */
public class Tuple
{
    private final Data[] values;

    public Tuple(Data[] values)
    {
        this.values = values;
    }

    public int size()
    {
        return values.length;
    }
    
    public Data get(int index)
    {
        return values[index];
    }
}
