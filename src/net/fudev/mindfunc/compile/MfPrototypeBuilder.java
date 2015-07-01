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
import java.util.Vector;

import net.fudev.mindfunc.MfPrototype;
import net.fudev.mindfunc.Mindfunc;

/**
 * @author Sekai Kyoretsuna
 */
class MfPrototypeBuilder
{
   private final class Scope
   {
      public final Scope previous;
      
      public int initialLocalsSize;
      
      public Scope(final Scope previous)
      {
         this.previous = previous;
         initialLocalsSize = getLocalsSize();
      }
   }
   
   public final MfPrototypeBuilder parent;
   
   private final List<Integer> bytecode = new Vector<>();
   private final List<MfPrototype> nested = new Vector<>();
   private final List<UpValueInfo> ups = new Vector<>();
   
   private int numParams = 0;
   private int numUpVals = 0;
   
   private final List<UpValueInfo> upvals = new Vector<>();
   private final List<LocalValueInfo> locals = new Vector<>();
   
   private int localsSize = 0;
   private int maxLocalsSize = 0;
   
   public int stackSize = 0;
   private int maxStackSize = 0;
   
   private Scope scope;
   
   public MfPrototypeBuilder()
   {
      this(null);
   }
   
   public MfPrototypeBuilder(final MfPrototypeBuilder parent)
   {
      this.parent = parent;
   }
   
   public MfPrototype build()
   {
      final int[] bytecode = CUtil.listToIntArray(this.bytecode);
      final MfPrototype[] nested = this.nested.toArray(new MfPrototype[this.nested.size()]);
      final UpValueInfo[] ups = this.ups.toArray(new UpValueInfo[this.ups.size()]);
      return new MfPrototype(bytecode, nested, maxLocalsSize, maxStackSize, numParams, ups);
   }
   
   public void beingScope()
   {
      scope = new Scope(scope);
   }
   
   public void endScope()
   {
      final int oldOuters = getNumUpVals();
      if (getLocalsSize() != scope.initialLocalsSize)
      {
         setLocalsSize(scope.initialLocalsSize);
         if (oldOuters != getNumUpVals())
         {
            visitOpClose(scope.initialLocalsSize);
         }
      }
      scope = scope.previous;
   }
   
   public int addNestedFunction(final MfPrototypeBuilder builder)
   {
      nested.add(builder.build());
      return nested.size() - 1;
   }
   
   public int addParam(final char name)
   {
      numParams++;
      return allocLocalVar(name);
   }
   
   private int allocLocalVar(final char name)
   {
      CUtil.checkValidName(name);
      for (final LocalValueInfo var : locals)
      {
         if (var.name == name)
         {
            return var.location;
         }
      }
      final int pos = locals.size();
      locals.add(new LocalValueInfo(name, pos));
      if ((localsSize = pos + 1) > maxLocalsSize)
      {
         if ((maxLocalsSize = localsSize) > Mindfunc.MAX_STACK_SIZE)
         {
            throw new IllegalStateException("compiler error: too many local variables");
         }
      }
      return pos;
   }
   
   public void changeStackSize(final int amt)
   {
      stackSize += amt;
      if (stackSize > maxStackSize)
      {
         if ((maxStackSize = stackSize) > Mindfunc.MAX_STACK_SIZE)
         {
            throw new IllegalStateException("compiler error: too many stack slots");
         }
      }
      else if (stackSize < 0)
      {
         throw new IllegalStateException("stackSize cannot be negative");
      }
   }
   
   public void increaseStackSize()
   {
      changeStackSize(1);
   }
   
   public void decreaseStackSize()
   {
      changeStackSize(-1);
   }
   
   public int addLocal(final char name)
   {
      // Don't check if defined already, we allow it
      return allocLocalVar(name);
   }
   
   public int getLocal(final char name)
   {
      for (final LocalValueInfo var : locals)
      {
         if (var.name == name)
         {
            return var.location;
         }
      }
      return -1;
   }
   
   public char getLocalName(final int local)
   {
      for (final LocalValueInfo var : locals)
      {
         if (var.location == local)
         {
            return var.name;
         }
      }
      return '\0';
   }
   
   public int getUpValue(final char name)
   {
      final int outerSize = upvals.size();
      for (int i = 0; i < outerSize; i++)
      {
         if (upvals.get(i).name == name)
         {
            return i;
         }
      }
      int pos = -1;
      if (parent != null)
      {
         pos = parent.getLocal(name);
         if (pos == -1)
         {
            pos = parent.getUpValue(name);
            if (pos != -1)
            {
               upvals.add(new UpValueInfo(name, pos, UpValueInfo.UP_VALUE));
               return upvals.size() - 1;
            }
         }
         else
         {
            parent.markLocalAsUpValue(pos);
            upvals.add(new UpValueInfo(name, pos, UpValueInfo.LOCAL));
            return upvals.size() - 1;
         }
      }
      return -1;
   }
   
