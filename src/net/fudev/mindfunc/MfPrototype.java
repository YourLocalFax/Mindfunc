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

import net.fudev.mindfunc.compile.UpValueInfo;

/**
 * @author Sekai Kyoretsuna
 */
public class MfPrototype
{
   public final int[] bytecode;
   public final MfPrototype[] nested;
   public final int maxLocal;
   public final int maxStack;
   public final int numParams;
   public final UpValueInfo[] ups;
   
   /**
    * @param bytecode
    * @param nested
    * @param maxLocal
    * @param maxStack
    * @param ups
    */
   public MfPrototype(final int[] bytecode, final MfPrototype[] nested, final int maxLocal, final int maxStack, final int numParams,
         final UpValueInfo[] ups)
   {
      this.bytecode = bytecode;
      this.nested = nested;
      this.maxLocal = maxLocal;
      this.maxStack = maxStack;
      this.numParams = numParams;
      this.ups = ups;
   }
}
