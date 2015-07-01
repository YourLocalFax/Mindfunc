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

import java.util.List;

/**
 * @author Sekai Kyoretsuna
 */
final class CUtil
{
   public static int[] listToIntArray(final List<Integer> ints)
   {
      final int[] result = new int[ints.size()];
      for (int i = 0; i < result.length; i++)
      {
         result[i] = ints.get(i);
      }
      return result;
   }
   
   public static void checkValidName(final char name)
   {
      switch (name)
      {
         case '(':
         case ')':
         case '[':
         case ']':
         case '{':
         case '}':
         case ';':
            throw new IllegalArgumentException(name + " is not a valid name.");
         default:
      }
   }
   
   private CUtil()
   {
   }
}
