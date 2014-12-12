package lolparser.backend.compiler;

import lolparser.frontend.*;
import lolparser.intermediate.LolParserVisitorAdapter;

public class CodeGeneratorVisitor 
extends LolParserVisitorAdapter
implements LolParserTreeConstants
{
  /*
   * data is the string for program name, or... ?
   * 
   * 
   * 
   * public Object visit(AST_whatever_ node, Object data)
   * {
   * 
   * 
   * 
   * CodeGenerator.objectFile.println("stuff");
   * CodeGenerator.objectFile.flush();
   * return data;
   * }
   * 
   */
	
	
	public Object visit(ASTStdOut node, Object data)
	{
		
		
		return data;
	}
	
	
}