   public char getUpValueName(final int outer)
   {
      for (final UpValueInfo var : upvals)
      {
         if (var.pos == outer)
         {
            return var.name;
         }
      }
      return '\0';
   }
   
   public void markLocalAsUpValue(final int local)
   {
      locals.get(local).endOp = LocalValueInfo.IS_UP_VALUE;
      numUpVals++;
   }
   
   public int getLocalsSize()
   {
      return locals.size();
   }
   
   public void setLocalsSize(final int n)
   {
      int size = locals.size();
      while (size > n)
      {
         size--;
         final LocalValueInfo var = locals.remove(size);
         if (var.endOp == LocalValueInfo.IS_UP_VALUE)
         {
            numUpVals--;
         }
         var.endOp = getCurrentPos();
      }
   }
   
   public void visitGetFunction(final char name)
   {
      CUtil.checkValidName(name);
      final int localPos = getLocal(name);
      if (localPos == -1)
      {
         visitOpGetGlobal(name);
      }
      else
      {
         visitOpLoadLocal(localPos);
      }
   }
   
   public int getNumParams()
   {
      return numParams;
   }
   
   public int getNumUpVals()
   {
      return numUpVals;
   }
   
   public int getCurrentPos()
   {
      return bytecode.size() - 1;
   }
   
   private void visitOp(final int op)
   {
      bytecode.add(op);
   }
   
   private void visitOpA(final int op, final int a)
   {
      bytecode.add(op | ((a & Mindfunc.MAX_A) << Mindfunc.POS_A));
   }
   
   private void visitOpB(final int op, final int b)
   {
      bytecode.add(op | ((b & Mindfunc.MAX_B) << Mindfunc.POS_B));
   }
   
   private void visitOpAB(final int op, final int a, final int b)
   {
      bytecode.add(op | ((a & Mindfunc.MAX_A) << Mindfunc.POS_A) | ((b & Mindfunc.MAX_B) << Mindfunc.POS_B));
   }
   
   private void visitOpASB(final int op, final int a, final int sb)
   {
      bytecode.add(op | ((a & Mindfunc.MAX_A) << Mindfunc.POS_A)
            | (((sb - Mindfunc.MIN_SB) & Mindfunc.MAX_SB) << Mindfunc.POS_B));
   }
   
   public void setOpSB(final int i, final int sb)
   {
      final int op = bytecode.get(i) & ~Mindfunc.MASK_B
            | (((sb - Mindfunc.MIN_SB) & Mindfunc.MAX_B) << Mindfunc.POS_B);
      bytecode.set(i, op);
   }
   
   public int visitOpEnd()
   {
      visitOp(Mindfunc.OP_END);
      return getCurrentPos();
   }
   
   public int visitOpConsumer(final int bNested)
   {
      increaseStackSize();
      visitOpB(Mindfunc.OP_CON, bNested);
      return getCurrentPos();
   }
   
   public int visitOpInvokeGlobal(final int aArguments, final char bName)
   {
      changeStackSize(-aArguments);
      visitOpAB(Mindfunc.OP_IGL, aArguments, (int) bName);
      return getCurrentPos();
   }
   
   public int visitOpInvokeLocal(final int aArguments, final int bLocalPosition)
   {
      changeStackSize(-aArguments);
      visitOpAB(Mindfunc.OP_ILC, aArguments, bLocalPosition);
      return getCurrentPos();
   }
   
   public int visitOpInvokeConsumer(final int aArguments)
   {
      changeStackSize(-aArguments);
      visitOpA(Mindfunc.OP_ICN, aArguments);
      return getCurrentPos();
   }
   
   public int visitOpGetGlobal(final char bName)
   {
      increaseStackSize();
      visitOpB(Mindfunc.OP_GET, (int) bName);
      return getCurrentPos();
   }
   
   public int visitOpStoreLocal(final int aLocalPosition)
   {
      decreaseStackSize();
      visitOpA(Mindfunc.OP_STO, aLocalPosition);
      return getCurrentPos();
   }
   
   public int visitOpLoadLocal(final int aLocalPosition)
   {
      increaseStackSize();
      visitOpA(Mindfunc.OP_LOD, aLocalPosition);
      return getCurrentPos();
   }
   
   public int visitOpJump(final int aJumpCondition, final int sbJumpAmount)
   {
      visitOpASB(Mindfunc.OP_JMP, aJumpCondition, sbJumpAmount);
      return getCurrentPos();
   }
   
   public int visitOpClose(final int bUpValueTop)
   {
      visitOpB(Mindfunc.OP_CLS, bUpValueTop);
      return getCurrentPos();
   }
   
   public int visitOpCompose(final int aArguments)
   {
      changeStackSize(-aArguments);
      visitOpA(Mindfunc.OP_CMP, aArguments);
      return getCurrentPos();
   }
}
