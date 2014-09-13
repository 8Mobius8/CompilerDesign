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
 * @author Jason Hungerford
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
      switch(currentChar)
       {
        case 't':
          text = "\\t";
          value = "\t";
          break;
        case 'b':
          text = "\\b";
          value = "\b";
          break;
        case 'n':
          text = "\\n";
          value = "\n";
          break;
        case 'r':
          text = "\\r";
          value = "\r";
          break;
        case 'f':
          text = "\\f";
          value = "\f";
          break;
        case '\'':
          text = "\\'"; //single-quote
          value = "'";
          break;
        case '"':
          text = "\\\"";
          value = "\"";
          break;
        case '\\':
          text = "\\\\"; //backslash - text becomes   \\
          value = "\\";  //value becomes    \
          break;
        default:
          text = "\\" + currentChar;
          type = ERROR;
          value = INVALID_ESCAPE_CHARACTER;
          return;
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
   }
  
 }
