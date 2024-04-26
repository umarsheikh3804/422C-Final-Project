package ClientSide;

import Common.Item;
import Common.Request;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client extends Application {

    private static String host = "localhost";
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();

        controller.init(primaryStage, toServer, fromServer);
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
            fromServer = new ObjectInputStream(socket.getInputStream());

//            Thread readerThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
////                        loaded catalog/updated catalog sent from server
////                        have to take into account case where server is running and client is shut down
//                        ArrayList<Item> catalog = ((Request) (fromServer.readObject())).getCatalog();
//                        System.out.println(Arrays.toString(catalog.toArray()));
//                        catalogHolder[0] = catalog;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        System.out.println("Populated catalog");
//
//                    }
//                }
//            });
//
//            readerThread.start();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}
