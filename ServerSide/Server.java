package ServerSide;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private static List<Item> catalog = new ArrayList<Item>();
    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
//        Scanner fs = new Scanner(new File("input.txt"));
//        while (fs.hasNextLine()) {
//            String line = fs.nextLine();
//            String[] arr = line.split(",");
//            catalog.add(new Item(arr[0], arr[1], arr[2], arr[3]))
//        }

        catalog.add(new Item("Book", "The Road", "Cormac McCarthy", 200, "", "Billy", null, null, new URL("file:images/TR.jpg")));
//        catalog.add(new Item(new Description("Book", "The Great Gatsby", "", 200, ""), "Bob", null, null, null));
        catalog.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", 200, "", "Joe", null, null, new URL("file:images/CR.jpg")));
        catalog.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", 200, "", "Sam", null, null, new URL("file:images/HP.jpg")));
        // Create a collection for users

//        MongoDatabase database = new MongoClientConnection().connectDB();
//        database.createCollection("library_members");

        new Server().setupNetworking();
    }


    private void setupNetworking() {
        try {
            ServerSocket server = new ServerSocket(1056);
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("incoming transmission");

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ClientHandler implements Runnable {

        private Socket clientSocket;
        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.reset();
                oos.writeObject(catalog);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
