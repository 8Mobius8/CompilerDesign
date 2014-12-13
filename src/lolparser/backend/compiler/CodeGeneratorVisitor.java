package lolparser.backend.compiler;

import java.util.ArrayList;

import lolparser.frontend.*;
import lolparser.intermediate.ICodeNode;
import lolparser.intermediate.LolParserVisitorAdapter;
import lolparser.intermediate.icodeimpl.ICodeKeyImpl;

public class CodeGeneratorVisitor extends LolParserVisitorAdapter implements
		LolParserTreeConstants
	{
		/*
		 * data is the string for program name, or... ?
		 * 
		 * 
		 * 
		 * public Object visit(AST_whatever_ node, Object data) {
		 * 
		 * 
		 * 
		 * CodeGenerator.objectFile.println("stuff");
		 * CodeGenerator.objectFile.flush(); return data; }
		 */

		public Object visit(ASTConst node, Object data)
		{
			
			if(node.containsKey(ICodeKeyImpl.VALUE))
				{
					String val = node.getAttribute(ICodeKeyImpl.VALUE).toString();
					return val;
					
					
				}
			return data;
		}
		
		//<meow> :,weo,m: Mew mew (meroow);
		
		public Object visit(ASTStdOut node, Object data)
			{
				Node kid = node.jjtGetChild(0);
				

				out("getstatic java/lang/System/out Ljava/io/PrintStream;");
				if (kid == null)
					{
						out("ldc \"\"");
					} 
				else
					{
						
						out("ldc \"" + kid.jjtAccept(this, data) + "\"");
					}

				out("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");

				return data;
			}

		private static void out(String s)
			{
				CodeGenerator.objectFile.println(s);
				CodeGenerator.objectFile.flush();
			}

	}
