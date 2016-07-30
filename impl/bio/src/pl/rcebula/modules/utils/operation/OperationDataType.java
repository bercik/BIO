/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.rcebula.modules.utils.operation;

/**
 *
 * @author beata
 */
public enum OperationDataType
{
    BOOL,
    STRING,
    INT,
    // konwertuj int do float w razie potrzeby
    FLOAT,
    // utrzymuj int dopóki nie natrafisz na float, wtedy float
    NUMBER;
}
