package chord;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Defines behavior for chord.Node thread. A node stores keys usually represented on a circle, and maps which node to
 * check to find a certain key by using a finger table.
 *
 * @author Marco Andre de Oliveira <mdeoliv2@illinois.edu>
 * @version 1.0
 */
public class Node {
    int identifier, predecessor, successor;
    Integer[] fingerTable;
    ServerSocket listener;
    Socket socket = null;

    /**
     * Creates a chord.Node thread and sets its initial finger table.
     */
    public Node(int identifier) {
        this.identifier = identifier;
        fingerTable = new Integer[Main.BITS];
        try {

            listener = new ServerSocket(Coordinator.BASE_PORT + identifier);
            new Listener().start();
        } catch (IOException e) {
            if (!(e instanceof BindException))
                e.printStackTrace();
        }

    }

    /**
     * Runs the chord.Node thread.
     */
    private class Listener extends Thread {
        @Override
        public void run() {

            try {
                while (true) {
                    socket = listener.accept();
                    Coordinator.MSG_COUNT++;
                    BufferedReader receivedMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = receivedMessage.readLine();
                    String[] listArgs = msg.split(" ");

                    if (listArgs[0].equals("join")) {
                        joinNode();
                        sendAck(msg);
                    }

                    else if (listArgs[0].equals("find")) {
                        if (listArgs.length == 3) {
                            int key = Integer.parseInt(listArgs[2]);
                            int response = find(key);
                            sendAck(msg + " " + response);
                        }
                        else {
                            int key = Integer.parseInt(listArgs[1]);
                            int response = find(key);
                            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                            writer.println(response);
                        }
                    }

                    else if (listArgs[0].equals("leave")) {
                        leave();
                        sendAck(msg);
                    }

                    else if (listArgs[0].equals("show")) {
                        show();
                        sendAck(msg);
                    }

                    else if (listArgs[0].equals("joined")) {
                        int node = Integer.parseInt(listArgs[1]);
                        nodeEntered(node);
                        sendAck(msg);
                    }

                    else if (listArgs[0].equals("left")) {
                        int node = Integer.parseInt(listArgs[1]);
                        nodeLeft(node);
                        sendAck(msg);
                    }

                    else if (listArgs[0].equals("predecessor")) {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.println(getPredecessor());
                    }

                    else if (listArgs[0].equals("successor")) {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.println(getSuccessor());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }

        }
    }

    /**
     * Makes the chord.Node thread join the circle of 256 keys.
     */
    public void joinNode() {
        if (identifier != 0) {
            successor = findKeyOn(identifier, 0);
            predecessor = getPredecessorOf(successor);
        } else {
            successor = 0;
            predecessor = 0;
        }
        recalculateFingerTable(identifier, true);

    }

    /**
     * Will find the predecessor of the instance of the node calling the function
     *
     * @return the predecessor of the node
     */
    public int getPredecessor() {
        return predecessor;
    }

    /**
     * Will find the successor of the instance of the node calling the function
     *
     * @return the successor of the node
     */
    public int getSuccessor() {
        return successor;
    }

    /**
     * Finds a key on the circle.
     *
     * @param key an integer relative to a certain key of the circle (between 0 and 255).
     */
    public int find(int key) {

        if (key == identifier) {
            return identifier;
        } else if (key > predecessor && (key < identifier || (identifier == 0 && key < Main.TOTAL_KEYS))) {
            return identifier;
        }

        Integer max = null;
        for (int k = 0; k < Main.BITS; k++) {
            if (fingerTable[k] <= key && fingerTable[k] >= identifier) {
                if ((max != null && max <= fingerTable[k]) || max == null) {
                    max = fingerTable[k];
                }
            }
        }

        if (max == null) {
            // Call find() on successor
            if (successor != identifier)
                return findKeyOn(key, fingerTable[0]);
        }
        else {
            // Call find on node max
            if (max == 0)
                return findKeyOn(key, fingerTable[0]);
            if (max != identifier)
                return findKeyOn(key, max);
        }

        return identifier;
    }

    /**
     * Called when the coordinator node sends a "leave" message.
     * No action required when the node receives a leave command.
     */
    public void leave() {}

    /**
     * Helper function to show all the keys stored in the node.
     */
    public void show() {
        System.out.print(identifier + " ");
        System.out.println(storedKeys(identifier, predecessor, Main.TOTAL_KEYS));
    }

    /**
     * Gets all keys stored in a node given its ID and its predecessor.
     *
     * @param nodeID    the node ID.
     * @param prevID    the predecessor ID.
     * @param totalKeys the total number of keys in the system.
     * @return a string containing all keys separated by spaces.
     */
    public String storedKeys(int nodeID, int prevID, int totalKeys) {
        int firstKey = (prevID + 1);
        int lastKey = prevID < nodeID ? nodeID : (nodeID + totalKeys) ;
        ArrayList<Integer> allNumbers = new ArrayList<Integer>();
        for (int key = firstKey; key <= lastKey; key++) {
            Integer modularKey = key % totalKeys;
            allNumbers.add(modularKey);
        }
        Collections.sort(allNumbers);
        return allNumbers.toString().replace("[", "").replace("]", "").replace(",", "");
    }

    /**
     * Called when a node just entered. The node executing this command should update its finger table
     * to reflect the new node added.
     *
     * @param addedNode the most recent node
     */
    public void nodeEntered(int addedNode) {

        if (addedNode != identifier) {
            if (getPredecessorOf(addedNode) == identifier)
                successor = addedNode;

            if (getSuccessorOf(addedNode) == identifier)
                predecessor = addedNode;

            recalculateFingerTable(addedNode, true);
        }
    }

    /**
     * Called when a node just left. The node executing this command should update its finger table
     * to reflect the node removal.
     *
     * @param removedNode the removed node
     */
    public void nodeLeft(int removedNode) {

        if (removedNode != identifier) {
            if (removedNode == predecessor)
                predecessor = getPredecessorOf(removedNode);

            else if (removedNode == successor)
                successor = getSuccessorOf(removedNode);

            recalculateFingerTable(removedNode, false);
        }
    }

    /**
     * Defines if the node calling it should have its finger table updated or initialized. This is called
     * whenever a new node is added or removed from the circle.
     *
     * @param node the node that either entered or left the circle
     * @param added indicates whether the node was added or removed
     */
    private void recalculateFingerTable(int node, boolean added) {

        if (node == identifier) {
            if (added == true)
                initializeFingerTable(node);
        }
        else {
            if (added == true)
                fingerTable = fingerTableAfterJoin(identifier, fingerTable, node, getSuccessorOf(node));
            else
                fingerTable = fingerTableAfterLeft(fingerTable, node, getSuccessorOf(node));
        }
    }

    /**
     * Initialize the finger table when the node is new and was just added to the circle
     *
     * @param node the node to be initialized
     */
    private void initializeFingerTable(int node) {
        for (int i = 0; i < Main.BITS; i++) {
            if (node == 0) {
                fingerTable[i] = 0;
            }
            else {
                int powerOfTwo = ((Double) Math.pow(2, i)).intValue();
                int iterator = (identifier + powerOfTwo) % Main.TOTAL_KEYS;
                fingerTable[i] = findKeyOn(iterator, 0);
            }
        }
    }

    /**
     * Rearrange the finger table when a different node joins the circle
     *
     * @param identifier the identifier of the node calling the function
     * @param oldFingerTable the previous version of the finger table of the node
     * @param nodeWhoJoined the node that just joined the circle
     * @param itsSuccessor the successor of the new node
     * @return the updated finger table
     */
    public Integer[] fingerTableAfterJoin(int identifier, Integer[] oldFingerTable, int nodeWhoJoined, int
            itsSuccessor) {

        Integer[] newFingerTable = new Integer[Main.BITS];

        for (int i = 0; i < Main.BITS; i++) {
            int powerOfTwo = ((Double) Math.pow(2, i)).intValue();
            int iterator = (identifier + powerOfTwo) % Main.TOTAL_KEYS;

            if (oldFingerTable[i] == itsSuccessor && iterator <= nodeWhoJoined)
                newFingerTable[i] = nodeWhoJoined;
            else
                newFingerTable[i] = oldFingerTable[i];
        }

        return newFingerTable;
    }

    /**
     * Rearrange the finger table when a different node leaves the circle
     *
     * @param oldFingerTable the previous version of the finger table of the node
     * @param nodeWhoLeft the identifier of the node which left the circle
     * @param itsSuccessor the successor of the removed node
     * @return the updated finger table
     */
    public Integer[] fingerTableAfterLeft(Integer[] oldFingerTable, int nodeWhoLeft, int itsSuccessor) {

        Integer[] newFingerTable = new Integer[Main.BITS];

        for (int i = 0; i < Main.BITS; i++) {
            if (oldFingerTable[i] == nodeWhoLeft)
                newFingerTable[i] = itsSuccessor;
            else
                newFingerTable[i] = oldFingerTable[i];
        }

        return newFingerTable;
    }

    /**
     * Sends a message to the node to find the key give
     *
     * @param key the key to be found
     * @param node the node that will receive the request
     * @return the node where the given key can be found
     */
    private int findKeyOn(int key, int node) {
        return sendAndWait("find " + key, Coordinator.BASE_PORT + node);
    }

    /**
     * Sends a message to a node to get its predecessor
     *
     * @param node the node to be contacted
     * @return the predecessor of the node
     */
    private int getPredecessorOf(int node) {
        return sendAndWait("predecessor " + node, Coordinator.BASE_PORT + node);
    }

    /**
     * Sends a message to a node to get its successor
     *
     * @param node the node to be contacted
     * @return the successor of the node
     */
    private int getSuccessorOf(int node) {
        return sendAndWait("successor " + node, Coordinator.BASE_PORT + node);
    }

    /**
     * Sends a message to a given node and waits for an answer
     *
     * @param message a string containing a command for the node
     * @param port the port where the node can be reached
     * @return the response sent by the node
     */
    private Integer sendAndWait(String message, int port) {

        try {
            Socket sendSocket = new Socket("127.0.0.1", port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(sendSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(sendSocket.getOutputStream(), true);
            writer.println(message);

            String response = reader.readLine();
            sendSocket.close();
            return Integer.parseInt(response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sends an ACK to the Coordinator when a command execution has finished
     *
     * @param message the message informing which command has finished
     */
    private void sendAck(String message) {

        try {
            Socket sendSocket = new Socket("127.0.0.1", Coordinator.COORDINATOR_PORT);
            PrintWriter writer = new PrintWriter(sendSocket.getOutputStream(), true);
            writer.println("ack " + message);
            sendSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}