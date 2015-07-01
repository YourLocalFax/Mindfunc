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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * @author Sekai Kyoretsuna
 */
public class MfState
{
   private final HashMap<Character, MfFunction> globals = new HashMap<>();
   
   final int memSize;
   final int[] memory;
   
   int pointer = 0;
   
   private InputStream input = System.in;
   private PrintStream output = System.out;
   
   public MfState()
   {
      this(65535);
   }
   
   public MfState(final int memSize)
   {
      this.memory = new int[this.memSize = memSize];
   }
   
   /**
    * @param c
    *           The character name of the function.
    * @param func
    */
   public void putFunction(final char c, final MfFunction func)
   {
      globals.put(c, func);
   }
   
   /**
    * @param c
    *           The character name of the function.
    * @return
    */
   public MfFunction getFunction(final char c)
   {
      return globals.get(c);
   }
   
   /**
    * @return this, for chaining.
    */
   public MfState generateDefaults()
   {
      putFunction('>', (f) ->
      {
         pointer = pointer == memSize - 1 ? 0 : pointer + 1;
      });
      putFunction('<', (f) ->
      {
         pointer = pointer == 0 ? memSize - 1 : pointer - 1;
      });
      putFunction('+', (f) ->
      {
         memory[pointer]++;
      });
      putFunction('-', (f) ->
      {
         memory[pointer]--;
      });
      putFunction(',', (f) ->
      {
         try
         {
            final int in = input.read();
            memory[pointer] = in == -1 ? 0 : in;
         }
         catch (final Exception e)
         {
            memory[pointer] = 0;
         }
      });
      putFunction('.', (f) ->
      {
         output.print((char) memory[pointer]);
      });
      putFunction('#', (f) ->
      {
         output.print(memory[pointer]);
      });
      return this;
   }
   
   /**
    * @return The current InputStream this mindfunc state is using.
    */
   public InputStream getInput()
   {
      return input;
   }
   
   /**
    * Sets the current InputStream for this mindfunc state. It cannot be null.
    * 
    * @param input
    *           The desired InputStream.
    */
   public void setInput(final InputStream input)
   {
      if (input == null)
      {
         throw new NullPointerException();
      }
      this.input = input;
   }
   
   /**
    * @return The current PrintStream this mindfunc state is using.
    */
   public PrintStream getOutput()
   {
      return output;
   }
   
   /**
    * Sets the current PrintStream for this mindfunc state. It cannot be null.
    * 
    * @param output
    *           The desired PrintStream.
    */
   public void setOutput(final PrintStream output)
   {
      if (output == null)
      {
         throw new NullPointerException();
      }
      this.output = output;
   }
}
