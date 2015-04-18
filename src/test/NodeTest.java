package test;

import chord.Node;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests modular versions of Node's methods.
 *
 * @author Cassio dos Santos Sousa <dssntss2@illinois.edu>
 * @version 1.0
 */
public class NodeTest {

    private Node testNode;

    @Before
    public void construct() {
        testNode = new Node(12);
    }

    @Test
    public void testStoredKeys() {
        assert testNode.storedKeys(20, 15, 32).equals("16 17 18 19 20");
        assert testNode.storedKeys(5, 28, 32).equals("29 30 31 0 1 2 3 4 5");
        assert testNode.storedKeys(5, 5, 8).equals("6 7 0 1 2 3 4 5");
        assert testNode.storedKeys(6, 5, 8).equals("6");
    }
}