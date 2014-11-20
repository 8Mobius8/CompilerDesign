package wci.intermediate;

import wci.frontend.Node;

/**
 * <h1>ICode</h1>
 *
 * <p>The framework interface that represents the intermediate code.</p>
 *
 * <p>Copyright (c) 2008 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public interface ICode
{
    /**
     * Set and return the root node.
     * @param node the node to set as root.
     * @return the root node.
     */
    public Node setRoot(Node node);

    /**
     * Get the root node.
     * @return the root node.
     */
    public Node getRoot();
}
