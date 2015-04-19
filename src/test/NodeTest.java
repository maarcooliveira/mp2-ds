package test;

import chord.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

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
        assert testNode.storedKeys(12, 0, 32).equals("1 2 3 4 5 6 7 8 9 10 11 12");
        assert testNode.storedKeys(20, 12, 32).equals("13 14 15 16 17 18 19 20");
        assert testNode.storedKeys(6, 5, 8).equals("6");
        assert testNode.storedKeys(0, 20, 32).equals("0 21 22 23 24 25 26 27 28 29 30 31");
        assert testNode.storedKeys(5, 5, 8).equals("0 1 2 3 4 5 6 7");
        assert testNode.storedKeys(8, 12, 16).equals("0 1 2 3 4 5 6 7 8 13 14 15");
    }

    @Test
    public void testAfterJoin() {
        // Using nodes 0, 80, 160, 220 for 8 bits (256 beys) on node 80
        // We will test what happens when 96, whose successor is 160, joins the circle
        Integer[] oldFingerTable = {160, 160, 160, 160, 160, 160, 160, 220};    // Correct according to the theory
        Integer[] newFingerTable = {96, 96, 96, 96, 96, 160, 160, 220};         // Tested  according to the theory
        assert Arrays.equals(testNode.fingerTableAfterJoin(80, oldFingerTable, 96, 160), newFingerTable);
    }

    @Test
    public void testAfterLeft() {
        // Using nodes 0, 80, 96, 160, 220 for 8 bits (256 beys) on node 80
        // We will test what happens when 96, whose successor is 160, leaves
        Integer[] oldFingerTable = {96, 96, 96, 96, 96, 160, 160, 220};        // Correct according to the theory
        Integer[] newFingerTable = {160, 160, 160, 160, 160, 160, 160, 220};   // Tested  according to the theory
        assert Arrays.equals(testNode.fingerTableAfterLeft(oldFingerTable, 96, 160), newFingerTable);
    }


}