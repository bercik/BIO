/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules;

import java.util.Map;

/**
 *
 * @author robert
 */
public interface IModule
{
    // zwraca mapę wszystkich funkcji z danego modułu
    public Map<String, IFunction> getFunctions();
}
