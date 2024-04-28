package ServerSide;

import Common.DBRequest;
import Common.Item;
import Common.Request;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
//import static org.junit.Assert.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerTest {
    private static final String URI = "mongodb+srv://umarsheikh4804:u1Q6b5NZfcgCICu4@cluster0.w2ijtfr.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final String DB = "Users";
    private static final String COLLECTION = "testUsers";
    private static final String COLLECTION2 = "testItems";

    private static MongoClient mongo;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static MongoCollection<Item> itemsCollection;

    @BeforeEach
    public void init() {
        System.out.println(itemsCollection == null);
        mongo = MongoClients.create(URI);

        database = mongo.getDatabase(DB);
        collection = database.getCollection(COLLECTION);
        if (collection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        itemsCollection = database.withCodecRegistry(pojoCodecRegistry).getCollection(COLLECTION2, Item.class);
        if (itemsCollection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

    }

    @Test
    public void checkoutTest() {

        Item selectedBook = new Item("Book", "The Road", "Cormac McCarthy", "English", "Fantasy", "ServerSide/images/TR.jpg", true);

        ArrayList<Item> catalog = new ArrayList<>();
        catalog.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", "English", "Mystery", "ServerSide/images/CR.jpg", true));
        catalog.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", "English", "Fantasy", "ServerSide/images/HP.jpg", true));
        catalog.add(selectedBook);

        ArrayList<Item> selected = new ArrayList<>();
        selected.add(selectedBook);

        ArrayList<Item> cart = new ArrayList<>();
        cart.add(new Item("Book", "Harry Potter and the Deathly Hallows", "JK Rowling", "English", "Fantasy", "ServerSide/images/DH.jpeg", false));
        cart.add(new Item("DVD", "Harry Potter and the Chamber of Secrets", "JK Rowling", "English", "Fantasy", "ServerSide/images/CS.jpg", false));
        Request request = new Request(selected, cart, null, null, null);

        new Server().checkoutRequestHandler(request);

        for (Item item : cart) {
            Assertions.assertFalse(item.getAvailable());
        }

        Assertions.assertTrue(cart.size() == 3);


    }

    @Test
    public void returnTest() {
        Item selectedBook = new Item("Book", "The Road", "Cormac McCarthy", "English", "Fantasy", "ServerSide/images/TR.jpg", false);

        ArrayList<Item> catalog = new ArrayList<>();
        catalog.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", "English", "Mystery", "ServerSide/images/CR.jpg", true));
        catalog.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", "English", "Fantasy", "ServerSide/images/HP.jpg", true));


        ArrayList<Item> selected = new ArrayList<>();
        selected.add(selectedBook);

        ArrayList<Item> cart = new ArrayList<>();
        cart.add(new Item("Book", "Harry Potter and the Deathly Hallows", "JK Rowling", "English", "Fantasy", "ServerSide/images/DH.jpeg", false));
        cart.add(new Item("DVD", "Harry Potter and the Chamber of Secrets", "JK Rowling", "English", "Fantasy", "ServerSide/images/CS.jpg", false));
        cart.add(selectedBook);

        Request request = new Request(selected, cart, null, null, null);

        new Server().returnRequestHandler(request);

        for (Item item : catalog) {
            Assertions.assertTrue(item.getAvailable());
        }

        Assertions.assertTrue(cart.size() == 2);

    }

    @Test
    public void updateCartTest() {
        ArrayList<Item> cart = new ArrayList<>();
        cart.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", "English", "Mystery", "ServerSide/images/CR.jpg", true));
        cart.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", "English", "Fantasy", "ServerSide/images/HP.jpg", true));
        cart.add(new Item("Book", "Harry Potter and the Deathly Hallows", "JK Rowling", "English", "Fantasy", "ServerSide/images/DH.jpeg", false));
        cart.add(new Item("DVD", "Harry Potter and the Chamber of Secrets", "JK Rowling", "English", "Fantasy", "ServerSide/images/CS.jpg", false));
        Request selected = new Request(null, cart, null, null, null);

        ArrayList<String> result = new Server().updateUserCart(selected);

        Assertions.assertArrayEquals(new String[]{"0", "1", "2", "3"}, result.toArray());

    }

    @Test
    public void addUserTest() {
//        System.out.println(collection == null);
        DBRequest request = new DBRequest("addUser", "username", "password", null, null);
        String id = new Server().addUser(request, collection);
        System.out.println(id);
        Assertions.assertNotNull(id);
    }

    @Test
    public void confirmUserTest() {

    }

    @Test
    public void addImageTest() {

    }

    @Test
    public void getEqualItemTest() {
        Item item = new Item("Book", "The Catcher in the Rye", "J.D. Salinger", "English", "Mystery", "ServerSide/images/CR.jpg", true);

        ArrayList<Item> cart = new ArrayList<>();
        cart.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", "English", "Mystery", "ServerSide/images/CR.jpg", true));
        cart.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", "English", "Fantasy", "ServerSide/images/HP.jpg", true));
        cart.add(new Item("Book", "Harry Potter and the Deathly Hallows", "JK Rowling", "English", "Fantasy", "ServerSide/images/DH.jpeg", false));
        cart.add(new Item("DVD", "Harry Potter and the Chamber of Secrets", "JK Rowling", "English", "Fantasy", "ServerSide/images/CS.jpg", false));
        cart.add(item);

        Assertions.assertEquals(new Server().getEqualItem(cart, item), item);

    }

}
