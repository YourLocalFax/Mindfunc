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

/**
 * @author Sekai Kyoretsuna
 */
public class CompilerException extends RuntimeException
{
   private static final long serialVersionUID = -825424712170776098L;

   /**
    * 
    */
   public CompilerException()
   {
   }
   
   /**
    * @param message
    */
   public CompilerException(String message)
   {
      super(message);
   }
   
   /**
    * @param cause
    */
   public CompilerException(Throwable cause)
   {
      super(cause);
   }
   
   /**
    * @param message
    * @param cause
    */
   public CompilerException(String message, Throwable cause)
   {
      super(message, cause);
   }
   
   /**
    * @param message
    * @param cause
    * @param enableSuppression
    * @param writableStackTrace
    */
   public CompilerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
   {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}
