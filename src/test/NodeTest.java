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
        assert testNode.storedKeys(12, 0, 32).equals("1 2 3 4 5 6 7 8 9 10 11 12");
        assert testNode.storedKeys(20, 12, 32).equals("13 14 15 16 17 18 19 20");
        assert testNode.storedKeys(6, 5, 8).equals("6");
        assert testNode.storedKeys(0, 20, 32).equals("0 21 22 23 24 25 26 27 28 29 30 31");
        assert testNode.storedKeys(5, 5, 8).equals("0 1 2 3 4 5 6 7");
        assert testNode.storedKeys(8, 12, 16).equals("0 1 2 3 4 5 6 7 8 13 14 15");
    }

    public String storedKeys(int nodeID, int prevID, int totalKeys) {
        int firstKey = (prevID + 1) % totalKeys;
        int lastKey = prevID < nodeID ? nodeID : nodeID + totalKeys;
        ArrayList<Integer> allNumbers = new ArrayList<Integer>();
        for (int key = firstKey; key <= lastKey; key++) {
            Integer modularKey = key % totalKeys;
            allNumbers.add(modularKey);
        }
        Collections.sort(allNumbers);
        return allNumbers.toString().replace("[", "").replace("]", "").replace(",", "");
    }
}