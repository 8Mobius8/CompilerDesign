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

	private static final TypeForm String = null;
	private static int labelCount = 0;
	private static String curLabelEnd = null;

	public Object visit(ASTIdent node, Object data)
	{

		if (data.toString() == "name") { return node.getAttribute(
				ICodeKeyImpl.VALUE).toString(); }
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
		CodeGenerator.symTabStack.enterLocal(name);

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
		Object value = resultNode.jjtAccept(this, data);

		SymTabEntry entry = CodeGenerator.symTabStack.lookup(name);
		entry.setAttribute(SymTabKeyImpl.DATA_VALUE, value); // / this may be
																													// unnecessary
		// This may be a type not a value

		entry.setTypeSpec(resultNode.getTypeSpec());

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

		return data;
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

				out("invokestatic java/lang/Math/max("+typePrefix+typePrefix+")"+typePrefix);

				return data;
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

				out("invokestatic java/lang/Math/min("+typePrefix+typePrefix+")"+typePrefix);

				return data;
			}
			
	// Split up ASTConst into separate nodes.
	/*
	 * public Object visit(ASTConst node, Object data) {
	 * 
	 * if (node.containsKey(ICodeKeyImpl.VALUE)) { String val =
	 * node.getAttribute(ICodeKeyImpl.VALUE).toString(); if (node.getTypeSpec() !=
	 * null && node.getTypeSpec() == Predefined.charType) val = val.substring(1,
	 * val.length() - 1); return val;
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
		boolean value = ((String) node.getAttribute(ICodeKeyImpl.VALUE))
				.equals("true") ? true : false;

		node.setTypeSpec(Predefined.booleanType);

		CodeGeneratorHelper.emit(Instruction.LDC, value == true ? 1 : 0);
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
		}

		String typeDescriptor = "";
		TypeSpec type = kid.getTypeSpec();

		if (type != Predefined.charType)
		{
			type = cast(type, Predefined.charType);
			type = cast(type, Predefined.integerType);
			type = cast(type, Predefined.booleanType);
			type = cast(type, Predefined.realType);
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
	public Object visit(ASTIfBlock node, Object data)
	{
		String ifeq = CodeGenerator.makeLabel("ifequal");
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
		out("");
		out("");
		
		if (original == required) { return original; }

		if (required == Predefined.undefinedType)
		{
			System.err.println("Cast to NOOB! Exiting cast");
			return original;
		}

		if (original == Predefined.booleanType) // should be a 0 or 1 (int) on
		// the stack
		{
			String isTrue = CodeGenerator.makeLabel("isEqual");
			String exit = CodeGenerator.makeLabel("exit");
			out("ifeq " + isTrue);
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
				// TODO
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
