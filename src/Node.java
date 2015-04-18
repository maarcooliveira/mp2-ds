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
    public Node(int identifier) {
        this.identifier = identifier;
        fingerTable = new Integer[Main.BITS];
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
        recalculateFingerTable();
        broadcast("joined " + identifier);
    }

    /**
     * Finds a key on the circle.
     *
     * @param key an integer relative to a certain key of the circle (between 0 and 255).
     */
    public int find(int key) {

        if (key <= identifier && key > predecessor) {
            System.out.println("Key stored in " + identifier);
            return identifier;
        }
        else {
            System.out.println("Key will be search on finger table");
        }

        Integer max = null;
        for (int k = 0; k < Main.BITS; k++) {
            if (k <= key) {
                if((max != null && max < k) || max == null) {
                    max = k;
                }
            }
        }
        // Call
        if (max == null) {
            //call find on successor
            System.out.println("Next node: successor");
            return findKeyOn(key, fingerTable[0]);
        }
        else {
            //call find on node max
            System.out.println("Next node: " + max);
            return findKeyOn(key, max);
        }
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
        if (predecessor < node) {
            predecessor = node;
        }
        recalculateFingerTable();
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

    private void broadcast(String message) {

    }

    private int findKeyOn(int key, int node) {
        return 0;
    }


}
