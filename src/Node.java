/**
 * Created by Marco Andre De Oliveira <mdeoliv2@illinois.edu>
 * Date: 4/17/15
 */
public class Node implements Runnable {
    int identifier;
    Integer[] fingerTable;

    public Node() {
        fingerTable = new Integer[8];
    }

    @Override
    public void run() {
        while(true) {
            //receive broadcast message

            //call function depending on the command received
        }
    }

    public void join() {

    }

    public void find(int key) {

    }

    public void leave() {

    }

    public void show() {
//        int firstKey = fingerTable[7] ==

        int lastKey = fingerTable[0] == null ? Main.TOTAL_KEYS : fingerTable[0];

        for(int key = 0; key < lastKey; key++) {
            System.out.println(key);
        }
    }
}
