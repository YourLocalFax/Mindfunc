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

import java.util.Arrays;

import net.fudev.mindfunc.compile.UpValueInfo;

/**
 * @author Sekai Kyoretsuna
 */
public class MfClosure implements MfFunction
{
   private static UpValue findUpValue(final MfFunction[] locals, final int idx, final UpValue[] openUps)
   {
      final int n = openUps.length;
      for (int i = 0; i < n; i++)
      {
         if (openUps[i] != null && openUps[i].getIndex() == idx)
         {
            return openUps[i];
         }
      }
      for (int i = 0; i < n; i++)
      {
         if (openUps[i] == null)
         {
            return openUps[i] = new UpValue(locals, idx);
         }
      }
      throw new IllegalArgumentException("no space for new upvalue.");
   }
   
   private final MfPrototype proto;
   
   private MfState state;
   private UpValue[] ups = null;
   
   public MfClosure(final MfPrototype proto)
   {
      this(proto, new MfState().generateDefaults());
   }
   
   public MfClosure(final MfPrototype proto, final MfState state)
   {
      this.proto = proto;
      this.state = state;
      ups = new UpValue[proto.maxStack];
   }
   
   @Override
   public void invoke(final MfFunction... args)
   {
      if (args.length != proto.numParams)
      {
         throw new IllegalArgumentException(
               "Wrong number of arguments passed: expected " + proto.numParams + ", got " + args.length);
      }
      final MfFunction[] locals = new MfFunction[proto.maxLocal];
      System.arraycopy(args, 0, locals, 0, args.length);
      exe(locals, new MfFunction[proto.maxStack]);
   }
   
   private void exe(final MfFunction[] locals, final MfFunction[] stack)
   {
      final int[] bytecode = proto.bytecode;
      final MfPrototype[] nested = proto.nested;
      
      final UpValue[] openUps = nested.length > 0 ? new UpValue[stack.length] : null;
      
      int temp = 0;
      
      int top = 0;
      int pc = 0;
      
      loop:
      while (true)
      {
         final int code = bytecode[pc++];
         
         final int op = code & Mindfunc.MAX_OP;
         final int a = code >>> Mindfunc.POS_A & Mindfunc.MAX_A;
         final int b = code >>> Mindfunc.POS_B & Mindfunc.MAX_B;
         
         switch (op)
         {
            case Mindfunc.OP_END:
            {
               break loop;
            }
            case Mindfunc.OP_CON:
            {
               final MfPrototype proto = nested[b];
               final MfClosure closure = new MfClosure(proto);
               final UpValueInfo[] protoUps = proto.ups;
               final int upCount = protoUps.length;
               for (temp = 0; temp < upCount; temp++)
               {
                  if (protoUps[temp].type == UpValueInfo.LOCAL)
                  {
                     closure.ups[temp] = findUpValue(locals, protoUps[temp].pos, openUps);
                  }
                  else
                  {
                     closure.ups[temp] = ups[protoUps[temp].pos];
                  }
               }
               closure.state = state;
               stack[top++] = closure;
               continue;
            }
            case Mindfunc.OP_IGL:
            {
               final MfFunction fn = state.getFunction((char) b);
               if (fn != null)
               {
                  temp = top;
                  fn.invoke(Arrays.copyOfRange(stack, top -= a, temp));
               }
               continue;
            }
            case Mindfunc.OP_ILC:
            {
               final MfFunction fn = locals[b];
               if (fn != null)
               {
                  temp = top;
                  fn.invoke(Arrays.copyOfRange(stack, top -= a, temp));
               }
               continue;
            }
            case Mindfunc.OP_ICN:
            {
               temp = top;
               final MfFunction[] args = Arrays.copyOfRange(stack, top -= a, temp);
               stack[--top].invoke(args);
               continue;
            }
            case Mindfunc.OP_GET:
            {
               stack[top++] = state.getFunction((char) b);
               continue;
            }
            case Mindfunc.OP_STO:
            {
               locals[a] = stack[--top];
               continue;
            }
            case Mindfunc.OP_LOD:
            {
               stack[top++] = locals[a];
               continue;
            }
            case Mindfunc.OP_JMP:
            {
               if (state.memory[state.pointer] == 0 ? a == 0 : a != 0)
               {
                  pc += b + Mindfunc.MIN_SB;
               }
               continue;
            }
            case Mindfunc.OP_CLS:
            {
               for (temp = openUps.length; --temp >= b;)
               {
                  if (openUps[temp] != null)
                  {
                     openUps[temp].close();
                     openUps[temp] = null;
                  }
               }
               continue;
            }
            case Mindfunc.OP_CMP:
            {
               final MfFunction[] comps = Arrays.copyOfRange(stack, top - a, top);
               final MfFunction func = stack[top = top - a - 1];
               stack[top++] = new MfComposition(func, comps);
               continue;
            }
         }
      }
      if (openUps != null)
      {
         for (temp = openUps.length; --temp >= 0;)
         {
            if (openUps[temp] != null)
            {
               openUps[temp].close();
            }
         }
      }
   }
   
   /**
    * @return the state
    */
   public MfState getState()
   {
      return state;
   }
   
   /**
    * @param state
    *           the state to set
    */
   public void setState(final MfState state)
   {
      if (state == null)
      {
         throw new NullPointerException();
      }
      this.state = state;
   }
}
