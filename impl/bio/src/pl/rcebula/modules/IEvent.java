/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

/**
 *
 * @author robert
 */
public interface IEvent
{
    // zwraca nazwę zdarzenia
    public String getName();
    
    // zwraca liczbę parametrów przyjmowaną przez funkcję zdarzenia
    public int getNumberOfParameters();
}
