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
package net.fudev.mindfunc;

final class UpValue
{
   private MfFunction[] values;
   private int index;
   
   public UpValue(final MfFunction[] stack, final int index)
   {
      values = stack;
      this.index = index;
   }
   
   public @Override String toString()
   {
      return "[" + index + "/" + values.length + "] " + values[index];
   }
   
   public MfFunction getValue()
   {
      return values[index];
   }
   
   public void setValue(final MfFunction value)
   {
      values[index] = value;
   }
   
   public void close()
   {
      final MfFunction[] old = values;
      values = new MfFunction[] { old[index] };
      old[index] = null;
      index = 0;
   }
   
   public int getIndex()
   {
      return index;
   }
}
