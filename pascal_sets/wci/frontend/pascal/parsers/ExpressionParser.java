package wci.frontend.pascal.parsers;

import java.util.EnumSet;
import java.util.HashMap;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.symtabimpl.SymTabKeyImpl;
import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

import java.util.ArrayList;

/**
 * <h1>ExpressionParser</h1>
 *
 * <p>
 * Parse a Pascal expression.</p>
 *
 * <p>
 * Copyright (c) 2009 by Ronald Mak</p>
 * <p>
 * For instructional purposes only. No warranties.</p>
 */
public class ExpressionParser extends StatementParser
 {
  /**
   * Constructor.
   *
   * @param parent the parent parser.
   */
  public ExpressionParser(PascalParserTD parent)
   {
    super(parent);
   }

  // Synchronization set for starting an expression.
  static final EnumSet<PascalTokenType> EXPR_START_SET
     = EnumSet.of(PLUS, MINUS, IDENTIFIER, INTEGER, REAL, STRING,
                  PascalTokenType.NOT, LEFT_PAREN);

  /**
   * Parse an expression.
   *
   * @param token the initial token.
   *
   * @return the root node of the generated parse tree.
   *
   * @throws Exception if an error occurred.
   */
  public ICodeNode parse(Token token)
     throws Exception
   {
    return parseExpression(token);
   }

  // Set of relational operators.
  private static final EnumSet<PascalTokenType> REL_OPS
     = EnumSet.of(EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUALS,
                  GREATER_THAN, GREATER_EQUALS, IN);

  // Map relational operator tokens to node types.
  private static final HashMap<PascalTokenType, ICodeNodeType> REL_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeType>();

  static
   {
    REL_OPS_MAP.put(EQUALS, EQ);
    REL_OPS_MAP.put(NOT_EQUALS, NE);
    REL_OPS_MAP.put(LESS_THAN, LT);
    REL_OPS_MAP.put(LESS_EQUALS, LE);
    REL_OPS_MAP.put(GREATER_THAN, GT);
    REL_OPS_MAP.put(GREATER_EQUALS, GE);
    REL_OPS_MAP.put(IN, SET_IN);
   }

  ;

    /**
     * Parse an expression.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseExpression(Token token)
     throws Exception
   {
        // Parse a simple expression and make the root of its tree
    // the root node.
    ICodeNode rootNode = parseSimpleExpression(token);

    token = currentToken();
    TokenType tokenType = token.getType();

//    if (tokenType == LEFT_BRACKET)
//     {
//      token = nextToken(); // consume the [
//      ICodeNode setNode = parseSet(token);
//
//      rootNode.addChild(setNode);
//
//      // TODO: not sure if this should be here...
//     }

    // Look for a relational operator.
    if (REL_OPS.contains(tokenType))
     {
    	
            // Create a new operator node and adopt the current tree
      // as its first child.
      ICodeNodeType nodeType = REL_OPS_MAP.get(tokenType);
      ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
      opNode.addChild(rootNode);
      
      Token opToken = currentToken();
      
      token = nextToken();  // consume the operator
      
            // Parse the second simple expression.  The operator node adopts
      // the simple expression's tree as its second child.
      ICodeNode rightExp = parseSimpleExpression(token);
      if(rightExp.getType() == ICodeNodeTypeImpl.SET || rootNode.getType() == ICodeNodeTypeImpl.SET){
    	  if(!SET_OPS.contains(tokenType) ){
    		  errorHandler.flag(opToken, INVALID_OPERATOR, this);
    	  }
      }
      opNode.addChild(rightExp);
      
      // The operator node becomes the new root node.
      rootNode = opNode;
     }

    return rootNode;
   }

  // Set of additive operators.
  private static final EnumSet<PascalTokenType> ADD_OPS
     = EnumSet.of(PLUS, MINUS, PascalTokenType.OR);

  // Map additive operator tokens to node types.
  private static final HashMap<PascalTokenType, ICodeNodeTypeImpl> ADD_OPS_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeTypeImpl>();

  static
   {
    ADD_OPS_OPS_MAP.put(PLUS, ADD);
    ADD_OPS_OPS_MAP.put(MINUS, SUBTRACT);
    ADD_OPS_OPS_MAP.put(PascalTokenType.OR, ICodeNodeTypeImpl.OR);
   }

  ;

    /**
     * Parse a simple expression.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseSimpleExpression(Token token)
     throws Exception
   {
    TokenType signType = null;  // type of leading sign (if any)

    // Look for a leading + or - sign.
    TokenType tokenType = token.getType();

    ICodeNode rootNode;

    // TODO: logic check, maybe I should not return yet
    if (tokenType == LEFT_BRACKET)
     {

      token = nextToken(); // consume the [

      rootNode = parseSet(token);

      return rootNode;

     }

    token = currentToken();
    tokenType = token.getType();

    if ((tokenType == PLUS) || (tokenType == MINUS))
     {
      signType = tokenType;
      token = nextToken();  // consume the + or -
     }

    // Parse a term and make the root of its tree the root node.
    rootNode = parseTerm(token);

    // Was there a leading - sign?
    if (signType == MINUS)
     {

            // Create a NEGATE node and adopt the current tree
      // as its child. The NEGATE node becomes the new root node.
      ICodeNode negateNode = ICodeFactory.createICodeNode(NEGATE);
      negateNode.addChild(rootNode);
      rootNode = negateNode;
     }

    token = currentToken();
    tokenType = token.getType();

    // Loop over additive operators.
    while (ADD_OPS.contains(tokenType))
     {

            // Create a new operator node and adopt the current tree
      // as its first child.
      ICodeNodeType nodeType = ADD_OPS_OPS_MAP.get(tokenType);
      ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
      opNode.addChild(rootNode);

      token = nextToken();  // consume the operator

            // Parse another term.  The operator node adopts
      // the term's tree as its second child.
      opNode.addChild(parseSimpleExpression(token));

      // The operator node becomes the new root node.
      rootNode = opNode;

      token = currentToken();
      tokenType = token.getType();
     }

    return rootNode;
   }

  // Set of multiplicative operators.
  private static final EnumSet<PascalTokenType> MULT_OPS
     = EnumSet.of(STAR, SLASH, DIV, PascalTokenType.MOD, PascalTokenType.AND);

  // Map multiplicative operator tokens to node types.
  private static final HashMap<PascalTokenType, ICodeNodeType> MULT_OPS_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeType>();

  static
   {
    MULT_OPS_OPS_MAP.put(STAR, MULTIPLY);
    MULT_OPS_OPS_MAP.put(SLASH, FLOAT_DIVIDE);
    MULT_OPS_OPS_MAP.put(DIV, INTEGER_DIVIDE);
    MULT_OPS_OPS_MAP.put(PascalTokenType.MOD, ICodeNodeTypeImpl.MOD);
    MULT_OPS_OPS_MAP.put(PascalTokenType.AND, ICodeNodeTypeImpl.AND);
   }

  ;

    /**
     * Parse a term.
     * @param token the initial token.
     * @return the root of the generated parse subtree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseTerm(Token token)
     throws Exception
   {
    // Parse a factor and make its node the root node.
    ICodeNode rootNode = parseFactor(token);

    token = currentToken();
    TokenType tokenType = token.getType();

    // Loop over multiplicative operators.
    while (MULT_OPS.contains(tokenType))
     {

            // Create a new operator node and adopt the current tree
      // as its first child.
      ICodeNodeType nodeType = MULT_OPS_OPS_MAP.get(tokenType);
      ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
      opNode.addChild(rootNode);

      token = nextToken();  // consume the operator

            // Parse another factor.  The operator node adopts
      // the term's tree as its second child.
      opNode.addChild(parseFactor(token));

      // The operator node becomes the new root node.
      rootNode = opNode;

      token = currentToken();
      tokenType = token.getType();
     }

    return rootNode;
   }

  /**
   * Parse a factor.
   *
   * @param token the initial token.
   *
   * @return the root of the generated parse subtree.
   *
   * @throws Exception if an error occurred.
   */
  private ICodeNode parseFactor(Token token)
     throws Exception
   {
    TokenType tokenType = token.getType();
    ICodeNode rootNode = null;

    switch ((PascalTokenType) tokenType)
     {

      case IDENTIFIER:
       {
                // Look up the identifier in the symbol table stack.
        // Flag the identifier as undefined if it's not found.
        String name = token.getText().toLowerCase();
        SymTabEntry id = symTabStack.lookup(name);
        if (id == null)
         {
          errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
          id = symTabStack.enterLocal(name);
         }
    
        rootNode = ICodeFactory.createICodeNode(VARIABLE);
        rootNode.setAttribute(ID, id);
        id.appendLineNumber(token.getLineNumber());
        
        token = nextToken();  // consume the identifier
        break;
       }

      case INTEGER:
       {
        // Create an INTEGER_CONSTANT node as the root node.
        rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
        rootNode.setAttribute(VALUE, token.getValue());

        token = nextToken();  // consume the number
        break;
       }

      case REAL:
       {
        // Create an REAL_CONSTANT node as the root node.
        rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
        rootNode.setAttribute(VALUE, token.getValue());

        token = nextToken();  // consume the number
        break;
       }

      case STRING:
       {
        String value = (String) token.getValue();

        // Create a STRING_CONSTANT node as the root node.
        rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
        rootNode.setAttribute(VALUE, value);

        token = nextToken();  // consume the string
        break;
       }

      case NOT:
       {
        token = nextToken();  // consume the NOT

        // Create a NOT node as the root node.
        rootNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

                // Parse the factor.  The NOT node adopts the
        // factor node as its child.
        rootNode.addChild(parseFactor(token));

        break;
       }

      case LEFT_PAREN:
       {
        token = nextToken();      // consume the (

        // Parse an expression and make its node the root node.
        rootNode = parseExpression(token);

        // Look for the matching ) token.
        token = currentToken();
        if (token.getType() == RIGHT_PAREN)
         {
          token = nextToken();  // consume the )
         }
        else
         {
          errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
         }

        break;
       }
      
      case LEFT_BRACKET:
       {
        token = nextToken(); //eat [
        rootNode = parseSet(token);
        
        
       }

      default:
       {
        errorHandler.flag(token, UNEXPECTED_TOKEN, this);
        break;
       }
     }

    return rootNode;
   }
  
  //AND and OR return boolean types, so they should not be included
  private static final EnumSet<PascalTokenType> SET_OPS = 
		  EnumSet.of(EQUALS, PLUS, MINUS, LESS_EQUALS, GREATER_EQUALS, STAR, NOT_EQUALS, IN);
  private static final EnumSet<ICodeNodeTypeImpl> SET_TYPES
	= EnumSet.of(VARIABLE, INTEGER_CONSTANT, REAL_CONSTANT, MULTIPLY, 
			INTEGER_DIVIDE, ICodeNodeTypeImpl.MOD,
			ICodeNodeTypeImpl.RANGE, ADD, SUBTRACT, NEGATE);
  
  private ICodeNode parseSet(Token token)
     throws Exception
   {
    	// Should use PascalTokenTypes to control this function. May possibly even use
    // parseExpression(), parseTerm(), parseSimpleExpression
    // Map additive operator tokens to node types.

    TokenType tokenType = token.getType();

    // SET type
    ICodeNode setNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.SET);

    // Now SET is the root node
    ICodeNode rootNode = setNode;

    ArrayList<Integer> currChildren = new ArrayList<>(); // Used for checking for unique values

    while (tokenType != RIGHT_BRACKET)
     {
      Token old = currentToken();
      ICodeNode oldNode;
      boolean isConstantSubrange = false; //If it's a constant we can parse it now
      
      ICodeNode newNode = parseExpression(token);
      if(newNode == null)
       {
        break; //error
       }
      else if (newNode.getType() == VARIABLE) {
    	  /*String targetName = token.getText().toLowerCase();
          SymTabEntry targetId = symTabStack.lookup(targetName);
          
          Integer symTabConstValue = (Integer) targetId.getAttribute(SymTabKeyImpl.CONSTANT_VALUE);
          
          if (symTabConstValue != null) 
          {
          	newNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
          	newNode.setAttribute(VALUE, symTabConstValue);
          }*/
      }
      
      ICodeNodeType newNodeType = newNode.getType();
      oldNode = newNode;

      if (!(SET_TYPES.contains(newNodeType)))
       {
        errorHandler.flag(token, UNEXPECTED_TOKEN, this);
       }

      token = currentToken();
      tokenType = token.getType();
      
      // If a range is being defined
      if (tokenType == DOT_DOT)
       {
        ICodeNode dotNode = ICodeFactory.createICodeNode(RANGE);
        dotNode.setAttribute(VALUE, DOT_DOT);

        dotNode.addChild(newNode); // Set range as the parent node

        token = nextToken(); // Consume the ..
        tokenType = token.getType();
        
        ICodeNode tempNode = parseExpression(token);
        
        if(tempNode == null)
         {
          break;
         }
        
        else if (tempNode.getType() == VARIABLE){
        	String targetName = (String) newNode.getAttribute(ID);
            SymTabEntry targetId = symTabStack.lookup(targetName);
            
            Integer symTabConstValue = (Integer) targetId.getAttribute(SymTabKeyImpl.CONSTANT_VALUE);
            
            if (symTabConstValue != null) {
            	tempNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
            	tempNode.setAttribute(VALUE, symTabConstValue);
            }
        }
        ICodeNodeType tempNodeType = tempNode.getType();

        
        //Integer constant subrange. Add all the range's values to rootNode
        if(oldNode.getType() == INTEGER_CONSTANT && tempNodeType == INTEGER_CONSTANT)
         { //they are both integers, so the cast should not fail
          isConstantSubrange = true;
          if((int)oldNode.getAttribute(VALUE) < (int)tempNode.getAttribute(VALUE))
           {
            for(int i = (int)oldNode.getAttribute(VALUE); i < (int)tempNode.getAttribute(VALUE); i++)
             {
              ICodeNode rangeValue = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
              rangeValue.setAttribute(VALUE, i);
              
              if(currChildren.contains(i))
               {
                errorHandler.flag(old, NON_UNIQUE_MEMBERS, this);
               }
              else
               {
                currChildren.add(i);
                rootNode.addChild(rangeValue);
               }
              
             }
           }
         }
        
        
        if ((!isConstantSubrange) && SET_TYPES.contains(tempNodeType)) //if it's a constant subrange it's
                                                            //already been added to the node
         {
          dotNode.addChild(tempNode); // Add the other field into the range node
          rootNode.addChild(dotNode); // Add the range into the set tree
         }
        else if(isConstantSubrange)
         {
          //not an error, just don't do anything here
         }
        else
         {
          errorHandler.flag(token, UNEXPECTED_TOKEN, this);
         }
       }
      else
       {
        Integer newNodeValue = (Integer) ((ICodeNodeImpl) newNode).get(ICodeKeyImpl.VALUE);
        if(newNodeValue == null)
         {
          rootNode.addChild(newNode);
         }
        else if (!currChildren.contains(newNodeValue))
         {
          rootNode.addChild(newNode);
          currChildren.add(newNodeValue);
         }
        else
         {
          errorHandler.flag(old, PascalErrorCode.NON_UNIQUE_MEMBERS, this);
         }
       }

      token = currentToken();
      tokenType = token.getType();

      if (tokenType == RIGHT_BRACKET)
       {
        token = nextToken(); // Consume the ]
        return rootNode;
       }

      else if (tokenType == SEMICOLON)
       {
        errorHandler.flag(token, MISSING_RIGHT_BRACKET, this);
       }

      else if (tokenType == COMMA)
       {
        token = nextToken(); // Consume the comma
        tokenType = token.getType();
        
        while(((PascalTokenType)token.getType())== COMMA)
         {
          errorHandler.flag(token, EXTRA_COMMA, this);
          token = nextToken();
          tokenType = token.getType();
         }
       }
      else
       {
        errorHandler.flag(token, MISSING_COMMA, this);
       }

      // Check for comma and error check for semicolon
     }

    if (tokenType == RIGHT_BRACKET)
     {
      token = nextToken(); // consume the ]
     }

    return rootNode;
   }
 }
