package lolparser.backend.compiler;

import java.util.ArrayList;

import lolparser.frontend.*;
import lolparser.intermediate.ICodeNode;
import lolparser.intermediate.LolParserVisitorAdapter;
import lolparser.intermediate.SymTabEntry;
import lolparser.intermediate.SymTabFactory;
import lolparser.intermediate.TypeForm;
import lolparser.intermediate.TypeSpec;
import lolparser.intermediate.icodeimpl.ICodeKeyImpl;
import lolparser.intermediate.symtabimpl.SymTabKeyImpl;
import lolparser.intermediate.typeimpl.TypeSpecImpl;
import lolparser.intermediate.symtabimpl.Predefined;
import lolparser.backend.*;

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
		private static int labelCount = 0;

		public Object visit(ASTIdent node, Object data)
			{

				if (data.toString() == "name") { return node.getAttribute(ICodeKeyImpl.VALUE).toString(); }
				if (node.containsKey(ICodeKeyImpl.VALUE))
					{
						// /get the name of the identifier from the tree
						// / look it up in the symbol table

						String val = node.getAttribute(ICodeKeyImpl.VALUE).toString();
						SymTabEntry entry = CodeGenerator.symTabStack.lookupLocal(val);

						String programName = CodeGenerator.programName;
						String fullname = programName + "/" + val;
						TypeSpec type = entry.getTypeSpec();
						String typeStr;

						String suffixes[] =
							{ "i", "f", "z", "s" };
						int suf = 0;
						if (type == Predefined.integerType)
							{
								typeStr = "I";
								suf = 0;
							}
						else if (type == Predefined.realType)
							{
								typeStr = "F";
								suf = 1;
							}
						else if (type == Predefined.booleanType)
							{
								typeStr = "Z";
								suf = 2;
							}
						else
							{
								typeStr = "Ljava/lang/String;";
								suf = 3;
							}
						node.setTypeSpec(type);
						out("getstatic \t" + fullname + suffixes[suf] + " " + typeStr);

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

				///This doesn't generate any jasmin. Is this wrong? Theoretically we would be dealing with local variables
				/// We aren't currently, which will make functions hairy
				return data;
			}

		public Object visit(ASTAssign node, Object data)
			{
				// Put the value of the second child into the symbol table entry for
				// the first
				SimpleNode varNode = (SimpleNode) node.jjtGetChild(0);
				String name = varNode.jjtAccept(this, "name").toString(); //this gets the name of a child identifier
				SimpleNode resultNode = (SimpleNode) node.jjtGetChild(1);
				Object value = resultNode.jjtAccept(this, data);

				assign(name, resultNode.getTypeSpec(), value);

				return data;
			}

		/**
		 * Push a value onto the stack, then call this to assign it to a global
		 * variable
		 * 
		 * @param name
		 *          the name of the variable
		 * @param type
		 *          the type of the variable (or of the thing you are assigning
		 * @return
		 */
		private Boolean assign(String name, TypeSpec type, Object value)
			{
				SymTabEntry entry = CodeGenerator.symTabStack.lookup(name);
				entry.setAttribute(SymTabKeyImpl.DATA_VALUE, value); ///
				entry.setTypeSpec(type);

				String programName = CodeGenerator.programName;
				String fullname = programName + "/" + name;
				String typeStr;

				String suffixes[] =
					{ "i", "f", "z", "s" };
				int suf = 0;
				if (type == Predefined.integerType)
					{
						typeStr = "I";
						suf = 0;
					}
				else if (type == Predefined.realType)
					{
						typeStr = "F";
						suf = 1;
					}
				else if (type == Predefined.booleanType)
					{
						typeStr = "Z";
						suf = 2;
					}
				else
					{
						typeStr = "Ljava/lang/String;";
						suf = 3;
					}

				out("putstatic \t" + fullname + suffixes[suf] + " " + typeStr);

				return true;
			}

		public Object visit(ASTIncrement node, Object data)
			{
				//push current value of object
				//push constant
				//add
				//assign(name of object, type of add (Predefined.integerType or Predefined.realType), value of add)

				return data;
			}

		public Object visit(ASTAdd node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the addition type.
				TypeSpec type = (type0 == Predefined.realType || type1 == Predefined.realType) ? Predefined.realType
						: Predefined.integerType;
				String typePrefix = (type == Predefined.realType) ? "f" : "i";

				// Emit code for the first expression
				// with type conversion if necessary.
				addend0Node.jjtAccept(this, data);
				if ((type == Predefined.realType) && (type0 == Predefined.integerType))
					{
						out("i2f");
					}

				// Emit code for the second expression
				// with type conversion if necessary.
				addend1Node.jjtAccept(this, data);
				if ((type == Predefined.realType) && (type1 == Predefined.integerType))
					{
						out("i2f");
					}

				// Emit the appropriate add instruction.
				out(typePrefix + "add");

				return data;
			}

		public Object visit(ASTSubtract node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the addition type.
				TypeSpec type = (type0 == Predefined.realType || type1 == Predefined.realType) ? Predefined.realType
						: Predefined.integerType;
				String typePrefix = (type == Predefined.realType) ? "f" : "i";

				// Emit code for the first expression
				// with type conversion if necessary.
				addend0Node.jjtAccept(this, data);
				if ((type == Predefined.realType) && (type0 == Predefined.integerType))
					{
						out("i2f");
					}

				// Emit code for the second expression
				// with type conversion if necessary.
				addend1Node.jjtAccept(this, data);
				if ((type == Predefined.realType) && (type1 == Predefined.integerType))
					{
						out("i2f");
					}

				// Emit the appropriate add instruction.
				out(typePrefix + "sub");

				return data;
			}

		public Object visit(ASTMultiply node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the addition type.
				TypeSpec type = (type0 == Predefined.realType || type1 == Predefined.realType) ? Predefined.realType
						: Predefined.integerType;
				String typePrefix = (type == Predefined.realType) ? "f" : "i";

				// Emit code for the first expression
				// with type conversion if necessary.
				addend0Node.jjtAccept(this, data);
				if ((type == Predefined.realType) && (type0 == Predefined.integerType))
					{
						out("i2f");
					}

				// Emit code for the second expression
				// with type conversion if necessary.
				addend1Node.jjtAccept(this, data);
				if ((type == Predefined.realType) && (type1 == Predefined.integerType))
					{
						out("i2f");
					}

				// Emit the appropriate add instruction.
				out(typePrefix + "mul");

				return data;
			}

		public Object visit(ASTDivide node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the addition type.
				TypeSpec type = (type0 == Predefined.realType || type1 == Predefined.realType) ? Predefined.realType
						: Predefined.integerType;
				String typePrefix = (type == Predefined.realType) ? "f" : "i";

				// Emit code for the first expression
				// with type conversion if necessary.
				addend0Node.jjtAccept(this, data);
				if ((type == Predefined.realType) && (type0 == Predefined.integerType))
					{
						out("i2f");
					}

				// Emit code for the second expression
				// with type conversion if necessary.
				addend1Node.jjtAccept(this, data);
				if ((type == Predefined.realType) && (type1 == Predefined.integerType))
					{
						out("i2f");
					}

				// Emit the appropriate add instruction.
				out(typePrefix + "div");

				return data;
			}

		// Split up ASTConst into separate nodes.
		/*
		 * public Object visit(ASTConst node, Object data) {
		 * 
		 * if (node.containsKey(ICodeKeyImpl.VALUE)) { String val =
		 * node.getAttribute(ICodeKeyImpl.VALUE).toString(); if (node.getTypeSpec()
		 * != null && node.getTypeSpec() == Predefined.charType) val =
		 * val.substring(1, val.length() - 1); return val;
		 * 
		 * } return data; }
		 */

		/**
		 * Returns the value of node, casted to type. It remains up to the caller to
		 * update the node's type
		 * 
		 * @param node
		 * @param type
		 * @return
		 */
		private Object cast(SimpleNode node, TypeSpec type)
			{

				Object value = node.getAttribute(ICodeKeyImpl.VALUE);
				if (value == null) { return null; }
				TypeSpec original = node.getTypeSpec();

				if (original == type) { return value; }
				if (original == Predefined.booleanType)
					{
						if (type == Predefined.integerType) { return (Integer) value == 1 ? 1 : 0; } //true -> 1
						if (type == Predefined.realType) { return (Float) value == 1 ? 1 : 0; } //false -> 0
						if (type == Predefined.charType) { return (Integer) value == 1 ? "WIN" : "FAIL"; }
						//handle other types?
					}
				if (original == Predefined.charType)
					{
						if (type == Predefined.integerType)
							{
								try
									{
										return Integer.parseInt((String) value);
									}
								catch (NumberFormatException e)
									{
										return (Integer) 0;
									}
							}
						if (type == Predefined.realType)
							{
								try
									{
										return Double.parseDouble((String) value);
									}
								catch (NumberFormatException e)
									{
										return (Double) 0.0;
									}
							}
						if (type == Predefined.booleanType) { return (String) value == "" ? 0 : 1; }
					}
				if (original == Predefined.integerType)
					{
						if (type == Predefined.realType) { return (Double) value; }
						if (type == Predefined.charType) { return value.toString(); }
						if (type == Predefined.booleanType) { return (Integer) value == 0 ? 0 : 1; }
					}

				if (original == Predefined.realType)
					{
						if (type == Predefined.integerType) { return (Integer) value; }
						if (type == Predefined.charType) { return value.toString(); }
						if (type == Predefined.booleanType) { return (Integer) value == 0 ? 0 : 1; }
					}

				if (original == Predefined.undefinedType)
					{
						if (type == Predefined.booleanType) { return 0; }
						if (type == Predefined.charType) { return ""; }
						if (type == Predefined.integerType) { return (Integer) 0; }
						if (type == Predefined.realType) { return (Double) 0.0; }
					}

				return value;
			}

		public Object visit(ASTConst_Int node, Object data)
			{

				Integer value = (Integer) cast(node, Predefined.integerType);

				node.setTypeSpec(Predefined.integerType);

				CodeGeneratorHelper.emit(Instruction.LDC, value);
				return value;
			}

		public Object visit(ASTConst_Real node, Object data)
			{
				float value = (Float) cast(node, Predefined.realType);

				node.setTypeSpec(Predefined.realType);

				CodeGeneratorHelper.emit(Instruction.LDC, value);
				return value;
			}

		public Object visit(ASTConst_String node, Object data)
			{
				String value = (String) cast(node, Predefined.charType);

				// Type for strings?

				node.setTypeSpec(Predefined.charType);// is this right?

				CodeGeneratorHelper.emit(Instruction.LDC, value);
				return value;
			}

		public Object visit(ASTConst_Bool node, Object data)
			{
				boolean value = (Boolean) node.getAttribute(ICodeKeyImpl.VALUE);

				node.setTypeSpec(Predefined.booleanType);

				CodeGeneratorHelper.emit(Instruction.LDC, value ? 1 : 0); //1 is true, 0 is false

				return value;
			}

		public Object visit(ASTStdIn node, Object data)
			{
				out("getstatic java/lang/System/in Ljava/io/InputStream;");
				out("invokevirtual java/io/InputStream/read()I");
				return data;
			}

		// <meow> :,weo,m: Mew mew (meroow);

		public Object visit(ASTStdOut node, Object data)
			{
				SimpleNode kid = (SimpleNode) node.jjtGetChild(0);

				out("getstatic java/lang/System/out Ljava/io/PrintStream;");
				if (kid == null)
					{
						out("ldc \"\"");
					}
				else
					{
						String val = kid.jjtAccept(this, data).toString();
						val = (String) cast(kid, Predefined.charType);
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
					}

				String typeDescriptor = "";
				TypeSpec type = kid.getTypeSpec();
				if (type == Predefined.integerType)
					{
						typeDescriptor = "I";
					}
				else if (type == Predefined.realType)
					{
						typeDescriptor = "F";
					}
				else if (type == Predefined.booleanType)
					{
						typeDescriptor = "Z";
					}
				else if (type == Predefined.charType)
					{
						typeDescriptor = "Ljava/lang/String;";
					}
				else if (type == Predefined.undefinedType)
					{
						System.err.println("Trying to print undefined");
					}
				out("invokevirtual java/io/PrintStream/println(" + typeDescriptor + ")V");

				return data;
			}

		public Object visit(ASTIf node, Object data)
			{
				ArrayList<ICodeNode> chilln = node.getChildren();

				return data;
			}

		private static void out(String s)
			{
				CodeGenerator.objectFile.println(s);
				CodeGenerator.objectFile.flush();
			}
	}
