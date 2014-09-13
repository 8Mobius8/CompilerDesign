package wci.frontend.java;

import java.util.Hashtable;
import java.util.HashSet;

import wci.frontend.TokenType;

/**
 * <h1>JavaTokenType</h1>
 *
 * <p>Java token types.</p>
 *
 * <p>Adapted from Dr. Mak.</p>
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 */
public enum JavaTokenType implements TokenType
{
    // Reserved words. All these reserved words appear in all
	// lower case in the source code. Don't forget Java is case
	// sensitive
	ABSTRACT, DOUBLE, INT, LONG,
	BREAK, ELSE, SWITCH,
	CASE, ENUM, NATIVE, SUPER,
	CHAR, EXTENDS, RETURN, THIS,
	CLASS, FLOAT, SHORT, THROW,
	CONST, FOR, PACKAGE, VOID,
	CONTINUE, GOTO, PROTECTED, VOLATILE,
	DO, IF, STATIC, WHILE,

    // Special symbols.
	TILDE("~"), EXCLAIM("!"), AT("@"), PERCENT("%"), 
	CAROT("^"), AMPERSAND("&"), ASTRISK("*"), 
	MINUS("-"), PLUS("+"), EQUALS("="), PIPE("|"), 
	SLASH("/"), COLON(":"), SEMI_COLON(";"), 
	QUESTION_MARK("?"), LESS_THAN("<"), 
	GREATER_THAN(">"), PERIOD("."), COMMA(","), 
	SINGLE_QUOTE("'"), DOUBLE_QUOTE("\""), 
	OPEN_PAREN("("), CLOSE_PAREN(")"), 
	OPEN_BRAKET("["), CLOSE_BRAKET("]"), 
	OPEN_BRACE("{"), CLOSE_BRACE("}"), INCREMENT("++"), 
	DECREMENT("--"), SHIFT_LEFT("<<"), 
	SHIFT_RIGHT(">>"), LESS_EQUALS("<="), 
	GREATER_EQUALS(">="), PLUS_EQUALS("+="), 
	MINUS_EQUALS("-="), MULTI_EQUALS("*="), 
	DIVIDE_EQUALS("/="), DOUBLE_EQUALS("=="), 
	PIPE_EQUALS("|="), PERCENT_EQUALS("%="), 
	AMP_EQUALS("&="), CAROT_EQUALS("^="), 
	EXCLAIM_EQUALS("!="), DOUBLE_LESS_EQUALS("<<="), 
	DOUBLE_GREATER_EQUALS(">>="), OR("||"), 
	AND("&&"), DOUBLE_SLASH("//"), 
	COMMENT_OPEN("/*"), COMMENT_CLOSE("*/"),


    IDENTIFIER, INTEGER, REAL, STRING,
    ERROR, END_OF_FILE;

    private static final int FIRST_RESERVED_INDEX = ABSTRACT.ordinal();
    private static final int LAST_RESERVED_INDEX  = WHILE.ordinal();

    private static final int FIRST_SPECIAL_INDEX = TILDE.ordinal();
    private static final int LAST_SPECIAL_INDEX  = COMMENT_CLOSE.ordinal();

    private String text;  // token text

    /**
     * Constructor.
     */
    JavaTokenType()
    {
        this.text = this.toString().toLowerCase();
    }

    /**
     * Constructor.
     * @param text the token text.
     */
    JavaTokenType(String text)
    {
        this.text = text;
    }

    /**
     * Getter.
     * @return the token text.
     */
    public String getText()
    {
        return text;
    }

    // Set of lower-cased Java reserved word text strings.
    public static HashSet<String> RESERVED_WORDS = new HashSet<String>();
    static {
        JavaTokenType values[] = JavaTokenType.values();
        for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i) {
            RESERVED_WORDS.add(values[i].getText().toLowerCase());
        }
    }

    // Hash table of Java special symbols.  Each special symbol's text
    // is the key to its Java token type.
    public static Hashtable<String, JavaTokenType> SPECIAL_SYMBOLS =
        new Hashtable<String, JavaTokenType>();
    static {
        JavaTokenType values[] = JavaTokenType.values();
        for (int i = FIRST_SPECIAL_INDEX; i <= LAST_SPECIAL_INDEX; ++i) {
            SPECIAL_SYMBOLS.put(values[i].getText(), values[i]);
        }
    }
}
