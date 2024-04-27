package ServerSide;
import java.io.*;
import Common.DBRequest;
import Common.Item;
import Common.Request;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

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

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        itemsCollection = database.withCodecRegistry(pojoCodecRegistry).getCollection(COLLECTION2, Item.class);

//        if (itemsCollection.countDocuments() > 0) {
//            itemsCollection.deleteMany(new Document());
//        }
//        if (!catalog.isEmpty()) {
//            itemsCollection.insertMany(catalog);
//        }

//      FOR LOADING EXISTING ITEMS/PERSISTENT STORAGE
        MongoCursor cursor = itemsCollection.find(Filters.empty()).cursor();
        while (cursor.hasNext()) {
            Item nextItem = (Item) cursor.next();
            if ((nextItem).getAvailable()) {
                catalog.add(nextItem);
            }
        }
//      FOR ADDING NEW ITEMS (COMMENT OUT IF NO NEW ITEMS)
//        catalog.add(new Item("Book", "The Road", "Cormac McCarthy", "English", "Fantasy", "ServerSide/images/TR.jpg", true));
//        catalog.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", "English", "Mystery", "ServerSide/images/CR.jpg", true));
//        catalog.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", "English", "Fantasy", "ServerSide/images/HP.jpg", true));
//        catalog.add(new Item("Book", "Harry Potter and the Deathly Hallows", "JK Rowling", "English", "Fantasy", "ServerSide/images/DH.jpeg", true));
//        catalog.add(new Item("DVD", "Harry Potter and the Chamber of Secrets", "JK Rowling", "English", "Fantasy", "ServerSide/images/CS.jpg", true));
//        itemsCollection.insertMany(catalog);



        new Server().setupNetworking();
    }

    private void setupNetworking() {
        try {
            ServerSocket server = new ServerSocket(1056);
            while (true) {
                Socket clientSocket = server.accept();
//                don't want to use multiple output streams for same socket
                sockets.put(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream()));
                System.out.println("incoming transmission");
                Thread req = new Thread(new ClientRequestHandler(clientSocket));
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

        private String URI;
        ClientResponseHandler(String response, ArrayList<Item> updatedCart, String id, Socket clientSocket, String URI) {
            this.response = response;
            this.updatedCart = updatedCart;
            this.id = id;
            this.clientSocket = clientSocket;
            this.URI = URI;
        }
        public void run() {
            try {
                synchronized (lock1) {
                    if (response.equals("dbResponse")) {
//                        send to dbHandler
                        ObjectOutputStream oos = sockets.get(clientSocket);
                        oos.reset();
                        oos.writeObject(new Request((ArrayList<Item>) catalog, updatedCart, response, id, URI));
                        oos.flush();
                    } else {
                        for (Socket s : sockets.keySet()) {
                            ObjectOutputStream oos = sockets.get(s);
                            oos.reset();
                            oos.writeObject(new Request((ArrayList<Item>) catalog, updatedCart, response, null, null));
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
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                while (true) {
                    Object request = ois.readObject();
                    if (request instanceof Request) {
                        Request selected = (Request) (request);

                        if (selected.getType().equals("checkout")) {
                            for (Object s : selected.getCatalog()) {
                                synchronized (lock1) {
                                    Item borrowItem = getEqualItem(catalog, (Item) (s));
                                    borrowItem.setAvailable(false);
                                    catalog.remove(borrowItem);
                                    itemsCollection.findOneAndReplace(Filters.eq("isbn", borrowItem.getIsbn()), borrowItem);
                                    selected.getCart().add(borrowItem);
                                    System.out.println(Arrays.toString(selected.getCart().toArray()));
                                }
                            }
                        }

                        if (selected.getType().equals("return")) {
                            for (Object s : selected.getCatalog()) {
                                synchronized (lock2) {
                                    Item returnItem = (Item) (s);
                                    returnItem.setAvailable(true);
                                    itemsCollection.findOneAndReplace(Filters.eq("isbn", returnItem.getIsbn()), returnItem);
                                    catalog.add(returnItem);
                                    selected.getCart().remove(getEqualItem(selected.getCart(), returnItem));
                                }
                            }
                        }

                        ArrayList<String> newList = new ArrayList<String>();
                        for (Item i : selected.getCart()) {
                            newList.add(i.getIsbn());
                        }

                        collection.updateOne(
                                Filters.eq("_id", new ObjectId(selected.getId())),
                                Updates.combine(
                                        Updates.set("checkedOutBooks", newList)
                                )
                        );

                        new Thread(new ClientResponseHandler("clientResponse", selected.getCart(), null, clientSocket, null)).start();

                    } else {
                        String id = null;
                        String imageURI = null;
                        ArrayList<Item> cart = new ArrayList<>();
                        DBRequest dbRequest = (DBRequest) (request);
                        if (dbRequest.getType().equals("addUser")) {
                            Document query = new Document("username", dbRequest.getUsername());
                            MongoCursor<Document> cursor = collection.find(query).cursor();
                            int available = cursor.available();

                            if (available == 0) {
                                Document userDocument = new Document()
                                        .append("username", dbRequest.getUsername())
                                        .append("password", dbRequest.getPassword())
                                        .append("checkedOutBooks", new ArrayList<String>())
                                        .append("imageURI", "");
                                collection.insertOne(userDocument);
                                id = collection.find(userDocument).first().get("_id", ObjectId.class).toString();
                            }
                        }

                        if (dbRequest.getType().equals("check")) {
                            Document query = new Document("username", dbRequest.getUsername()).append("password", dbRequest.getPassword());
                            MongoCursor cursor = collection.find(query).cursor();
                            int available = cursor.available();
                            boolean found = (available > 0);

//                            gets cart for associated username and password and sends it back to client
                            if (found) {
                                Document document = (Document) cursor.next();
                                id = document.get("_id", ObjectId.class).toHexString();
                                ArrayList isbns = document.get("checkedOutBooks", ArrayList.class);
                                if (!isbns.isEmpty()) {
                                    for (Object isbn : isbns) {
                                        Item toAdd = itemsCollection.find(Filters.eq("isbn", isbn)).first();
                                        cart.add(toAdd);
                                    }
                                }

                                imageURI = document.get("imageURI", String.class);
                            }

                        }

                        if (dbRequest.getType().equals("addImage")) {
                            collection.updateOne(
                                    Filters.eq("_id", new ObjectId(dbRequest.getId())),
                                    Updates.combine(
                                            Updates.set("imageURI", dbRequest.getResult())
                                    )
                            );
                        }
                        new Thread(new ClientResponseHandler("dbResponse", cart, id, clientSocket, imageURI)).start();
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
            if (i.equals(s)) {
                return i;
            }
        }
        return null;
    }

}
