package chord;

import io.Input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * chord.Main class.
 *
 * @author Bruno de Nadai Sarnaglia <denandai2@illinois.edu>
 * @author Cassio dos Santos Sousa <dssntss2@illinois.edu>,
 * @author Marco Andre de Oliveira <mdeoliv2@illinois.edu>
 * @version 1.0
 */
public class Main {

    public static final int BITS = 8;
    public static final int TOTAL_KEYS = ((Double) Math.pow(2, BITS)).intValue();

    /**
     * chord.Main method.
     *
     * @param args optional argument: an integer P, defining how many nodes will be generated
     *             for the performance evaluation test.
     */
    public static void main(String[] args) {

        Coordinator c = new Coordinator();
        c.start();
        Node n0 = new Node(0);
        c.joinNode(0);

        if(args.length == 1) {
            int p = Integer.valueOf(args[0]);
            Input io = new Input();
            io.generate64Finds(p);

            FileInputStream is = null;
            try {
                is = new FileInputStream(new File("input.txt"));
                System.setIn(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("input.txt generated with test operations");
        }
    }
}