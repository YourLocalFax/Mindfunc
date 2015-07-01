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

import java.io.IOException;
import java.io.InputStream;

import net.fudev.mindfunc.MfClosure;
import net.fudev.mindfunc.MfState;

/**
 * @author Sekai Kyoretsuna
 */
public class MfCompiler
{
   private InputStream source = null;
   private MfPrototypeBuilder builder = null;
   
   private int input;
   private char token;
   
   public MfCompiler()
   {
   }
   
   public MfClosure compile(final InputStream source)
   {
      return compile(source, new MfState().generateDefaults());
   }
   
   public MfClosure compile(final InputStream source, final MfState state)
   {
      this.source = source;
      builder = new MfPrototypeBuilder();
      
      lex();
      while (input != -1)
      {
         statement();
      }
      
      builder.visitOpEnd();
      
      return new MfClosure(builder.build(), state);
   }
   
   private void statement()
   {
      switch (token)
      {
         case '[':
            visitLoop();
            break;
         case '(':
            visitFnDef();
            break;
         default:
            visitFnCall();
      }
   }
   
   private void lex()
   {
      try
      {
         while (Character.isWhitespace((char) (input = source.read())))
            ;
         token = input > 0 ? (char) input : '\0';
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
   private void expect(final char token)
   {
      if (this.token != token)
      {
         throw new CompilerException("expected " + token + ", got " + this.token);
      }
      lex();
   }
   
   private void visitLoop()
   {
      lex(); // [
      final int jmptoe = builder.visitOpJump(0, 0);
      while (token != ']')
      {
         statement();
      }
      lex(); // ]
      final int jmptob = builder.visitOpJump(1, 0);
      builder.setOpSB(jmptoe, jmptob - jmptoe);
      builder.setOpSB(jmptob, jmptoe - jmptob);
   }
   
   private void visitFnCall()
   {
      final char name = token;
      CUtil.checkValidName(name);
      lex(); // name
      final int localPos = builder.getLocal(name);
      int args = 0;
      while (token == ';')
      {
         lex(); // ;
         args++;
         if (token == '(')
         {
            lex(); // (
            visitFnComp();
            expect(')');
         }
         else
         {
            builder.visitGetFunction(token);
            lex(); // name
         }
      }
      if (localPos == -1)
      {
         builder.visitOpInvokeGlobal(args, name);
      }
      else
      {
         builder.visitOpInvokeLocal(args, localPos);
      }
   }
   
   private void visitFnComp()
   {
      final char name = token;
      CUtil.checkValidName(name);
      builder.visitGetFunction(name);
      lex(); // name
      int args = 0;
      while (token == ';')
      {
         lex(); // ;
         args++;
         if (token == '(')
         {
            lex();
            visitFnComp();
            expect(')');
         }
         else
         {
            builder.visitGetFunction(token);
            lex();
         }
      }
      if (args != 0)
      {
         builder.visitOpComp(args);
      }
   }
   
   private void visitFnDef()
   {
      builder = new MfPrototypeBuilder(builder);
      lex(); // (
      if (token == '(')
      {
         lex();
         while (token != ')')
         {
            builder.addParam(token);
            lex();
         }
         lex(); // ')'
      }
      if (token != ')')
      {
         final char name = token;
         lex();
         while (token != ')')
         {
            statement();
         }
         builder.visitOpEnd();
         lex();
         final int func = builder.parent.addNestedFunction(builder);
         builder = builder.parent;
         builder.visitOpStoreLocal(builder.addLocal(name), func);
      }
      else
      {
         lex();
         builder = builder.parent;
      }
   }
}
