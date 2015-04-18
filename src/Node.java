/**
 * Created by Marco Andre De Oliveira <mdeoliv2@illinois.edu>
 * Date: 4/17/15
 */
public class Node implements Runnable {
    int identifier;
    int predecessor;
    int successor;
    Integer[] fingerTable;

    public Node() {
        fingerTable = new Integer[8];
        predecessor = identifier;
        successor = identifier;
    }

    @Override
    public void run() {
        while(true) {
            //receive broadcast message

            //call function depending on the command received
        }
    }

    public void join(int predecessor) {
        this.predecessor = predecessor;

    }

    public void find(int key) {

    }

    /* Called when the coordinator node sends a "leave" message */
    public void leave() {

    }

    public void show() {
        int firstKey = predecessor == identifier ? Main.TOTAL_KEYS - 1 : predecessor + 1;
        int lastKey = identifier;

        for(int key = firstKey; key <= lastKey; key++) {
            System.out.println(key);
        }
    }

    /* Called by a new node that just entered. It should update its
     * finger table to reflect the new node added. */
    public void nodeEntered(int node) {

    }

    /* Called by a node that just left. It should update its finger
     * table to reflect the node removal. */
    public void nodeLeft(int removedNode, int removedNodePredecessor) {
        predecessor = removedNodePredecessor;

    }

    private void recalculateFingerTable() {

    }
}
