package lolparser.intermediate;

import lolparser.frontend.ASTAdd;
import lolparser.frontend.ASTAnd;
import lolparser.frontend.ASTAssign;
import lolparser.frontend.ASTBlock;
import lolparser.frontend.ASTConst;
import lolparser.frontend.ASTDecrement;
import lolparser.frontend.ASTDivide;
import lolparser.frontend.ASTEquals;
import lolparser.frontend.ASTFunctionCall;
import lolparser.frontend.ASTFunctionDef;
import lolparser.frontend.ASTIdent;
import lolparser.frontend.ASTIf;
import lolparser.frontend.ASTIfBlock;
import lolparser.frontend.ASTIncrement;
import lolparser.frontend.ASTIncrementByInt;
import lolparser.frontend.ASTLOL_STD_OPS;
import lolparser.frontend.ASTLoop;
import lolparser.frontend.ASTMax;
import lolparser.frontend.ASTMin;
import lolparser.frontend.ASTModulus;
import lolparser.frontend.ASTMultiply;
import lolparser.frontend.ASTNot;
import lolparser.frontend.ASTNotEquals;
import lolparser.frontend.ASTOr;
import lolparser.frontend.ASTStdIn;
import lolparser.frontend.ASTStdOut;
import lolparser.frontend.ASTSubtract;
import lolparser.frontend.ASTVariableDef;
import lolparser.frontend.ASTXor;
import lolparser.frontend.ASTparse;
import lolparser.frontend.LolParserVisitor;
import lolparser.frontend.SimpleNode;

public class LolParserVisitorAdapter implements LolParserVisitor
{

  @Override
  public Object visit(SimpleNode node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTparse node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTBlock node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTAssign node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTConst node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTFunctionCall node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTFunctionDef node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTVariableDef node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTAdd node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTSubtract node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTMultiply node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTDivide node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTModulus node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTMax node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTMin node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTIncrementByInt node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTAnd node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTOr node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTXor node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTNot node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTEquals node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTNotEquals node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTLOL_STD_OPS node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTIncrement node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTDecrement node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTStdOut node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTStdIn node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTLoop node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTIf node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTIfBlock node, Object data) {
      return node.childrenAccept(this, data);
   }

  @Override
  public Object visit(ASTIdent node, Object data) {
      return node.childrenAccept(this, data);
   }
}
