/**
 * Copyright (C) 2015 Sekai Kyoretsuna
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package net.fudev.mindfunc.compile;

public final class UpValueInfo
{
   public static final int LOCAL = 0;
   public static final int UP_VALUE = 0;
   
   public final char name;
   public final int pos;
   public int type;
   
   public UpValueInfo(final char name, final int pos, final int type)
   {
      this.name = name;
      this.pos = pos;
      this.type = type;
   }
   
   public UpValueInfo(final UpValueInfo other)
   {
      this(other.name, other.pos, other.type);
   }
   
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + Character.hashCode(name);
      result = prime * result + pos;
      result = prime * result + type;
      return result;
   }
   
   @Override
   public boolean equals(final Object obj)
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
      final UpValueInfo other = (UpValueInfo) obj;
      if (name != other.name)
      {
         return false;
      }
      if (pos != other.pos)
      {
         return false;
      }
      if (type != other.type)
      {
         return false;
      }
      return true;
   }
}
