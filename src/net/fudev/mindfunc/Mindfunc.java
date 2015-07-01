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

/**
 * @author Sekai Kyoretsuna
 */
public final class Mindfunc
{
   public static final int MAX_STACK_SIZE = 256;
   
   public static final int SIZE_OP = 4;
   public static final int SIZE_A = 12;
   public static final int SIZE_B = 16;
   
   public static final int POS_OP = 0;
   public static final int POS_A = SIZE_OP;
   public static final int POS_B = POS_A + SIZE_A;
   
   public static final int MAX_OP = (1 << SIZE_OP) - 1;
   public static final int MAX_A = (1 << SIZE_A) - 1;
   public static final int MAX_B = (1 << SIZE_B) - 1;

   public static final int MAX_SB = MAX_B >>> 1;
   public static final int MIN_SB = -MAX_SB - 1;
   
   public static final int MASK_OP = MAX_OP << POS_OP;
   public static final int MASK_A = MAX_A << POS_A;
   public static final int MASK_B = MAX_B << POS_B;
   
   public static final int MASK_NOP = ~MASK_OP;
   public static final int MASK_NA = ~MASK_A;
   
   public static final int OP_END = 0x0;
   public static final int OP_CON = 0x1;
   public static final int OP_IGL = 0x2;
   public static final int OP_ILC = 0x3;
   public static final int OP_ICN = 0x4;
   public static final int OP_GET = 0x5;
   public static final int OP_STO = 0x6;
   public static final int OP_LOD = 0x7;
   public static final int OP_JMP = 0x8;
   public static final int OP_CLS = 0x9;
   public static final int OP_CMP = 0xA;
   
   private Mindfunc()
   {
   }
}
