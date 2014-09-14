package wci.frontend.java;

import wci.frontend.*;
import wci.frontend.java.tokens.*;

import static wci.frontend.Source.EOF;
import static wci.frontend.Source.EOL;
import static wci.frontend.java.JavaTokenType.*;
import static wci.frontend.java.JavaErrorCode.*;

/**
 * <h1>JavaScanner</h1>
 *
 * <p>The Java scanner.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class JavaScanner extends Scanner
{
    /**
     * Constructor
     * @param source the source to be used with this scanner.
     */
    public JavaScanner(Source source)
    {
        super(source);
    }

    /**
     * Extract and return the next Java token from the source.
     * @return the next token.
     * @throws Exception if an error occurred.
     */
    protected Token extractToken()
        throws Exception
    {
        skipWhiteSpace();

        Token token;
        char currentChar = currentChar();

        // Construct the next token.  The current character determines the
        // token type.
        if (currentChar == EOF) {
            token = new EofToken(source);
        }
        else if (Character.isLetter(currentChar)) {
            token = new JavaWordToken(source);
        }
        else if (Character.isDigit(currentChar)) {
            token = new JavaNumberToken(source);
        }
        else if (currentChar == '\'') { // single quote means char, '?'
            token = new JavaCharToken(source);
        }
        else if (currentChar == '"') //    double quote means string, "?"
         {
          token = new JavaStringToken(source);
         }
        else if (JavaTokenType.SPECIAL_SYMBOLS
                 .containsKey(Character.toString(currentChar))) {
            token = new JavaSpecialSymbolToken(source);
        }
        else {
            token = new JavaErrorToken(source, INVALID_CHARACTER,
                                         Character.toString(currentChar));
            nextChar();  // consume character
        }

        return token;
    }

  /**
   * Skip whitespace characters by consuming them. A comment is whitespace.
   *
   * @throws Exception if an error occurred.
   */
  private void skipWhiteSpace()
     throws Exception
   {
    char currentChar = currentChar();

    while (Character.isWhitespace(currentChar) || (currentChar == '/'))
     {
      /*  Pascal comments are not Java comments
      // Start of a comment?
      if (currentChar == '{')
       {
        do
         {
          currentChar = nextChar();  // consume comment characters
         } while ((currentChar != '}') && (currentChar != EOF));

        // Found closing '}'?
        if (currentChar == '}')
         {
          currentChar = nextChar();  // consume the '}'
         }
       }
      */
      
      if(currentChar == '/')
       {
        if(peekChar() == '*')
         {
          //It's a comment block, keep eating chars until you find */
          nextChar();   //You must eat two chars, since the comment block start is 2 chars long.
                          // Also, /*/ is not a valid comment block - it starts but never ends. So, make sure
                          // you eat that * or nasty things will happen.
                          // Comment blocks must be /**/ or larger.
           do
            {
             currentChar = nextChar();
             if (currentChar == EOF)
              { return; }
            } while ((currentChar != '*' && peekChar() != '/'));
           
           nextChar();
           currentChar = nextChar(); //eat the closing statement
         }
        else if(peekChar() == '/')
         {
          do
           {
            currentChar = nextChar();
          } while(currentChar != EOF && currentChar != EOL);
          if(currentChar != EOF) //if it's an EOL, eat it
           {
            currentChar = nextChar();
           }
         }
        else
         {
          return;
         }
       }
      else if(currentChar == '/')
       {
        return;
       }
      
      // Not a comment.
      else
       {
        currentChar = nextChar();  // consume whitespace character
       }
     }
   }
 }
