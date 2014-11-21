package lolparser.intermediate;

import lolparser.frontend.LolParserVisitor;
import lolparser.frontend.SimpleNode;

public class LolParserVisitorAdapter implements LolParserVisitor
{

	@Override
	public Object visit(SimpleNode node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}
	
//    public Object visit(SimpleNode node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
//    
//    public Object visit(ASTcompoundStatement node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
//    
//    public Object visit(ASTassignmentStatement node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
//    
//    public Object visit(ASTadd node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
//    
//    public Object visit(ASTsubtract node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
//
//    public Object visit(ASTmultiply node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
//
//    public Object visit(ASTdivide node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
//
//    public Object visit(ASTvariable node, Object data)
//    {
//         return node.childrenAccept(this, data);
//    }
//
//    public Object visit(ASTintegerConstant node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
//
//    public Object visit(ASTrealConstant node, Object data)
//    {
//        return node.childrenAccept(this, data);
//    }
}
