package ServerSide;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {

    private static List<Item> catalog = new ArrayList<Item>();
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
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
//            System.out.println("gets to reader run");
            try {
//                need to implement backend server logic to update catalog based on items checked out and borrowed
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                while (true) {
                    Request selected = (Request) (ois.readObject());

//                    technically speaking a user can't check out and borrow at the same time, but I have added 2
//                    locks nonetheless
                    if (selected.getType().equals("checkout")) {
                        for (Object s : selected.getList()) {
                            synchronized (lock1) {
                                Item borrowItem = (Item) (s);
                                catalog.remove(forTitle(borrowItem.getTitle()));
                                System.out.println(borrowItem.getTitle());
                            }
                        }
                    }

                    if (selected.getType().equals("return")) {
//                        System.out.println("gets to return handler");
                        for (Object s : selected.getList()) {
                            synchronized (lock2) {
                                Item returnItem = forTitle((String) (s));
                                catalog.remove(returnItem);
                                System.out.println(returnItem.getTitle());
                            }
                        }
                    }

                    System.out.println(Arrays.toString(catalog.toArray()));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Item forTitle(String s) {
        for (Item i : catalog) {
            if (i.getTitle().equals(s)) {
                return i;
            }
        }
        return null;
    }


}
