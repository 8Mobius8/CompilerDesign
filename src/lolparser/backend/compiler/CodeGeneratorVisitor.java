package lolparser.backend.compiler;

import java.util.ArrayList;

import lolparser.frontend.*;
import lolparser.intermediate.ICodeNode;
import lolparser.intermediate.LolParserVisitorAdapter;
import lolparser.intermediate.SymTabEntry;
import lolparser.intermediate.TypeForm;
import lolparser.intermediate.icodeimpl.ICodeKeyImpl;
import lolparser.intermediate.symtabimpl.SymTabKeyImpl;
import lolparser.intermediate.typeimpl.TypeSpecImpl;
import lolparser.intermediate.symtabimpl.Predefined;

public class CodeGeneratorVisitor extends LolParserVisitorAdapter implements LolParserTreeConstants
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

		private static final TypeForm String = null;

		public Object visit(ASTIdent node, Object data)
			{
				if (data.toString() == "name") { return node.getAttribute(ICodeKeyImpl.VALUE).toString(); }
				if (node.containsKey(ICodeKeyImpl.VALUE))
					{
						// /get the name of the identifier from the tree
						// / look it up in the symbol table

						String val = node.getAttribute(ICodeKeyImpl.VALUE).toString();
						SymTabEntry entry = CodeGenerator.symTabStack.lookupLocal(val);
						return entry.getAttribute(SymTabKeyImpl.DATA_VALUE); // returns the symbol table entry for the variable

					}
				return data; // should never get here
			}

		public Object visit(ASTVariableDef node, Object data)
			{

				// do check to ensure that child is there? Or assume parser returned
				// valid code?
				String name = node.jjtGetChild(0).jjtAccept(this, "name").toString(); //this gets the name of a child identifier
				CodeGenerator.symTabStack.enterLocal(name);

				return data;
			}

		public Object visit(ASTAssign node, Object data)
			{
				// /put the value of the second child into the symbol table entry for
				// the first

				String name = node.jjtGetChild(0).jjtAccept(this, "name").toString(); //this gets the name of a child identifier
				Object value = node.jjtGetChild(1).jjtAccept(this, data);

				SymTabEntry entry = CodeGenerator.symTabStack.enterLocal(name);
				entry.setAttribute(SymTabKeyImpl.DATA_VALUE, value); ///

				return data;
			}

		public Object visit(ASTConst node, Object data)
			{

				if (node.containsKey(ICodeKeyImpl.VALUE))
					{
						String val = node.getAttribute(ICodeKeyImpl.VALUE).toString();
						if (node.getTypeSpec() != null && node.getTypeSpec() == Predefined.charType)
							val = val.substring(1, val.length() - 1);
						return val;

					}
				return data;
			}

		public Object visit(ASTStdIn node, Object data) {
			out("getstatic java/lang/System/in Ljava/io/InputStream;");
			out("invokevirtual java/io/InputStream/read()I");
			return data;
		}

		// <meow> :,weo,m: Mew mew (meroow);

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
						String val = kid.jjtAccept(this, data).toString();
						if (val != null && val.contains("\""))
							{
								int offset = 0;
								do
									{
										int index = val.indexOf("\"", offset);
										if (index == -1)
											{
												break;
											}
										String firstBit = val.substring(0, index);
										String endBit = val.substring(index);
										val = firstBit + "\\" + endBit;
										offset = index + 2;

									} while (val.contains("\""));
							}

						out("ldc \"" + val + "\"");
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
