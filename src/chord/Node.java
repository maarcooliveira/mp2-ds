package chord;

import java.io.*;
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

    /**
     * Creates a chord.Node thread and sets its initial finger table.
     */
    public Node(int identifier) {
        this.identifier = identifier;
        fingerTable = new Integer[Main.BITS];
        new Listener().start();
    }

    /**
     * Runs the chord.Node thread.
     */
    private class Listener extends Thread {
        @Override
        public void run() {
            //receive broadcast message
            //call function depending on the command received
            Socket socket = null;
            try {
                ServerSocket listener = new ServerSocket(Coordinator.BASE_PORT + identifier);
                System.out.println("Server Socket listening at node " + identifier);
                while (true) {
                    socket = listener.accept();
                    System.out.println("Connection accepted at node " + identifier);
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader receivedMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = receivedMessage.readLine();
                    String[] listArgs = msg.split(" ");
                    System.out.println("CMD " + listArgs[0] + " received at node " + identifier);

                    // join p
                    if (listArgs[0].equals("join")) {
                        System.out.println("join called");
                        joinNode();
                        System.out.println("join finished");
                    }

                    // find p k
                    else if (listArgs[0].equals("find")) {
                        int key = Integer.parseInt(listArgs[2]);

                        System.out.println("find(" + key + ") called");
                        writer.println(find(key));
//                        writer.write(find(key));
//                        writer.flush();
                        System.out.println("find(key) returned");
                    }

                    // leave p
                    else if (listArgs[0].equals("leave")) {
                        System.out.println("leave called");
                        leave();
                        System.out.println("leave returned");
                    }

                    // show p (or show all)
                    else if (listArgs[0].equals("show")) {
                        System.out.println("show called");
                        show();
                        System.out.println("show returned");
                    }

                    //
                    else if (listArgs[0].equals("joined")) {
                        int node = Integer.parseInt(listArgs[1]);
                        System.out.println("nodeEntered(" + node + ") called");
                        nodeEntered(node);
                        System.out.println("nodeEntered returned");
                    }

                    //
                    else if (listArgs[0].equals("left")) {
                        int node = Integer.parseInt(listArgs[1]);
                        System.out.println("nodeLeft(" + node + ") called");
                        nodeLeft(node);
                        System.out.println("nodeLeft returned");
                    }

                    //
                    else if (listArgs[0].equals("predecessor")) {
                        System.out.println("getPredecessor() of " + identifier + " called");
//                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                        writer.write(getPredecessor());
//                        writer.flush();
                        writer.println(getPredecessor());
                        System.out.println("getPredecessor() of " + identifier + " returned");
                    }

                    //
                    else if (listArgs[0].equals("successor")) {
                        System.out.println("getSuccessor() of " + identifier + " called");
//                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                        writer.write(getSuccessor());
//                        writer.flush();
                        writer.println(getSuccessor());
                        System.out.println("getSuccessor() of " + identifier + " returned");
                    }
//                    socket.close();
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
            successor = 0; //findKeyOn(identifier, 0);
            predecessor = 0; //getPredecessorOf(successor);
        } else {
            successor = 0;
            predecessor = 0;
        }
        //recalculateFingerTable(identifier, true);
        System.out.println("Node joined " + identifier);
    }

    public int getPredecessor() {
        System.out.println("getPredecessor() of " + identifier + " returns " + predecessor);
        return predecessor;
    }

    public int getSuccessor() {
        System.out.println("getSuccessor() of " + identifier + " returns " + successor);
        return successor;
    }

    /**
     * Finds a key on the circle.
     *
     * @param key an integer relative to a certain key of the circle (between 0 and 255).
     */
    public int find(int key) {
        System.out.println("Entered find(key)");

        if (key <= identifier && key > predecessor) {
            System.out.println("Key stored in " + identifier);
            return identifier;
        } else {
            System.out.println("Key will be searched on finger table");
        }

        Integer max = null;
        for (int k = 0; k < Main.BITS; k++) {
            if (k <= key) {
                if ((max != null && max < k) || max == null) {
                    max = k;
                }
            }
        }

        if (max == null) {
            //call find on successor
            System.out.println("Next node: successor");
            return findKeyOn(key, fingerTable[0]);
        } else {
            //call find on node max
            System.out.println("Next node: " + max);
            return findKeyOn(key, max);
        }
    }

    /**
     * Called when the coordinator node sends a "leave" message.
     */
    public void leave() {
        System.out.println("Entered leave() in " + identifier);
    }

    /**
     * Show all keys stored in the node.
     */
    public void show() {
        System.out.println("Entered show() in " + identifier);
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

    /**
     * Called by a new node that just entered. It should update its finger table to reflect the new node added.
     */
    public void nodeEntered(int addedNode) {
        System.out.println("nodeEntered(): " + identifier + " knows that node " + addedNode + " joined");
        if (addedNode != identifier) {
            if (getPredecessorOf(addedNode) == identifier) {
                System.out.println("Successor of node is now the new node");
                successor = addedNode;
            }
            if (getSuccessorOf(addedNode) == identifier) {
                System.out.println("Predecessor of node is now the new node");
                predecessor = addedNode;
            }
            System.out.println("Will recalculate fingerTable on " + identifier);
            recalculateFingerTable(addedNode, true);
            System.out.println("Finished recalculate fingerTable on " + identifier);
        }
    }

    /**
     * Called by a node that just left. It should update its finger table to reflect the node removal.
     */
    public void nodeLeft(int removedNode) {
        System.out.println("nodeLeft(): " + identifier + " knows that node " + removedNode + " left");
        if (removedNode != identifier) {
            if (removedNode == predecessor) {
                System.out.println("Predecessor of node is now the predecessor of removed node");
                predecessor = getPredecessorOf(removedNode);
            } else if (removedNode == successor) {
                System.out.println("Successor of node is now the successor of removed node");
                successor = getSuccessorOf(removedNode);
            }
            System.out.println("Will recalculate fingerTable on " + identifier);
            recalculateFingerTable(removedNode, false);
            System.out.println("Finished recalculate fingerTable on " + identifier);
        }
    }

    /**
     * Recalculates the finger table whenever a new node is either added or removed from the circle.
     */
    private void recalculateFingerTable(int node, boolean added) {
        System.out.println("Entered recalculateFingerTable() of node " + identifier + ", with node=" + node + " and added=" + added);
        for (int bit = 0; bit < Main.BITS; bit++) {
            int key = (identifier + ((Double) Math.pow(2, bit)).intValue()) % Main.TOTAL_KEYS;
//            if (node == 0) {
            fingerTable[bit] = 0;
//            }
//            else {
//                fingerTable[bit] = findKeyOn(key, 0);
//            }
        }
    }

    private int findKeyOn(int key, int node) {
        System.out.println("FindKeyOn called for key " + key + " and node " + node);
        return sendAndWait("find " + key, Coordinator.BASE_PORT + node);
    }

    private int getPredecessorOf(int node) {
        System.out.println("getPredecessorOf called for node " + node);
        return sendAndWait("predecessor " + node, Coordinator.BASE_PORT + node);
    }

    private int getSuccessorOf(int node) {
        System.out.println("getSuccessorOf called for node " + node);
        return sendAndWait("successor " + node, Coordinator.BASE_PORT + node);
    }

    /**
     * Sends a message to a given port (thread) in the system.
     *
     * @param message a string containing commands for the thread.
     * @param port    the port for that thread.
     */
    private Integer sendAndWait(String message, int port) {
        try {
            System.out.println("sendAndWait called with message " + message + " to port " + port);
            Socket socket = new Socket("127.0.0.1", port);
//            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
//            dataOutputStream.writeBytes(message);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//            dataOutputStream.close();
//            dataOutputStream.flush();
            writer.println(message);
            System.out.println("sendAndWait wrote message");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("sendAndWait will read response");
            String response = reader.readLine();

            System.out.println("sendAndWait already read response");

            socket.close();
            System.out.println("Will return response");
            return Integer.parseInt(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Will return null");
        return null;
    }

}