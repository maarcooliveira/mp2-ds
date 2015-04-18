package chord;

/**
 * chord.Main class.
 *
 * @author Bruno, Cassio, Marco
 * @version 1.0
 */
public class Main {

    public static final int BITS = 8;
    public static final int TOTAL_KEYS = ((Double) Math.pow(2, BITS)).intValue();
    public static final String HOST = "localhost:90";

    /**
     * chord.Main method.
     *
     * @param args all arguments needed to run the threads.
     */
    public static void main(String[] args) {
        Coordinator c = new Coordinator();
        c.start();
        Node n0 = new Node(0);
        c.joinNode(0);
    }
}