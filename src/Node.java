/**
 * Defines behavior for Node thread. A node stores keys usually represented on a circle, and maps which node to check to
 * find a certain key by using a finger table.
 *
 * @author Marco Andre de Oliveira <mdeoliv2@illinois.edu>
 * @version 1.0
 */
public class Node implements Runnable {
    int identifier, predecessor, successor;
    Integer[] fingerTable;

    /**
     * Creates a Node thread and sets its initial finger table.
     */
    public Node() {
        fingerTable = new Integer[8];
        predecessor = identifier;
    }

    /**
     * Runs the Node thread.
     */
    @Override
    public void run() {
        while (true) {
            //receive broadcast message

            //call function depending on the command received
        }
    }

    /**
     * Makes the Node thread join the circle of 256 keys.
     *
     * @param predecessor the node's predecessor.
     */
    public void join(int predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * Finds a key on the circle.
     *
     * @param key
     */
    public void find(int key) {

    }

    /**
     * Called when the coordinator node sends a "leave" message.
     */
    public void leave() {

    }

    /**
     * Show all keys stored in the node.
     */
    public void show() {
        int firstKey = predecessor == identifier ? Main.TOTAL_KEYS - 1 : predecessor + 1;
        int lastKey = identifier;

        for (int key = firstKey; key <= lastKey; key++) {
            System.out.println(key);
        }
    }

    /**
     * Called by a new node that just entered. It should update its finger table to reflect the new node added.
     */
    public void nodeEntered(int node) {

    }

    /**
     * Called by a node that just left. It should update its finger table to reflect the node removal.
     */
    public void nodeLeft(int removedNode, int removedNodePredecessor) {
        predecessor = removedNodePredecessor;

    }

    /**
     * Recalculates the finger table whenever a new node is either added or removed from the circle.
     */
    private void recalculateFingerTable() {

    }
}
