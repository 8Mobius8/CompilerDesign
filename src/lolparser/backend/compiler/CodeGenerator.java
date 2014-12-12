package lolparser.backend.compiler;

import java.util.ArrayList;
import java.io.*;

import lolparser.frontend.*;
import lolparser.intermediate.*;
import lolparser.intermediate.symtabimpl.Predefined;
import lolparser.backend.*;

import static lolparser.intermediate.symtabimpl.SymTabKeyImpl.*;
import static lolparser.intermediate.symtabimpl.DefinitionImpl.*;

/**
 * <p>The code generator for a compiler back end.</p>
 *
 * <p>Copyright (c) 2008 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CodeGenerator extends Backend
{
    private static final int STACK_LIMIT = 16;
        
    static ICode iCode;
    static SymTabStack symTabStack;
    static PrintWriter objectFile;

    /**
     * Process the intermediate code and the symbol table generated by the
     * parser to generate machine-language instructions.
     * @param iCode the intermediate code.
     * @param symTabStack the symbol table stack.
     * @param objectFile the object file path for the generated code.
     * @throws Exception if an error occurred.
     */
    public void process(ICode iCode, SymTabStack symTabStack,
                        String objectFilePath)
        throws Exception
    {
    	CodeGenerator.iCode = iCode;
    	CodeGenerator.symTabStack = symTabStack;
        CodeGenerator.objectFile  = new PrintWriter(objectFilePath);

     // Make the program and method names.
        int start = objectFilePath.lastIndexOf("/") + 1;
        String programName = objectFilePath.substring(start);
        int end = programName.indexOf(".");
        if (end > -1) {
            programName = programName.substring(0, end);
        }
        programName = programName.substring(0, 1).toUpperCase() +
                      programName.substring(1);
        String methodName = programName.substring(0, 1).toLowerCase() +
                            programName.substring(1);
                
        SymTabEntry programId = symTabStack.getProgramId();
        int localsCount =
                (Integer) programId.getAttribute(ROUTINE_LOCALS_COUNT);
        SymTab routineSymTab = 
                (SymTab) programId.getAttribute(ROUTINE_SYMTAB);
        ArrayList<SymTabEntry> locals = routineSymTab.sortedEntries();

        // Generate the program header.
        objectFile.println(".class public " + programName);
        objectFile.println(".super java/lang/Object");
        objectFile.println();

        // Generate code for fields.
        for (SymTabEntry id : locals) {
            Definition defn = id.getDefinition();

            if (defn == VARIABLE) {
                String fieldName = id.getName();
                TypeSpec type = id.getTypeSpec();
                String typeCode = type == Predefined.integerType ? "I" : "F";
                objectFile.println(".field private static " + fieldName + " " + typeCode);
            }
        }
        objectFile.println();

        // Generate the class constructor/
        objectFile.println(".method public <init>()V");
        objectFile.println();
        objectFile.println("  aload_0");
        objectFile.println("  invokenonvirtual  java/lang/Object/<init>()V");
        objectFile.println("  return");
        objectFile.println();
        objectFile.println(".limit locals 11");
        objectFile.println(".limit stack 11");
        objectFile.println(".end method");
        objectFile.println();

        // Generate the main method header.
        objectFile.println(".method public static main([Ljava/lang/String;)V");
        objectFile.println();

        // Visit the parse tree nodes to generate code 
        // for the main method's compound statement.
        CodeGeneratorVisitor codeVisitor = new CodeGeneratorVisitor();
        Node rootNode = iCode.getRoot();
        rootNode.jjtAccept(codeVisitor, programName);
        objectFile.println();

        // May need double/long, which are 2 slots each.
        int localSlots = (localsCount * 2) + 1;

        // Generate the main method epilogue.
        objectFile.println();
        objectFile.println("    return");
        objectFile.println();
        objectFile.println(".limit locals " + localSlots);
        objectFile.println(".limit stack  " + STACK_LIMIT);
        objectFile.println(".end method");
        objectFile.flush();

        CodeGenerator.objectFile.close();   
    }

    public void processFunction(ICode iCode, SymTabStack symTabStack,
                                String objectFilePath, SymTabEntry functionId) throws IOException
    {
        CodeGenerator.iCode       = iCode;
        CodeGenerator.symTabStack.push((SymTab) functionId.getAttribute(ROUTINE_SYMTAB));
        // Open the file in append mode
        CodeGenerator.objectFile  = new PrintWriter(new BufferedWriter(new FileWriter(objectFilePath, true)));

        // Make the function declaration                
        objectFile.println();
        String fName = functionId.getName().toString();
        objectFile.print(".method private static " + fName + "(");

        ArrayList<SymTabEntry> params = (ArrayList<SymTabEntry>) functionId.getAttribute(ROUTINE_PARMS);
        for (SymTabEntry param : params)
            objectFile.print("LVariant;");
        objectFile.println(")LVariant;");
        
        // Visit the parse tree nodes to generate code for this function
        CodeGeneratorVisitor codeVisitor = new CodeGeneratorVisitor();
        Node rootNode = iCode.getRoot();
        rootNode.jjtAccept(codeVisitor, functionId);
        
        // approximate the number of slots needed for local variables
        int localsCount = (Integer) functionId.getAttribute(ROUTINE_LOCALS_COUNT);
        int localSlots = (localsCount * 2) + 1;
        
        // put the IT variable on top of the stack to return
        objectFile.println("invokestatic Util/getMostRecentExpression()LVariant;");
        objectFile.println("areturn");
        
        // Generate the function epilogue
        objectFile.println();
        objectFile.println(".limit locals " + localSlots);
        objectFile.println(".limit stack  " + STACK_LIMIT);
        objectFile.println(".end method");
        objectFile.flush();

        CodeGenerator.objectFile.close();
        CodeGenerator.symTabStack.pop();
    }
}
