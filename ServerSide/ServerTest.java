package ServerSide;

import Common.DBRequest;
import Common.Item;
import Common.Request;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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

    @BeforeAll
    public void init() {
        mongo = MongoClients.create(URI);

        database = mongo.getDatabase(DB);
        collection = database.getCollection(COLLECTION);
        if (collection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        itemsCollection = database.withCodecRegistry(pojoCodecRegistry).getCollection(COLLECTION2, Item.class);
//        System.out.println(itemsCollection == null);
        if (itemsCollection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

    }

    @Test
    public void checkoutTest() {

        if (collection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

        if (itemsCollection.countDocuments() > 0) {
            itemsCollection.deleteMany(new Document());
        }

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
        itemsCollection.insertMany(cart);
        itemsCollection.insertMany(catalog);

        Request request = new Request(selected, cart, null, null, null);

        new Server().checkoutRequestHandler(request, itemsCollection, catalog);

        for (Item item : cart) {
            Assertions.assertFalse(item.getAvailable());
            System.out.println(item.getIsbn());
            System.out.println(itemsCollection == null);
            Item check = itemsCollection.find(Filters.eq("isbn", item.getIsbn())).first();
            System.out.println(check);
            Assertions.assertFalse(check.getAvailable());

        }

        Assertions.assertTrue(cart.size() == 3);
        Assertions.assertTrue(catalog.size() == 2);

    }

    @Test
    public void returnTest() {
        if (collection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

        if (itemsCollection.countDocuments() > 0) {
            itemsCollection.deleteMany(new Document());
        }

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
        itemsCollection.insertMany(cart);
        itemsCollection.insertMany(catalog);

        Request request = new Request(selected, cart, null, null, null);

        new Server().returnRequestHandler(request, itemsCollection, catalog);

        for (Item item : catalog) {
            Assertions.assertTrue(item.getAvailable());
            System.out.println(item.getIsbn());
            System.out.println(itemsCollection == null);
            Item check = itemsCollection.find(Filters.eq("isbn", item.getIsbn())).first();
            System.out.println(check);
            Assertions.assertTrue(check.getAvailable());

        }

        Assertions.assertTrue(cart.size() == 2);
        Assertions.assertTrue(catalog.size() == 3);

    }

    @Test
    public void updateCartTest() {
        if (collection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

        if (itemsCollection.countDocuments() > 0) {
            itemsCollection.deleteMany(new Document());
        }

        ArrayList<Item> cart = new ArrayList<>();
        cart.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", "English", "Mystery", "ServerSide/images/CR.jpg", false));
        cart.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", "English", "Fantasy", "ServerSide/images/HP.jpg", false));
        cart.add(new Item("Book", "Harry Potter and the Deathly Hallows", "JK Rowling", "English", "Fantasy", "ServerSide/images/DH.jpeg", false));
        cart.add(new Item("DVD", "Harry Potter and the Chamber of Secrets", "JK Rowling", "English", "Fantasy", "ServerSide/images/CS.jpg", false));
        for (int i = 0; i < cart.size(); i++) {
            cart.get(i).setIsbn(String.valueOf(i));
        }

        Request selected = new Request(null, cart, null, null, null);

        ArrayList<String> result = new Server().updateUserCart(selected);

        Assertions.assertArrayEquals(new String[]{"0", "1", "2", "3"}, result.toArray());

    }

    @Test
    public void addUserTest() {
        if (collection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

        if (itemsCollection.countDocuments() > 0) {
            itemsCollection.deleteMany(new Document());
        }

//        System.out.println(collection == null);
        DBRequest request = new DBRequest("addUser", "username", "password", null, null);
        String id = new Server().addUser(request, collection);
        System.out.println(id);
        Assertions.assertNotNull(id);
    }

    @Test
    public void confirmUserTest() {


        if (collection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

        if (itemsCollection.countDocuments() > 0) {
            itemsCollection.deleteMany(new Document());
        }

        ArrayList<Item> cart = new ArrayList<>();
        ArrayList<Item> catalog = new ArrayList<>();

        catalog.add(new Item("Book", "The Catcher in the Rye", "J.D. Salinger", "English", "Mystery", "ServerSide/images/CR.jpg", false));
        catalog.add(new Item("Book", "Harry Potter and the Sorcerer's Stone", "JK Rowling", "English", "Fantasy", "ServerSide/images/HP.jpg", false));
        catalog.add(new Item("Book", "Harry Potter and the Deathly Hallows", "JK Rowling", "English", "Fantasy", "ServerSide/images/DH.jpeg", false));
        catalog.add(new Item("DVD", "Harry Potter and the Chamber of Secrets", "JK Rowling", "English", "Fantasy", "ServerSide/images/CS.jpg", false));
        for (int i = 0; i < catalog.size(); i++) {
            catalog.get(i).setIsbn(String.valueOf(i));
        }

        itemsCollection.insertMany(catalog);

        ArrayList<String> checkedOut = new ArrayList<>();
        checkedOut.add("0");
        checkedOut.add("1");
        Document userDocument = new Document()
                .append("username", "username")
                .append("password", "password")
                .append("checkedOutBooks", checkedOut)
                .append("imageURI", "file");
        collection.insertOne(userDocument);

        DBRequest request = new DBRequest("check", "username", "password", null, null);
        List<String> res = new Server().confirmUser(request, cart, collection, itemsCollection);

        Assertions.assertArrayEquals(new Item[]{catalog.get(0), catalog.get(1)}, cart.toArray());
        Assertions.assertNotNull(res.get(0));
        Assertions.assertNotNull(res.get(1));


    }

    @Test
    public void addImageTest() {
        if (collection.countDocuments() > 0) {
            collection.deleteMany(new Document());
        }

        if (itemsCollection.countDocuments() > 0) {
            itemsCollection.deleteMany(new Document());
        }

        Document userDocument = new Document()
                .append("username", "username")
                .append("password", "password")
                .append("checkedOutBooks", null)
                .append("imageURI", "file");
        collection.insertOne(userDocument);
        Document justAdded = collection.find(Filters.eq("username", "username")).first();
        ObjectId id = justAdded.get("_id", ObjectId.class);

        DBRequest request = new DBRequest("addImage", null, null, id.toHexString(), "newFile");
        new Server().addImage(request, collection);

        String newURI = collection.find(Filters.eq("_id", id)).first().get("imageURI", String.class);
        Assertions.assertEquals("newFile", newURI);


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
