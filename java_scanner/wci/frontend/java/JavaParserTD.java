package wci.frontend.java;

import wci.frontend.*;
import wci.message.Message;

import static wci.frontend.java.JavaTokenType.*;
import static wci.frontend.java.JavaErrorCode.*;
import static wci.message.MessageType.*;

/**
 * <h1>JavaParserTD</h1>
 *
 * <p>The top-down Java parser.</p>
 *
 * <p>Adapted from Dr. Mak.</p>
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 */
public class JavaParserTD extends Parser
{
    protected static JavaErrorHandler errorHandler = new JavaErrorHandler();

    /**
     * Constructor.
     * @param scanner the scanner to be used with this parser.
     */
    public JavaParserTD(Scanner scanner)
    {
        super(scanner);
    }

    /**
     * Parse a Java source program and generate the symbol table
     * and the intermediate code.
     */
    public void parse()
        throws Exception
    {
        Token token;
        long startTime = System.currentTimeMillis();

        try {
            // Loop over each token until the end of file.
            while (!((token = nextToken()) instanceof EofToken)) {
                TokenType tokenType = token.getType();

                if (tokenType != ERROR) {

                    // Format each token.
                    sendMessage(new Message(TOKEN,
                                            new Object[] {token.getLineNumber(),
                                                          token.getPosition(),
                                                          tokenType,
                                                          token.getText(),
                                                          token.getValue()}));
                }
                else {
                    errorHandler.flag(token, (JavaErrorCode) token.getValue(),
                                      this);
                }

            }

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            sendMessage(new Message(PARSER_SUMMARY,
                                    new Number[] {token.getLineNumber(),
                                                  getErrorCount(),
                                                  elapsedTime}));
        }
        catch (java.io.IOException ex) {
            errorHandler.abortTranslation(IO_ERROR, this);
        }
    }

    /**
     * Return the number of syntax errors found by the parser.
     * @return the error count.
     */
    public int getErrorCount()
    {
        return errorHandler.getErrorCount();
    }
}
