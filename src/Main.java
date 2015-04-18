/**
 * Main class.
 *
 * @author Bruno, Cassio, Marco
 * @version 1.0
 */
public class Main {

    public static final int BITS = 8;
    public static final int TOTAL_KEYS = ((Double) Math.pow(2, BITS)).intValue();
    public static final String HOST = "localhost:90";

    /**
     * Main method.
     *
     * @param args all arguments needed to run the threads.
     */
    public static void main(String[] args) {
        // write your code here
    }

    public static void show() {
        int firstKey = predecessor + 1;
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

}