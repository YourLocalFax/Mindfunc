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
package net.fudev.mf;

import java.io.IOException;
import java.io.InputStream;

import net.fudev.mindfunc.MfClosure;
import net.fudev.mindfunc.compile.MfCompiler;

/**
 * @author Sekai Kyoretsuna
 */
public final class Test
{
   public static void main(final String[] args) throws IOException
   {
      final int numTests = 4;
      for (int i = 1; i <= numTests; i++)
      {
         execute("test_" + i + ".mf");
         System.out.println();
      }
   }
   
   private static void execute(final String file)
   {
      final InputStream input = Test.class.getResourceAsStream("/" + file);
      
      final MfCompiler compiler = new MfCompiler();
      final MfClosure closure = compiler.compile(input);
      
      closure.invoke();
   }
   
   private Test()
   {
   }
}
