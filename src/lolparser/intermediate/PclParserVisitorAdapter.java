package wci.intermediate;

import wci.frontend.ASTadd;
import wci.frontend.ASTsubtract;
import wci.frontend.ASTmultiply;
import wci.frontend.ASTdivide;
import wci.frontend.ASTassignmentStatement;
import wci.frontend.ASTcompoundStatement;
import wci.frontend.ASTintegerConstant;
import wci.frontend.ASTrealConstant;
import wci.frontend.ASTvariable;
import wci.frontend.PclParserVisitor;
import wci.frontend.SimpleNode;

public class PclParserVisitorAdapter implements PclParserVisitor
{
    public Object visit(SimpleNode node, Object data)
    {
        return node.childrenAccept(this, data);
    }
    
    public Object visit(ASTcompoundStatement node, Object data)
    {
        return node.childrenAccept(this, data);
    }
    
    public Object visit(ASTassignmentStatement node, Object data)
    {
        return node.childrenAccept(this, data);
    }
    
    public Object visit(ASTadd node, Object data)
    {
        return node.childrenAccept(this, data);
    }
    
    public Object visit(ASTsubtract node, Object data)
    {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTmultiply node, Object data)
    {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTdivide node, Object data)
    {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTvariable node, Object data)
    {
         return node.childrenAccept(this, data);
    }

    public Object visit(ASTintegerConstant node, Object data)
    {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTrealConstant node, Object data)
    {
        return node.childrenAccept(this, data);
    }
}
