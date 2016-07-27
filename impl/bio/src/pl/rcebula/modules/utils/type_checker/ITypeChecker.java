/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules.utils.type_checker;

import pl.rcebula.internals.data_types.Data;

/**
 *
 * @author robert
 */
public interface ITypeChecker
{
    public boolean isError();

    public Data getError();
}
