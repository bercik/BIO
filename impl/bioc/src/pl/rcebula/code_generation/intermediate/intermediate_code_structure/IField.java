/*
 * Copyright (C) 2016 robert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.rcebula.code_generation.intermediate.intermediate_code_structure;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author robert
 */
public interface IField
{
    @Override
    public String toString();
    
    public void writeToBinaryFile(DataOutputStream dos) throws IOException;
    
    default public void frozeForOptimization()
    {
        
    }
    
    default public void moveBeforeOptimization(int shift, int origLine)
    {
        
    }
    
    default public String toStringOptimizationDiffrence()
    {
        return toString();
    }
}