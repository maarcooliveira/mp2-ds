package chord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Defines behavior for chord.Coordinator thread.
 *
 * @author Bruno de Nadai Sarnaglia <denandai2@illinois.edu>
 * @version 1.0
 */
public class Coordinator extends Thread {

    public static final int BASE_PORT = 9000;
    private Integer[] listOfPorts = new Integer[256];

    /**
     * Creates a new coordinator and runs its thread.
     *
     * @param args all arguments needed to run coordinator (none).
     */
    public static void main(String[] args) {
        new Coordinator().start();
    }

    public void joinNode(int port) {
        listOfPorts[port] = BASE_PORT + port;
        sendMessage("join " + port, listOfPorts[port]);
        broadcast("joined " + port);
    }

    /**
     * Runs the coordinator thread.
     */
    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String input = bufferedReader.readLine();
                if (input != null && input.equals("exit")) {
                    break;
                }
                if (validInput(input)) {
                    executeCommand(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a message to a given port (thread) in the system.
     *
     * @param message a string containing commands for the thread.
     * @param port    the port for that thread.
     */
    private void sendMessage(String message, int port) {
        try {
            Socket socket = new Socket("127.0.0.1", port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeBytes(message);
            dataOutputStream.close();
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        Integer avoid = null;
        String[] cmd = message.split(" ");
        if (cmd.length == 2 && cmd[0].equals("joined"))
            avoid = Integer.valueOf(cmd[1]);
        for (int p = 0; p < Main.TOTAL_KEYS; p++) {
            if (listOfPorts[p] != null && (avoid == null || (avoid != null && p != avoid))) {
                sendMessage(message, listOfPorts[p]);
            }
        }
    }

    /**
     * Executes a command given by the user.
     *
     * @param input a string generated by user input.
     */
    private void executeCommand(String input) {
        String[] listArgs = input.split(" ");

        // join p

        if (listArgs[0].equals("join")) {
            int port = Integer.parseInt(listArgs[1]);
            Node n = new Node(port);
            joinNode(port);
        }

        // find p k

        if (listArgs[0].equals("find")) {
            int port = Integer.parseInt(listArgs[1]);
            if (listOfPorts[port] != null) {
                sendMessage(input, listOfPorts[port]);
            }
        }

        // leave p

        if (listArgs[0].equals("leave")) {
            int port = Integer.parseInt(listArgs[1]);
            if (listOfPorts[port] != null) {
                broadcast("joined " + port);
                sendMessage(input, listOfPorts[port]);
            }
        }

        // show p (or show all)

        if (listArgs[0].equals("show")) {
            if (!listArgs[1].equals("all")) {
                int port = Integer.parseInt(listArgs[1]);
                System.out.println("SHOW X");
                if (listOfPorts[port] != null) {
                    System.out.println("SHOW X OK");
                    sendMessage(input, listOfPorts[port]);
                }
            } else {
                broadcast("show");
            }
        }
    }

    /**
     * Checks if the input is valid.
     *
     * @param input a string generated by user input.
     * @return <em>true</em> if the input is valid, and an STDOUT message indicating the problem if <em>false</em>.
     */
    private boolean validInput(String input) {
        if (input == null) {
            System.out.println("command not valid");
            return false;
        }
        String[] listArgs = input.split(" ");

        // join p

        if (listArgs[0].equals("join")) {
            if (listArgs.length != 2) {
                System.out.println("join <node>");
                return false;
            }
            if (!isNumber(listArgs[1])) {
                System.out.println("<node> should be an integer from 0 to 255");
                return false;
            }
            return true;
        }

        // find p k

        else if (listArgs[0].equals("find")) {
            if (listArgs.length != 3) {
                System.out.println("find <node> <node>");
                return false;
            }
            if (!isNumber(listArgs[1]) || !isNumber(listArgs[2])) {
                System.out.println("<node> should be an integer from 0 to 255");
                return false;
            }
            return true;
        }

        // leave p

        else if (listArgs[0].equals("leave")) {
            if (listArgs.length != 2) {
                System.out.println("leave <node>");
                return false;
            }
            if (!isNumber(listArgs[1])) {
                System.out.println("<node> should be an integer from 0 to 255");
                return false;
            }
            return true;
        }

        // show p (or show all)

        else if (listArgs[0].equals("show")) {
            if (listArgs.length != 2) {
                System.out.println("show <node> OR show all");
                return false;
            }
            if (listArgs[1].equals("all")) {
                return true;
            }
            if (!isNumber(listArgs[1])) {
                System.out.println("<node> should be an integer from 0 to 255");
                return false;
            }
            return true;
        }
        System.out.println("command not valid");
        return false;
    }

    /**
     * Checks if a given string corresponds to a valid node number.
     *
     * @param node a string containing a node's number on the system.
     * @return <em>true</em> if the string corresponds to a number between 0 and 255 (inclusive).
     */
    private boolean isNumber(String node) {
        try {
            int i = Integer.parseInt(node);
            if (i < 0 || i > 255) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}