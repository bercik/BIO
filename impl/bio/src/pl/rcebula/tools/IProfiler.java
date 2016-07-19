/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.tools;

/**
 *
 * @author robert
 */
public interface IProfiler
{
    public void enter(String name);
    
    public void exit(String name);
}
