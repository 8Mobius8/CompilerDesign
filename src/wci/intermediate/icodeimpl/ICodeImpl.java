package wci.intermediate.icodeimpl;

import wci.frontend.Node;
import wci.intermediate.*;

/**
 * <h1>ICodeImpl</h1>
 *
 * <p>An implementation of the intermediate code as a parse tree.</p>
 *
 * <p>Copyright (c) 2008 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ICodeImpl implements ICode
{
    private Node root;  // root node

    /**
     * Set and return the root node.
     * @param node the node to set as root.
     * @return the root node.
     */
    public Node setRoot(Node node)
    {
        root = node;
        return root;
    }

    /**
     * Get the root node.
     * @return the root node.
     */
    public Node getRoot()
    {
        return root;
    }
}
