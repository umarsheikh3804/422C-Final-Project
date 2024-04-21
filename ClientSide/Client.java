package ClientSide;

import ServerSide.Item;
import ServerSide.MongoClientConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Application {

    private static String host = "11.20.0.187";
    protected ObjectInputStream fromServer;
    protected PrintWriter toServer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        MongoClient mongoClient = MongoClients.create(new MongoClientConnection().connectDB());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_login.fxml"));
        Parent root = loader.load();
        SceneController controller = loader.getController();
        controller.init(primaryStage, mongoClient);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            new Client().setupNetworking(controller);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupNetworking(SceneController controller) {
        try {
            Socket socket = new Socket(host, 1056);
            System.out.println("Network established");

            toServer = new PrintWriter(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());

            Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        only really need to read books from server when displaying catalog to user
//                        or showing user their current checked out books
//                        while () {
                        ArrayList<Item> catalog = (ArrayList<Item>) (fromServer.readObject());
                            controller.populateCatalog(catalog);
//                            controller.showCatalog();
//                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("populated catalog");
                    }
                }
            });

//            writerThread to send objects to the server
            Thread writerThread = new Thread(new Runnable() {
                @Override
                public void run() {
//                    only really need to send books that are checked out or returned to the server
//                    possibly user id and password for mongodb database if time permits
                }
            });

            readerThread.start();
            writerThread.start();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}
