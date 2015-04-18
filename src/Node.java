import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Defines behavior for Node thread. A node stores keys usually represented on a circle, and maps which node to check to
 * find a certain key by using a finger table.
 *
 * @author Marco Andre de Oliveira <mdeoliv2@illinois.edu>
 * @version 1.0
 */
public class Node {
    int identifier, predecessor, successor;
    Integer[] fingerTable;

    /**
     * Creates a Node thread and sets its initial finger table.
     */
    public Node(int identifier) {
        this.identifier = identifier;
        fingerTable = new Integer[Main.BITS];
        new Listener().start();
    }

    /**
     * Runs the Node thread.
     */

    private class Listener extends Thread {
        @Override
        public void run() {
            //receive broadcast message
            //call function depending on the command received
            Socket socket = null;
            try {
                ServerSocket listener = new ServerSocket(Coordinator.BASE_PORT + identifier);
                System.out.println("Server Socket listener added");
                while (true) {
                    socket = listener.accept();
                    System.out.println("Connection accepted at node " + identifier);
                    BufferedReader receivedMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String[] listArgs = receivedMessage.readLine().split(" ");
                    System.out.println("CMD: " + listArgs[0]);
                    // join p
                    if (listArgs[0].equals("join")) {
                        joinNode();
                    }

                    // find p k
                    else if (listArgs[0].equals("find")) {
                        int key = Integer.parseInt(listArgs[2]);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        writer.write(find(key));
                        writer.flush();
                    }

                    // leave p
                    else if (listArgs[0].equals("leave")) {
                        leave();
                    }

                    // show p (or show all)
                    else if (listArgs[0].equals("show")) {
                        show();
                    }

                    else if(listArgs[0].equals("joined")) {
                        int node = Integer.parseInt(listArgs[1]);
                        nodeEntered(node);
                    }

                    else if(listArgs[0].equals("left")) {
                        int node = Integer.parseInt(listArgs[1]);
                        nodeLeft(node);
                    }

                    else if(listArgs[0].equals("predecessor")) {
                        System.out.println("*WILL GET PRED of " + identifier);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        writer.write(getPredecessor());
                        writer.flush();
                    }

                    else if(listArgs[0].equals("successor")) {
                        System.out.println("*WILL GET SUC of " + identifier);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        writer.write(getSuccessor());
                        writer.flush();
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
     * Makes the Node thread join the circle of 256 keys.
     *
     */
    public void joinNode() {
        if (identifier != 0) {
            successor = 0; //findKeyOn(identifier, 0);
            predecessor = 0; //getPredecessorOf(successor);
        }
        else {
            successor = 0;
            predecessor = 0;
        }
        //recalculateFingerTable(identifier, true);
        System.out.println("node joined " + identifier);
    }

    public int getPredecessor() {
        return predecessor;
    }

    public int getSuccessor() {
        return successor;
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
        System.out.println("HI");
        //TODO wait piazza response
        int firstKey = (predecessor + 1) % 256;
        int lastKey = predecessor < identifier ? identifier : identifier + 256;
        boolean first = true;

        for (int key = firstKey; key <= lastKey; key++) {
            int modularKey = key % 256;
            if (first) {
                System.out.print(modularKey);
                first = false;
            } else {
                System.out.print(" " + modularKey);
            }
        }
        System.out.println();
    }

    /**
     * Called by a new node that just entered. It should update its finger table to reflect the new node added.
     */
    public void nodeEntered(int addedNode) {
        if (addedNode != identifier) {
            if (getPredecessorOf(addedNode) == identifier) {
                successor = addedNode;
            }
            if (getSuccessorOf(addedNode) == identifier) {
                predecessor = addedNode;
            }
            recalculateFingerTable(addedNode, true);
        }
    }

    /**
     * Called by a node that just left. It should update its finger table to reflect the node removal.
     */
    public void nodeLeft(int removedNode) {
        if (removedNode != identifier) {
            if (removedNode == predecessor) {
                predecessor = getPredecessorOf(removedNode);
            } else if (removedNode == successor) {
                successor = getSuccessorOf(removedNode);
            }
            recalculateFingerTable(removedNode, false);
        }
    }

    /**
     * Recalculates the finger table whenever a new node is either added or removed from the circle.
     */
    private void recalculateFingerTable(int node, boolean added) {
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
        System.out.println("WILL SEND AND WAIT");
        return sendAndWait("find " + key, Coordinator.BASE_PORT + node);
    }

    private int getPredecessorOf(int node) {
        System.out.println("WILL SEND AND WAIT PRED of " + node);
        return sendAndWait("predecessor " + node, Coordinator.BASE_PORT + node);
    }

    private int getSuccessorOf(int node) {
        System.out.println("WILL SEND AND SUC");
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
            System.out.println("SENDING");
            Socket socket = new Socket("127.0.0.1", port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeBytes(message);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine();
            System.out.println("AND");
            dataOutputStream.close();
            socket.close();
            System.out.println("WAITING");
            return Integer.parseInt(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
