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

		public Object visit(ASTStdOut node, Object data)
			{
				ArrayList<ICodeNode> kids = node.getChildren();

				out("getstatic java/lang/System/out Ljava/io/PrintStream;");
				if (kids == null || kids.isEmpty())
					{
						out("ldc \"\"");
					} else
					{
						ICodeNode firstborn = kids.get(0);
						String val = firstborn.getAttribute(ICodeKeyImpl.VALUE).toString();
						out("ldc " + val + "************");
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
