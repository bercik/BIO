/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.code;

/**
 *
 * @author robert
 */
public enum IdValueType
{
    ID,
    VAR;

    @Override
    public String toString()
    {
        return name().toLowerCase();
    }
}
