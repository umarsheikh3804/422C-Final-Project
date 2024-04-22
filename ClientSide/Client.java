package ClientSide;

import ServerSide.Item;
import ServerSide.MongoClientConnection;
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
import java.util.concurrent.CountDownLatch;

public class Client extends Application {

    private static String host = "10.145.61.2";
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        final ArrayList<Item>[] catalogHolder = new ArrayList[]{new ArrayList<>()};
        try {
            setupNetworking(catalogHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MongoClient mongoClient = MongoClients.create(new MongoClientConnection().connectDB());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
//        at this point obviously the list view and tree elements will be null, the fxml is not even loaded and linked
        System.out.println((toServer == null) + " before passing to controller");
        controller.init(primaryStage, mongoClient, toServer, fromServer);
        controller.populateCatalog(catalogHolder[0]);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void setupNetworking(ArrayList<Item>[] catalogHolder) {
        try {
            Socket socket = new Socket(host, 1056);
            System.out.println("Network established");

            toServer = new ObjectOutputStream(socket.getOutputStream());
            System.out.println((toServer == null) + " at setup");
            fromServer = new ObjectInputStream(socket.getInputStream());


            Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        loaded catalog/updated catalog sent from server
                        ArrayList<Item> catalog = (ArrayList<Item>) (fromServer.readObject());
                        catalogHolder[0] = catalog;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Populated catalog");

                    }
                }
            });

            readerThread.start();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}
