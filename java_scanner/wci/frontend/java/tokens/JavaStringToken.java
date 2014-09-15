package wci.frontend.java.tokens;

import wci.frontend.*;
import wci.frontend.java.*;
import static wci.frontend.Source.EOL;
import static wci.frontend.Source.EOF;
import static wci.frontend.java.JavaTokenType.*;
import static wci.frontend.java.JavaErrorCode.*;

/**
 * <h1>JavaStringToken</h1>
 *
 * <p> Java string tokens.</p>
 *
 * <p>Adapted from Professor Mak.</p>
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 */
public class JavaStringToken extends JavaToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public JavaStringToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Java string token from the source.
     * @throws Exception if an error occurred.
     */
    @Override
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        char currentChar = nextChar();  // consume initial quote
        textBuffer.append('"');

        // Get string characters.
        do {
            //Do we need to check for escape characters? I think in the case of "\n" the value
                    //would be (newline) and the text would be "\n". In this case, we need to
                    //check for that and update value accordingly. I did this in JavaCharToken
            
            
            //We need to check for escape characters.
            if(currentChar == '\\')
            {
            	currentChar = nextChar();
            	if(JavaCharToken.isValidEscapeChar(currentChar))
            	 { 
            		textBuffer.append("\\" + currentChar); 
            		valueBuffer.append(currentChar);
            	 } 
            	else
            	 {
            		textBuffer.append("\\IE ");
            		valueBuffer.append(currentChar);
            		type = ERROR; // Should we make an enum for error-escaped string?
            	 }
            }
                                                                // EOL is not allowed either
            if ((currentChar != '"') && (currentChar != EOF) && currentChar != EOL) {
                textBuffer.append(currentChar);
                valueBuffer.append(currentChar);
                currentChar = nextChar();  // consume character
            }

        } while ((currentChar != '"') && (currentChar != EOF) && currentChar != EOL);
                        // In java, strings cannot be split among multiple lines
                        //And actually, they can't be split in Pascal either so I'm not sure why
                          //prof. mak doesn't check for this in his pascal code

        if (currentChar == '"') {
            nextChar();  // consume final quote
            textBuffer.append('"');

            type = STRING;
            value = valueBuffer.toString();
        }
        else if(currentChar == EOF){
            type = ERROR;
            value = UNEXPECTED_EOF;
        }
        else if(currentChar == EOL)
         {
          type = ERROR;
          value = UNEXPECTED_EOL;
         }
        else
         {
          //Should never get here
          type = ERROR;
          value = UNRECOGNIZABLE;
         }

        text = textBuffer.toString();
    }
}
