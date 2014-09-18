package wci.frontend.java.tokens;

import static wci.frontend.Source.*;
import static wci.frontend.java.JavaErrorCode.*;
import static wci.frontend.java.JavaTokenType.*;

import wci.frontend.Source;
import wci.frontend.java.JavaToken;

/**
 * <h1>JavaCharToken</h1>
 *
 * <p>Java character token.</p>
 *
 * <p>Adapted from Professor Mak.</p>
 * @author The_Almost_Donerz
 */
public class JavaCharToken extends JavaToken
 {
  public JavaCharToken(Source source) throws Exception
   {
    super(source);
   }
  
  @Override
  protected void extract() throws Exception
   {
    char currentChar = nextChar(); //Eat the first quote
    text = "'";
    if(currentChar == '\'') //single-quote character as next character
     {
      nextChar();
      type = ERROR;
      value = EMPTY_CHARACTER_LITERAL;
      text = "''";
      return;
     }
    
    if(currentChar == '\\') //escape sequence
     {
      currentChar = nextChar();
      String escapeSeq = returnValidEscapeChar(currentChar);
      if(escapeSeq == null)
       {
    	  text += "\\" + currentChar;
          type = ERROR;
          value = INVALID_ESCAPE_CHARACTER;
          return;
       }
      else 
      {
    	  text += escapeSeq.substring(1);
    	  value = escapeSeq.charAt(0);
      }
     }
    else if(currentChar == EOL)
     {
      type = ERROR;
      value = UNEXPECTED_EOL;
      text = "'";
      return;
     }
    else if(currentChar == EOF)
     {
      type = ERROR;
      value = UNEXPECTED_EOF;
      text = "'";
     }
    else //anything else, that's the char
     {
      value = currentChar;
      text = "'" + currentChar;
     }
    currentChar = nextChar();
    if(currentChar!='\'')
     {
      type = ERROR;
      value = UNCLOSED_CHARACTER_LITERAL;
      text += currentChar;
      return;
     }
    text += "'";
    type = CHAR;
    nextChar(); //eat final '
   }
  /**
   * Usage:
   * To get the char value getCharAt(0). To get String version subString(1); 
   * @param c - A char to check if is an escaped.
   * @return A string with the value of the character in the first char of String, followed by a string identifying it
   */
  public static String returnValidEscapeChar(char c)
  {
	  String toReturn;
	  switch(c)
	  {
       // We use the return string as a way of storing the text
	   // string for the symbol table entry.
	   case 't':
	     toReturn = "\t\\t";
	     break;
	   case 'b':
	     toReturn = "\b\\b";
	     break;
	   case 'n':
	     toReturn = "\n\\n";
	     break;
	   case 'r':
	     toReturn = "\r\\r";
	     break;
	   case 'f':
	     toReturn = "\f\\f";
	     break;
	   case '\'':
	     toReturn = "\'\\'"; //single-quote
	     break;
	   case '"':
	     toReturn = "\"\\\"";
	     break;
	   case '\\':
	     toReturn = "\\\\\\"; //backslash - text becomes   \\
	     break;
	   default:
	     toReturn = null;
	  }
	  return toReturn;
   }
 }
