package ClientSide;

import ServerSide.Item;
import ServerSide.MongoClientConnection;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Application {

    private static String host = "11.20.0.188";
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
        LoginController controller = loader.getController();
//        at this point obviously the list view and tree elements will be null, the fxml is not even loaded and linked
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

    private void setupNetworking(LoginController controller) {
        try {
            Socket socket = new Socket(host, 1056);
            System.out.println("Network established");

            toServer = new PrintWriter(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());

            Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
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
