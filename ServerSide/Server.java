package ServerSide;

import com.mongodb.client.model.InsertOneModel;
import javafx.collections.ObservableList;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
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

        catalog.add(new Item("Book", "The Road", "Cormac McCarthy", 200, "", "Billy", null, null, "ServerSide/images/TR.jpg"));
        catalog.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", 200, "", "Joe", null, null, "ServerSide/images/CR.jpg"));
        catalog.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", 200, "", "Sam", null, null, "ServerSide/images/HP.jpg"));
        // Create a collection for users


        new Server().setupNetworking();
    }


    private void setupNetworking() {
        try {
            ServerSocket server = new ServerSocket(1056);
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("incoming transmission");
                Thread sender = new Thread(new ClientSender(clientSocket));
                Thread reader = new Thread(new ClientReader(clientSocket));
                sender.start();
                reader.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ClientSender implements Runnable {

        private Socket clientSocket;
        ClientSender(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                oos.reset();
                oos.writeObject(catalog);
                oos.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class ClientReader implements Runnable {
        private Socket clientSocket;
        ClientReader(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            System.out.println("gets to reader run");
            try {
                System.out.println("before read");
//                Book book = (Book)(new ObjectInputStream(clientSocket.getInputStream()).readObject());
                ArrayList<Item> selected = (ArrayList<Item>) (new ObjectInputStream(clientSocket.getInputStream()).readObject());
                System.out.println("after read");
                for (Item s : selected) {
                    System.out.println(s.getTitle());
                }
                System.out.println("got updates");

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
