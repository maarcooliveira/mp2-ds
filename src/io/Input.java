package io;

import chord.Main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Class that creates inputs for the performance evaluation.
 *
 * @author Cassio dos Santos Sousa <dssntss2@illinois.edu>
 * @version 1.0
 */
public class Input {

    /**
     * Generate a given amount of random processes to be nodes in the system. 0 is excluded for already being a node.
     *
     * @param amount an integer for the number of processes to be generated.
     * @return an array list containing distinct random processes.
     */
    public ArrayList<Integer> generateRandomProcesses(int amount) {
        HashSet<Integer> distinctNodes = new HashSet<Integer>();
        Random random = new Random();
        while (distinctNodes.size() < amount) {
            int randomProcess = random.nextInt(Main.TOTAL_KEYS);
            if (randomProcess != 0)
                distinctNodes.add(randomProcess);
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.addAll(distinctNodes);
        return list;
    }

    /**
     * Generates a string that turns every node in a list into a join command.
     *
     * @param intArray an array list containing (random) processes.
     * @return a single string containing joins for all the processes in the list.
     */
    public String generateJoins(ArrayList<Integer> intArray) {
        String input = "";
        for (Integer number : intArray)
            input += "join " + number + "\n";
        return input;
    }

    /**
     * Gets a random node inside an array list.
     *
     * @param intArray an array list of (random, integer) processes.
     * @return a random process inside the list.
     */
    public int getRandomP(ArrayList<Integer> intArray) {
        return intArray.get(new Random().nextInt(intArray.size()));
    }

    /**
     * Gets a random key in the system.
     *
     * @return an integer going from 0 to 255 (in this case).
     */
    public int getRandomK() {
        return new Random().nextInt(Main.TOTAL_KEYS);
    }

    /**
     * Unites the two previous methods to create a single find command.
     *
     * @param intArray an array list that contains nodes for getRandomP.
     * @return a string with a find command.
     */
    public String getRandomFind(ArrayList<Integer> intArray) {
        return "find " + getRandomP(intArray) + " " + getRandomK();
    }

    /**
     * Generates a given amount of find commands, based on the method above.
     *
     * @param amount   an integer for the number of find commands.
     * @param intArray an array list that contains nodes for getRandomP.
     * @return a single string containing all the find commands.
     */
    public String getMultipleFinds(int amount, ArrayList<Integer> intArray) {
        String input = "";
        for (int i = 0; i < amount; i++) {
            input += getRandomFind(intArray) + "\n";
        }
        return input;
    }

    /**
     * Generates a text file for the input based on the number of nodes to be joined to the system and the number of
     * find commands.
     *
     * @param P the required amount of nodes (processes).
     * @param F the required amount of find commands.
     */
    public void generateInput(int P, int F) {
        // Creates a writer for the input
        PrintWriter input;
        try {
            input = new PrintWriter("input.txt");
            ArrayList<Integer> pRandomProcesses = generateRandomProcesses(P);
            input.print(generateJoins(pRandomProcesses));
            input.print(getMultipleFinds(F, pRandomProcesses));
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * A simplified version of the method above, that automatically generates F=64 find commands.
     *
     * @param P the required amount of nodes (processes).
     */
    public void generate64Finds(int P) {
        generateInput(P, 64);
    }
}