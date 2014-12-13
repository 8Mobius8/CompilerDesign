package lolparser.backend.compiler;

import lolparser.backend.compiler.CodeGenerator;
import lolparser.backend.compiler.Instruction;

public class CodeGeneratorHelper
{
  public static void emit(Instruction opcode)
  {
    CodeGenerator.objectFile.println(opcode.toString());
    CodeGenerator.objectFile.flush();
  }
  
  public static void emit(Instruction opcode, String operand)
  {
    CodeGenerator.objectFile.println(opcode.toString()+ "\t" + operand);
    CodeGenerator.objectFile.flush();
  }
  
  public static void emit(Instruction opcode, int operand)
  {
    CodeGenerator.objectFile.println(opcode.toString()+ "\t" + operand);
    CodeGenerator.objectFile.flush();
  }
  
  public static void emit(Instruction opcode, float operand)
  {
    CodeGenerator.objectFile.println(opcode.toString()+ "\t" + operand);
    CodeGenerator.objectFile.flush();
  }
  
  public static void emit(Instruction opcode, int operand1, int operand2)
  {
    CodeGenerator.objectFile.println(opcode.toString()+ "\t" + operand1 + " " +operand2);
    CodeGenerator.objectFile.flush();
  }
  
  public static void emit(Instruction opcode, String operand1, String operand2)
  {
    CodeGenerator.objectFile.println(opcode.toString()+ "\t" + operand1 + " " +operand2);
    CodeGenerator.objectFile.flush();
  }
}
