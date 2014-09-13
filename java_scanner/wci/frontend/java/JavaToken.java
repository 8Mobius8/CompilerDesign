package wci.frontend.java;

import wci.frontend.*;

/**
 * <h1>JavaToken</h1>
 *
 * <p>Base class for Java token classes.</p>
 *
 * <p>Adapted from Dr. Mak.</p>
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 */
public class JavaToken extends Token
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    protected JavaToken(Source source)
        throws Exception
    {
        super(source);
    }
}
