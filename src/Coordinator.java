import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by Bruno on 4/17/2015.
 */
public class Coordinator implements Runnable{

    private int port = 9000;
    private int[] listOfPorts = new int[256];

    public static void main(String[] args){
        new Coordinator().run();
    }



    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            try {
                String input = bufferedReader.readLine();
                if(input != null && input.equals("exit")){
                    break;
                }
                if(validInput(input)){
                    executeCommand(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeCommand(String input) {
        String[] listArgs = input.split(" ");
        if(listArgs[0].equals("join")){
            listOfPorts[Integer.parseInt(listArgs[1])] = port++;
        }
    }

    private boolean validInput(String input){
        if(input == null) {
            System.out.println("command not valid");
            return false;
        }
        String[] listArgs = input.split(" ");
        if(listArgs[0].equals("join")){
           if(listArgs.length != 2){
               System.out.println("join <node>");
               return false;
           }
            if(!isNumber(listArgs[1])){
                System.out.println("<node> should be an integer from 0 to 255");
                return false;
            }
            return true;
        }
        if(listArgs[0].equals("find")){
            if(listArgs.length != 3){
                System.out.println("find <node> <node>");
                return false;
            }
            if(!isNumber(listArgs[1]) || !isNumber(listArgs[2])){
                System.out.println("<node> should be an integer from 0 to 255");
                return false;
            }
            return true;
        }
        if(listArgs[0].equals("leave")){
            if(listArgs.length != 2){
                System.out.println("leave <node>");
                return false;
            }
            if(!isNumber(listArgs[1])){
                System.out.println("<node> should be an integer from 0 to 255");
                return false;
            }
            return true;
        }
        if(listArgs[0].equals("show")){
            if(listArgs.length != 2){
                System.out.println("show <node> OR show all");
                return false;
            }
            if(listArgs[1].equals("all")){
                return true;
            }
            if(!isNumber(listArgs[1])){
                System.out.println("<node> should be an integer from 0 to 255");
                return false;
            }
            return true;
        }
        System.out.println("command not valid");
        return false;
    }

    private boolean isNumber(String node){
        try {
            int i = Integer.parseInt(node);
            if(i < 0 || i > 255){
                return false;
            }
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }


}
