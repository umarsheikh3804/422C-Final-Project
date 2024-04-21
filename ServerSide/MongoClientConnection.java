package ServerSide;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;

public class MongoClientConnection {
    public MongoClientSettings connectDB() {
        String connectionString = "mongodb+srv://umarsheikh4804:u1Q6b5NZfcgCICu4@cluster0.w2ijtfr.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        return settings;
        // Create a new client and connect to the server
//        try (MongoClient mongoClient = MongoClients.create(settings)) {
//            try {
////                // Send a ping to confirm a successful connection
////                MongoDatabase database = mongoClient.getDatabase("Users");
////                database.runCommand(new Document("ping", 1));
////                return database;
////                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
//
//            } catch (MongoException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
    }
}
