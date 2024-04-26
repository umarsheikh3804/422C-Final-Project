package ServerSide;
import java.io.*;
import Common.DBRequest;
import Common.Item;
import Common.Request;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

public class Server {

    private static List<Item> catalog = new ArrayList<Item>();
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    Map<Socket, ObjectOutputStream> sockets = new HashMap<>();

    private static MongoClient mongo;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static final String URI = "mongodb+srv://umarsheikh4804:u1Q6b5NZfcgCICu4@cluster0.w2ijtfr.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final String DB = "Users";
    private static final String COLLECTION = "library_members";

    private static MongoCollection<Item> itemsCollection;
    private static final String COLLECTION2 = "Catalog";

    private ClientSession session;



    public static void main(String[] args) throws MalformedURLException, FileNotFoundException {

        mongo = MongoClients.create(URI);
        database = mongo.getDatabase(DB);
        collection = database.getCollection(COLLECTION);
//        collection.deleteMany(new Document());

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        itemsCollection = database.withCodecRegistry(pojoCodecRegistry).getCollection(COLLECTION2, Item.class);

        catalog.add(new Item("Book", "The Road", "Cormac McCarthy", 200, "", "ServerSide/images/TR.jpg"));
        catalog.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", 200, "", "ServerSide/images/CR.jpg"));
        catalog.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", 200, "", "ServerSide/images/HP.jpg"));

        updateItemCollection();
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
        private String id;
        private Socket clientSocket;

        ClientResponseHandler() {
            this.response = "";
        }
        ClientResponseHandler(String response, ArrayList<Item> updatedCart, String id, Socket clientSocket) {
            this.response = response;
            this.updatedCart = updatedCart;
            this.id = id;
            this.clientSocket = clientSocket;

        }
        public void run() {
            try {
                synchronized (lock1) {
                    if (response.equals("dbRequest")) {
//                        send to dbHandler
                        ObjectOutputStream oos = sockets.get(clientSocket);
                        oos.reset();
                        oos.writeObject(new Request((ArrayList<Item>) catalog, updatedCart, response, id));
                        oos.flush();
                    } else {
                        for (Socket s : sockets.keySet()) {
                            ObjectOutputStream oos = sockets.get(s);
                            oos.reset();
                            oos.writeObject(new Request((ArrayList<Item>) catalog, updatedCart, response, null));
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
                session = mongo.startSession();
                session.startTransaction();
//                MongoDatabase database = mongoClient.getDatabase("Users");
//                need to implement backend server logic to update catalog based on items checked out and borrowed
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                while (true) {
                    Object request = ois.readObject();
                    if (request instanceof Request) {
                        Request selected = (Request) (request);

                        System.out.println(selected.getId());
//                        need to get cart and in database to update for use

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

//                        System.out.println(selected.getId());
//                        Document filter = new Document("_id", selected.getId());

                        ArrayList<String> newList = new ArrayList<String>();
                        for (Item i : selected.getCart()) {
                            newList.add(i.getIsbn());
                        }
                        System.out.println(Arrays.toString(newList.toArray()));


                        collection.updateOne(
                                Filters.eq("_id", new ObjectId(selected.getId())),
                                Updates.combine(
                                        Updates.set("checkedOutBooks", newList)
                                )
                        );
//

                        updateItemCollection();
                        new Thread(new ClientResponseHandler("clientRequest", selected.getCart(), null, clientSocket)).start();

                    } else {
                        DBRequest dbRequest = (DBRequest) (request);
                        if (dbRequest.getType().equals("addUser")) {
                            Document userDocument = new Document()
                                    .append("username", dbRequest.getUsername())
                                    .append("password", dbRequest.getPassword())
                                    .append("checkedOutBooks", new ArrayList<String>());
                            // Set checked out books and other details as needed
                            collection.insertOne(userDocument);
                            System.out.println("User added successfully!");
                        }

                        if (dbRequest.getType().equals("check")) {
                            System.out.println("gets here");
                            Document query = new Document("username", dbRequest.getUsername()).append("password", dbRequest.getPassword());
                            MongoCursor cursor = collection.find(query).cursor();
                            int available = cursor.available();
                            boolean found = (available > 0);

//                            gets cart for associated username and password and sends it back to client
                            String id = null;
                            ArrayList<Item> cart = new ArrayList<>();
                            if (found) {
                                Document document = (Document) cursor.next();
                                id = document.get("_id", ObjectId.class).toHexString();
                                System.out.println(id);
                                ArrayList<String> isbns = document.get("checkedOutBooks", ArrayList.class);
                                System.out.println(Arrays.toString(isbns.toArray()));
                                if (!isbns.isEmpty()) {
                                    for (String isbn : isbns) {
                                        MongoCursor itemCursor = itemsCollection.find(Filters.eq("isbn", isbn)).cursor();
                                        Item toAdd = (Item)(itemCursor.next());
                                        System.out.println(toAdd);
                                        cart.add(toAdd);
                                    }
                                }
                            }

                            new Thread(new ClientResponseHandler("dbRequest", cart, id, clientSocket)).start();
                        }

                    }
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

    private static void updateItemCollection() {
        if (itemsCollection.countDocuments() > 0) {
            itemsCollection.deleteMany(new Document());
        }
        if (!catalog.isEmpty()) {
            itemsCollection.insertMany(catalog);
        }
    }



}
