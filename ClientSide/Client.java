package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static String host = "11.20.0.168";
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private Scanner consoleInput = new Scanner(System.in);

    public static void main (String[] args) {
        try {
            new Client().setupNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupNetworking() {
        try {
            Socket socket = new Socket(host, 1056);
            System.out.println("Network established");

            toServer = new PrintWriter(socket.getOutputStream());
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String input;
                    try {
                        while ((input = fromServer.readLine()) != null) {
                            System.out.println("From server: " + input);
                            processRequest(input);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

//            writerThread to send objects to the server
            Thread writerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String input = consoleInput.nextLine();
                    }
                }
            });

            readerThread.start();
            writerThread.start();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    protected void processRequest(String input) {
        return;
    }
}
