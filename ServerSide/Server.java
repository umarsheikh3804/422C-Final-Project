package ServerSide;
import java.io.*;
import Common.DBRequest;
import Common.Item;
import Common.Request;
import com.mongodb.client.*;
import org.bson.Document;

import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private static List<Item> catalog = new ArrayList<Item>();
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    Map<Socket, ObjectOutputStream> sockets = new HashMap<>();

    MongoClient mongoClient = MongoClients.create(new MongoClientConnection().connectDB());

    ClientSession session;

    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
//        Scanner fs = new Scanner(new File("input.txt"));
//        while (fs.hasNextLine()) {
//            String line = fs.nextLine();
//            String[] arr = line.split(",");
//            catalog.add(new Item(arr[0], arr[1], arr[2], arr[3]))
//        }
        catalog.add(new Item("Book", "The Road", "Cormac McCarthy", 200, "", "ServerSide/images/TR.jpg"));
        catalog.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", 200, "", "ServerSide/images/CR.jpg"));
        catalog.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", 200, "", "ServerSide/images/HP.jpg"));
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
                Thread res = new Thread(new ClientResponseHandler());
                Thread req = new Thread(new ClientRequestHandler(clientSocket));

                res.start();
                req.start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ClientResponseHandler implements Runnable {

        private final String response;
        private ArrayList<Item> updatedCart;
        private boolean result;
        private Socket clientSocket;

        ClientResponseHandler() {
            this.response = "";
        }
        ClientResponseHandler(String response, ArrayList<Item> updatedCart, boolean result, Socket clientSocket) {
            this.response = response;
            this.updatedCart = updatedCart;
            this.result = result;
            this.clientSocket = clientSocket;

        }
        public void run() {
            try {
//                we may need to put a lock here since you don't want to send the 2 different catalogs to the same client
                synchronized (lock1) {
                    if (response.equals("dbRequest")) {
//                        send to dbHandler
                        ObjectOutputStream oos = sockets.get(clientSocket);
                        oos.reset();
                        oos.writeObject(new DBRequest(response, null, null, result));
                        oos.flush();
                    } else {
                        for (Socket s : sockets.keySet()) {
                            ObjectOutputStream oos = sockets.get(s);
                            oos.reset();
                            oos.writeObject(new Request((ArrayList<Item>) catalog, updatedCart, response));
                            oos.flush();
                        }
                    }

                    System.out.println("sending updated catalog back to client");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class ClientRequestHandler implements Runnable {
        private final Socket clientSocket;

        ClientRequestHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                session = mongoClient.startSession();
                session.startTransaction();
                MongoDatabase database = mongoClient.getDatabase("Users");
//                need to implement backend server logic to update catalog based on items checked out and borrowed
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                while (true) {
                    Object request = ois.readObject();
                    if (request instanceof Request) {
                        Request selected = (Request) (request);

                        if (selected.getType().equals("checkout")) {
                            System.out.println("got checkout request");
                            for (Object s : selected.getCatalog()) {
                                synchronized (lock1) {
                                    Item borrowItem = (Item) (s);
//                                doesn't actually remove right here
                                    System.out.println(borrowItem);
                                    catalog.remove(getEqualItem(catalog, borrowItem));
                                    System.out.println("adding book to cart: " + borrowItem);
                                    selected.getCart().add(borrowItem);
                                }
                            }
                        }

                        if (selected.getType().equals("return")) {
                            for (Object s : selected.getCatalog()) {
                                synchronized (lock2) {
                                    Item returnItem = (Item) (s);
//                                doesn't actually add right here
                                    catalog.add(returnItem);
                                    selected.getCart().remove(getEqualItem(selected.getCart(), returnItem));
                                }
                            }
                        }
                        new Thread(new ClientResponseHandler("clientRequest", selected.getCart(), false, clientSocket)).start();

                    } else {
                        DBRequest dbRequest = (DBRequest) (request);
                        System.out.println(dbRequest.getUsername());
                        System.out.println(dbRequest.getPassword());
                        if (dbRequest.getType().equals("addUser")) {
                            Document userDocument = new Document()
                                    .append("username", dbRequest.getUsername())
                                    .append("password", dbRequest.getPassword())
                                    .append("checkedOutBooks", null);
                            // Set checked out books and other details as needed
                            database.getCollection("library_members").insertOne(userDocument);
                            System.out.println("User added successfully!");
                        }

                        if (dbRequest.getType().equals("check")) {
                            System.out.println("gets here");
                            Document query = new Document("username", dbRequest.getUsername()).append("password", dbRequest.getPassword());
                            FindIterable<Document> results = database.getCollection("library_members").find(query);
                            boolean result = false;
                            System.out.println(Arrays.toString(results.into(new ArrayList<>()).toArray()));
                            for (Document d : results) {
                                System.out.println("gets here 2");
                                if (d != null) {
                                    result = true;
                                    System.out.println("match found");
                                    break;
                                }
                            }

                            new Thread(new ClientResponseHandler("dbRequest", null, result, clientSocket)).start();
                        }
                    }

                    System.out.println("updated catalog should be: " + catalog.toString());

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                session.close();
            }
        }
    }

    private Item getEqualItem(List<Item> list, Item s) {
        for (Item i : list) {
            System.out.println(i);
            if (i.equals(s)) {
                System.out.println("found equal item");
                return i;
            }
        }
        return null;
    }

}
