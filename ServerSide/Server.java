package ServerSide;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private static List<Item> catalog = new ArrayList<Item>();
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    Map<Socket, ObjectOutputStream> sockets = new HashMap<>();
//    private boolean sendCatalog;

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
//                don't want to use multiple outputstreams for same socket
                sockets.put(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream()));
                System.out.println("incoming transmission");
                Thread sender = new Thread(new ClientSender());
                Thread reader = new Thread(new ClientReader(clientSocket));
//                sendCatalog = true;
//                sender.start();
                sender.start();
                reader.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ClientSender implements Runnable {

//        private Socket clientSocket;
//        ClientSender(Socket clientSocket) {
//            this.clientSocket = clientSocket;
//        }


        public void run() {
            try {
                for (Socket s : sockets.keySet()) {
//                            maybe initialize ios an oos once
//                            System.out.println("gets here");
                    ObjectOutputStream oos = sockets.get(s);
                    oos.reset();
                    oos.writeObject(catalog);
                    oos.flush();
//                            print statement to check if it sends to client
                    System.out.println("sending updated catalog back to client");

                }

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
//                                System.out.println(borrowItem);
                            }
                        }
                    }

                    if (selected.getType().equals("return")) {
                        for (Object s : selected.getList()) {
                            synchronized (lock2) {
                                Item returnItem = (Item) (s);
                                catalog.add(returnItem);
                            }
                        }
                    }

                    System.out.println("updated catalog should be: " + catalog.toString());

                    Thread sender = new Thread(new ClientSender());
                    sender.start();


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
