package lolparser.backend.compiler;

import java.util.ArrayList;
import java.util.HashMap;

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
		public CodeGeneratorVisitor()
			{
				predefinedTypeCodes.put(Predefined.booleanType, "Z");
				predefinedTypeCodes.put(Predefined.charType, "Ljava/Ljava/lang/String;");
				predefinedTypeCodes.put(Predefined.integerType, "I");
				predefinedTypeCodes.put(Predefined.realType, "F");
				predefinedTypeCodes.put(Predefined.undefinedType, "********Undefined type!**********"); //this has been giving me trouble
			}

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
		private static String curLabelEnd = null;

		private static HashMap<TypeSpec, String> predefinedTypeCodes = new HashMap<TypeSpec, String>();

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
								type = Predefined.charType;
							}
						node.setTypeSpec(type);
						entry.setTypeSpec(type);
						out("getstatic \t" + fullname + suffixes[suf] + " " + typeStr);

						return entry.getTypeSpec(); // return the type
					}
				return data; // should never get here
			}

		public Object visit(ASTVariableDef node, Object data)
			{

				// do check to ensure that child is there? Or assume parser returned
				// valid code?
				String name = node.jjtGetChild(0).jjtAccept(this, "name").toString(); // this
				// gets
				// the
				// name
				// of
				// a
				// child
				// identifier
				CodeGenerator.symTabStack.enterLocal(name).setTypeSpec(Predefined.undefinedType);

				return Predefined.undefinedType;
			}

		public Object visit(ASTAssign node, Object data)
			{
				// Put the value of the second child into the symbol table entry for
				// the first
				SimpleNode varNode = (SimpleNode) node.jjtGetChild(0);
				String name = varNode.jjtAccept(this, "name").toString(); // this gets
				// the name
				// of a
				// child
				// identifier
				SimpleNode resultNode = (SimpleNode) node.jjtGetChild(1);
				TypeSpec value = (TypeSpec) resultNode.jjtAccept(this, data);

				SymTabEntry entry = CodeGenerator.symTabStack.lookup(name);
				entry.setAttribute(SymTabKeyImpl.DATA_VALUE, value); // / this may be
																															// unnecessary
				// This may be a type not a value

				entry.setTypeSpec(value); //TODO fix this

				String programName = CodeGenerator.programName;
				String fullname = programName + "/" + name;
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

				out("putstatic \t" + fullname + suffixes[suf] + " " + typeStr);

				return type;
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

				return type;
			}

		public Object visit(ASTSubtract node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the subtraction type.
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

				// Emit the appropriate sub instruction.
				out(typePrefix + "sub");

				return type;
			}

		public Object visit(ASTMultiply node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the multiplication type.
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

				// Emit the appropriate mul instruction.
				out(typePrefix + "mul");

				return type;
			}

		public Object visit(ASTDivide node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the division type.
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

				// Emit the appropriate div instruction.
				out(typePrefix + "div");

				return type;
			}

		public Object visit(ASTModulus node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);

				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the modulus type.
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

				// Emit the appropriate rem instruction.
				out(typePrefix + "rem");

				return type;
			}

		public Object visit(ASTMax node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the modulus type.
				TypeSpec type = (type0 == Predefined.realType || type1 == Predefined.realType) ? Predefined.realType
						: Predefined.integerType;
				String typePrefix = (type == Predefined.realType) ? "F" : "I";

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

				out("invokestatic java/lang/Math/max(" + typePrefix + typePrefix + ")" + typePrefix);

				return type;
			}

		public Object visit(ASTMin node, Object data)
			{
				SimpleNode addend0Node = (SimpleNode) node.jjtGetChild(0);
				SimpleNode addend1Node = (SimpleNode) node.jjtGetChild(1);

				TypeSpec type0 = addend0Node.getTypeSpec();
				TypeSpec type1 = addend1Node.getTypeSpec();

				// Get the modulus type.
				TypeSpec type = (type0 == Predefined.realType || type1 == Predefined.realType) ? Predefined.realType
						: Predefined.integerType;
				String typePrefix = (type == Predefined.realType) ? "F" : "I";

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

				out("invokestatic java/lang/Math/min(" + typePrefix + typePrefix + ")" + typePrefix);

				return type;
			}

		public Object visit(ASTIncrementByInt node, Object data)
			{
				SimpleNode kid = (SimpleNode) node.jjtGetChild(0);
				TypeSpec type = (TypeSpec) kid.jjtAccept(this, this);
				if (type == Predefined.undefinedType)
					{
						System.err.println("Error! Incrementing a NOOB!");
					}
				String name = (String) kid.jjtAccept(this, "name");
				SymTabEntry entry = CodeGenerator.symTabStack.lookup(name);

				if (type != Predefined.integerType && type != Predefined.realType)
					{
						type = cast(type, Predefined.integerType); //if it's not a number, cast to int
						kid.setTypeSpec(type);
					}
				entry.setTypeSpec(type);

				SimpleNode increm = (SimpleNode) node.jjtGetChild(1);
				TypeSpec incremType = (TypeSpec) increm.jjtAccept(this, this);
				cast(incremType, Predefined.integerType); //convert to int
				//This is increment by int after all
				cast(incremType, type); //cast to proper type (the type of the first child)

				String typeCode = type == Predefined.integerType ? "i" : "f";
				out(typeCode + "add");

				String fullname = CodeGenerator.programName + "/" + name;
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

				//TODO this
				return type;
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
		public Object visit(ASTConst_Int node, Object data)
			{
				int value = (Integer) node.getAttribute(ICodeKeyImpl.VALUE);

				node.setTypeSpec(Predefined.integerType);

				CodeGeneratorHelper.emit(Instruction.LDC, value);
				return Predefined.integerType;
			}

		public Object visit(ASTConst_Real node, Object data)
			{
				float value = (Float) node.getAttribute(ICodeKeyImpl.VALUE);

				node.setTypeSpec(Predefined.realType);

				CodeGeneratorHelper.emit(Instruction.LDC, value);
				return Predefined.realType;
			}

		public Object visit(ASTConst_String node, Object data)
			{
				String value = (String) node.getAttribute(ICodeKeyImpl.VALUE);

				// Type for strings?
				node.setTypeSpec(Predefined.charType);

				CodeGeneratorHelper.emit(Instruction.LDC, value);
				return Predefined.charType;
			}

		public Object visit(ASTConst_Bool node, Object data)
			{
				boolean value = ((String) node.getAttribute(ICodeKeyImpl.VALUE)).equals("true") ? true : false;

				node.setTypeSpec(Predefined.booleanType);

				CodeGeneratorHelper.emit(Instruction.LDC, value == true ? 1 : 0);
				return Predefined.booleanType;
			}

		public Object visit(ASTEquals node, Object data)
			{
				int numKids = node.jjtGetNumChildren();
				if (numKids < 2)
					{
						System.err.println("Not enough children of Equals node!");
					}
				SimpleNode first = (SimpleNode) node.jjtGetChild(0);
				TypeSpec type = (TypeSpec) first.jjtAccept(this, data);

				SimpleNode second = (SimpleNode) node.jjtGetChild(1);
				TypeSpec secondType = (TypeSpec) second.jjtAccept(this, data);

				String typeCode;

				//Comparisons are performed as integer math in the presence of two NUMBRs, 
				//but if either of the expressions are NUMBARs, then floating point math takes over. 
				//Otherwise, there is no automatic casting in the equality, so BOTH SAEM "3" AN 3 is FAIL.

				if (type == secondType)
					{
						typeCode = predefinedTypeCodes.get(type);
						System.err.println(typeCode);
						System.err.println(type == Predefined.charType ? "String" : "Not string!");
						System.err.flush();
						if (typeCode == predefinedTypeCodes.get(Predefined.undefinedType))
							{
								typeCode = predefinedTypeCodes.get(Predefined.charType);
							}
					}
				else if ((type == Predefined.integerType || type == Predefined.realType)
						&& (secondType == Predefined.realType || secondType == Predefined.integerType)) //both number values
					{
						if (type == Predefined.realType || secondType == Predefined.realType)
							{
								typeCode = predefinedTypeCodes.get(Predefined.realType); //either reals
								if (secondType == Predefined.integerType)
									{
										out("i2f"); //cast the top int to a real
									}
								else if (type == Predefined.realType)
									{
										out("swap");
										out("i2f");
									}
							}
						else
							{
								typeCode = predefinedTypeCodes.get(Predefined.integerType); //both integers
							}
					}
				else
					{
						// No typecasting in equals, so all other combinations are FAIL. Need to pop the top two values off 
						//the stack, they could be single- or double-width...

						//Or can they? Is a string pointer a 32 bit value, like an int?
						//That's easier, so I'll assume that for now.
						//TODO figure that out ^^
						out("pop2"); //pop two values from the stack
						CodeGeneratorHelper.emit(Instruction.LDC, 0); //boolean false
						return Predefined.booleanType;
					}
				if (typeCode == "Z")
					{
						typeCode = "I"; // booleans are handled as integers internally, 0 or 1
					}
				if (typeCode == "I")
					{
						typeCode = "i"; //lowercase for this operation
					}
				if (typeCode == predefinedTypeCodes.get(Predefined.charType))
					{
						typeCode = "a";
					}
				//Typecodes are all set, do math

				String compareTrue = CodeGenerator.makeLabel("cmpTrue");
				String compareExit = CodeGenerator.makeLabel("cmpExit");
				if (typeCode == "F")
					{
						out("fcmpl"); //pops two floats, pushes 0 if equal and 1 if (not equal or NAN)
						out("ifne " + compareTrue); //jump to compareTrue if 1 (items are not equal)
					}
				else
					{
						out("if_" + typeCode + "cmpne " + compareTrue); //jump if not equal
					}
				CodeGeneratorHelper.emit(Instruction.LDC, 1); //1 is true for lolcode
				out("goto " + compareExit);
				out(compareTrue + ":");
				CodeGeneratorHelper.emit(Instruction.LDC, 0); //0 is false
				out(compareExit + ":");

				return Predefined.booleanType;
			}

		/**
		 * this code was copied directly from ASTEquals, so please make sure to
		 * change this also if you change that.
		 * 
		 * The only things that changed are the return values.
		 * 
		 */
		public Object visit(ASTNotEquals node, Object data)
			{
				int numKids = node.jjtGetNumChildren();
				if (numKids < 2)
					{
						System.err.println("Not enough children of NotEquals node!");
					}
				SimpleNode first = (SimpleNode) node.jjtGetChild(0);
				TypeSpec type = (TypeSpec) first.jjtAccept(this, data);

				SimpleNode second = (SimpleNode) node.jjtGetChild(1);
				TypeSpec secondType = (TypeSpec) second.jjtAccept(this, data);

				String typeCode;

				//Comparisons are performed as integer math in the presence of two NUMBRs, 
				//but if either of the expressions are NUMBARs, then floating point math takes over. 
				//Otherwise, there is no automatic casting in the equality, so BOTH SAEM "3" AN 3 is FAIL.

				if (type == secondType)
					{
						typeCode = predefinedTypeCodes.get(type);
					}
				else if ((type == Predefined.integerType || type == Predefined.realType)
						&& (secondType == Predefined.realType || secondType == Predefined.integerType)) //both number values
					{
						if (type == Predefined.realType || secondType == Predefined.realType)
							{
								typeCode = predefinedTypeCodes.get(Predefined.realType); //either reals
								if (secondType == Predefined.integerType)
									{
										out("i2f"); //cast the top int to a real
									}
								else if (type == Predefined.realType)
									{
										out("swap");
										out("i2f");
									}
							}
						else
							{
								typeCode = predefinedTypeCodes.get(Predefined.integerType); //both integers
							}
					}
				else
					{
						// No typecasting in equals, so all other combinations are FAIL. Need to pop the top two values off 
						//the stack, they could be single- or double-width...

						//Or can they? Is a string pointer a 32 bit value, like an int?
						//That's easier, so I'll assume that for now.
						//TODO figure that out ^^
						out("pop2"); //pop two values from the stack
						CodeGeneratorHelper.emit(Instruction.LDC, 1); //boolean true
						return Predefined.booleanType;
					}
				if (typeCode == "Z")
					{
						typeCode = "I"; // booleans are handled as integers internally, 0 or 1
					}
				if (typeCode == "I")
					{
						typeCode = "i"; //lowercase for this operation
					}
				if (typeCode == predefinedTypeCodes.get(Predefined.charType))
					{
						typeCode = "a";
					}
				//Typecodes are all set, do math

				String compareTrue = CodeGenerator.makeLabel("cmpTrue");
				String compareExit = CodeGenerator.makeLabel("cmpExit");
				if (typeCode == "F")
					{
						out("fcmpl"); //pops two floats, pushes 0 if equal and 1 if (not equal or NAN)
						out("ifne " + compareTrue); //jump to compareTrue if 1 (items are not equal)
					}
				else
					{
						out("if_" + typeCode + "cmpne " + compareTrue); //jump if not equal
					}
				CodeGeneratorHelper.emit(Instruction.LDC, 0); //0 is false 
				out("goto " + compareExit);
				out(compareTrue + ":");
				CodeGeneratorHelper.emit(Instruction.LDC, 1); //1 is true for lolcode
				out(compareExit + ":");

				return Predefined.booleanType;
			}

		public Object visit(ASTStdIn node, Object data)
			{
				out("getstatic java/lang/System/in Ljava/io/InputStream;");
				out("invokevirtual java/io/InputStream/read()I");
				return data; // where do we put it?
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

				String typeDescriptor = "";
				TypeSpec type = (TypeSpec) kid.jjtAccept(this, data);

				if (type != Predefined.charType)
					{
						type = cast(type, Predefined.charType);
					}

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

		// IF / THEN / ELSE
		//TODO Fix if statements
		//TODO Add else/then statements
		public Object visit(ASTIfBlock node, Object data)
			{
				String ifeq = CodeGenerator.makeLabel("isequal");
				String end = CodeGenerator.makeLabel("ifend");

				SimpleNode literalNode = (SimpleNode) node.jjtGetChild(0);
				literalNode.jjtAccept(this, data); // print bool
				TypeSpec type = literalNode.getTypeSpec();
				if (type != Predefined.booleanType)
					{
						cast(type, Predefined.booleanType);
					}
				// expression
				// out TRUE, for comparison ?? Does ifeq pop two values off the stack?
				out("ifeq " + ifeq); // if true, jump to ifeq label
				if (node.jjtGetNumChildren() >= 3) // if getChild(2) exists
					{
						literalNode = (SimpleNode) node.jjtGetChild(2);
						literalNode.jjtAccept(this, data);
					}
				// elseif goes here?
				out("goto " + end);
				out(ifeq + ":"); // ifeq label
				literalNode = (SimpleNode) node.jjtGetChild(1);
				literalNode.jjtAccept(this, data); // print if block

				out(end + ":");

				return data;
			}

		private TypeSpec cast(TypeSpec original, TypeSpec required)
			{
				// vvvv Human-readable comment for what cast is happening vvvv //
				out("");
				String firstType;
				String secondType;
				if (original == Predefined.booleanType)
					{
						firstType = "TROOF (Z) ";
					}
				else if (original == Predefined.charType)
					{
						firstType = "YARN (String) ";
					}
				else if (original == Predefined.integerType)
					{
						firstType = "NUMBR (I) ";
					}
				else if (original == Predefined.realType)
					{
						firstType = "NUMBAR (F) ";
					}
				else if (original == Predefined.undefinedType)
					{
						firstType = "NOOB (untyped) ";
					}
				else
					{
						firstType = "???";
					}

				if (required == Predefined.booleanType)
					{
						secondType = "TROOF (Z) ";
					}
				else if (required == Predefined.charType)
					{
						secondType = "YARN (String) ";
					}
				else if (required == Predefined.integerType)
					{
						secondType = "NUMBR (I) ";
					}
				else if (required == Predefined.realType)
					{
						secondType = "NUMBAR (F) ";
					}
				else if (required == Predefined.undefinedType)
					{
						secondType = "NOOB (untyped) -- This should not be happening ";
					}
				else
					{
						secondType = "???";
					}
				out(";Casting " + firstType + " to " + secondType);
				// ^^^^ Human-readable comment for what cast is happening ^^^^ //

				if (original == required) { return original; }

				if (required == Predefined.undefinedType)
					{
						System.err.println("Cast to NOOB! Exiting cast");
						return original;
					}

				//If it's undefined, it has not been assigned yet. I think.
				if (original == Predefined.undefinedType)
					{
						if (required == Predefined.integerType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, 0);
							}
						if (required == Predefined.charType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, "");
							}
						if (required == Predefined.booleanType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, 0);
							}
						if (required == Predefined.realType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, 1);
								out("i2f");
							}
						return required; //Assign it and return
					}

				if (original == Predefined.booleanType) // should be a 0 or 1 (int) on
				// the stack
					{
						String isTrue = CodeGenerator.makeLabel("isEqual");
						String exit = CodeGenerator.makeLabel("exit");
						out("ifeq " + isTrue); // jump if 0, which is true in java and false in lol
						if (required == Predefined.charType) // string
							{
								CodeGeneratorHelper.emit(Instruction.LDC, "\"WIN\"");
							}
						else if (required == Predefined.integerType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, 1);
							}
						else if (required == Predefined.realType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, new Double(1.0).toString());
							}
						out("goto " + exit);
						out(isTrue + ":");
						if (required == Predefined.charType) // string
							{
								CodeGeneratorHelper.emit(Instruction.LDC, "\"FAIL\"");
							}
						else if (required == Predefined.integerType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, 0);
							}
						else if (required == Predefined.realType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, new Double(0.0).toString());
							}

						out(exit + ":");
					}

				if (original == Predefined.integerType)
					{
						if (required == Predefined.realType)
							{
								out("i2f");
							}
						if (required == Predefined.charType)
							{
								out("invokestatic   java/lang/Integer/toString(I)Ljava/lang/String;");
							}
						if (required == Predefined.booleanType)
							{
								String isTrue = CodeGenerator.makeLabel("isEqual");
								String exit = CodeGenerator.makeLabel("exit");
								out("ifeq " + isTrue);
								CodeGeneratorHelper.emit(Instruction.LDC, 1);
								out("goto " + exit);
								out(isTrue + ":");
								CodeGeneratorHelper.emit(Instruction.LDC, 0);
								out(exit + ":");
							}
					}

				if (original == Predefined.realType)
					{
						if (required == Predefined.integerType)
							{
								out("f2i");
							}
						if (required == Predefined.charType)
							{
								out("invokestatic   java/lang/Float/toString(F)Ljava/lang/String;");
							}
						if (required == Predefined.booleanType)
							{
								out("f2i");
								return cast(Predefined.integerType, required);
							}
					}
				if (original == Predefined.charType)
					{
						if (required == Predefined.integerType)
							{
								out("invokestatic  java/lang/Integer/parseInt(Ljava/lang/String;)I");
							}
						if (required == Predefined.realType)
							{
								out("invokestatic  java/lang/Float/parseFloat(Ljava/lang/String;)F");
							}
						if (required == Predefined.booleanType)
							{
								CodeGeneratorHelper.emit(Instruction.LDC, "");
								String isTrue = CodeGenerator.makeLabel("isEqual");
								String exit = CodeGenerator.makeLabel("exit");
								out("if_acmpeq " + isTrue);
								CodeGeneratorHelper.emit(Instruction.LDC, 1);
								out("goto " + exit);
								out(isTrue + ":");
								CodeGeneratorHelper.emit(Instruction.LDC, 0);
								out(exit + ":");
							}

					}

				return required;

			}

		// functions are ignored for now
		public Object visit(ASTFunctionDef node, Object data)
			{
				return data;
			}

		// loops
		public Object visit(ASTLoop node, Object data)
			{
				String loopStart = CodeGenerator.makeLabel("loop");
				String endLoop = CodeGenerator.makeLabel("end_loop");

				if (curLabelEnd != null)
					{
						System.err.println("Nested loops! This may get hairy");
					}
				curLabelEnd = endLoop;

				out(loopStart + ":");
				SimpleNode literalNode = (SimpleNode) node.jjtGetChild(0);
				literalNode.jjtAccept(this, data);

				out("goto " + loopStart);
				out(endLoop + ":");

				curLabelEnd = null;
				return data;
			}

		public Object visit(ASTBreak node, Object data)
			{
				out("goto " + curLabelEnd);
				return data;
			}

		private static void out(String s)
			{
				CodeGenerator.objectFile.println(s);
				CodeGenerator.objectFile.flush();
			}
	}
