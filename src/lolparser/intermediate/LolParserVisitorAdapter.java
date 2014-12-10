package lolparser.intermediate;

import lolparser.frontend.ASTAssignmentExpression;
import lolparser.frontend.ASTBIGGR;
import lolparser.frontend.ASTBOTH;
import lolparser.frontend.ASTBOTHSAEM;
import lolparser.frontend.ASTBlock;
import lolparser.frontend.ASTBooleanOperation;
import lolparser.frontend.ASTComparisionOperation;
import lolparser.frontend.ASTDIFF;
import lolparser.frontend.ASTDIFFRINT;
import lolparser.frontend.ASTDecrement;
import lolparser.frontend.ASTEITHER;
import lolparser.frontend.ASTFunctionDeclare;
import lolparser.frontend.ASTIncrement;
import lolparser.frontend.ASTMOD;
import lolparser.frontend.ASTNOT;
import lolparser.frontend.ASTPRODUKT;
import lolparser.frontend.ASTQUOSHUNT;
import lolparser.frontend.ASTSMALLR;
import lolparser.frontend.ASTSUM;
import lolparser.frontend.ASTSpecialOps;
import lolparser.frontend.ASTUP;
import lolparser.frontend.ASTVarDeclareExpression;
import lolparser.frontend.ASTWON;
import lolparser.frontend.ASTifThen;
import lolparser.frontend.ASTloop;
import lolparser.frontend.ASTparse;
import lolparser.frontend.LolParserVisitor;
import lolparser.frontend.SimpleNode;

public class LolParserVisitorAdapter implements LolParserVisitor
{

	@Override
	public Object visit(SimpleNode node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTparse node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTBlock node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTAssignmentExpression node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTFunctionDeclare node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTVarDeclareExpression node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTSUM node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTDIFF node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTPRODUKT node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTQUOSHUNT node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTMOD node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTBIGGR node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTSMALLR node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTUP node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTBooleanOperation node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTBOTH node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTEITHER node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTWON node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTNOT node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTComparisionOperation node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTBOTHSAEM node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTDIFFRINT node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTSpecialOps node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTIncrement node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTDecrement node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTloop node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ASTifThen node, Object data) {
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
