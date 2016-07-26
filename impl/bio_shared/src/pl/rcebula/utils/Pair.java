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
package pl.rcebula.utils;

import java.util.Objects;

/**
 *
 * @author robert
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1, T2>
{
    private T1 left;
    private T2 right;

    public Pair(T1 left, T2 right)
    {
        this.left = left;
        this.right = right;
    }

    public T1 getLeft()
    {
        return left;
    }

    public T2 getRight()
    {
        return right;
    }

    public void setLeft(T1 left)
    {
        this.left = left;
    }

    public void setRight(T2 right)
    {
        this.right = right;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.left);
        hash = 73 * hash + Objects.hashCode(this.right);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>)obj;
        if (!Objects.equals(this.left, other.left))
        {
            return false;
        }
        if (!Objects.equals(this.right, other.right))
        {
            return false;
        }
        return true;
    }
}
