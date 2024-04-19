package ClientSide;

import ServerSide.Item;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;

public class Client extends Application {

    private static String host = "11.20.0.187";
    protected ObjectInputStream fromServer;
    protected PrintWriter toServer;

    protected ObservableList<Item> catalog = FXCollections.observableArrayList();;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            new Client().setupNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_login.fxml"));
        Parent root = loader.load();
//

        SceneController controller = loader.getController();
        controller.init(primaryStage, catalog);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupNetworking() {
        try {
            Socket socket = new Socket(host, 1056);
            System.out.println("Network established");

            toServer = new PrintWriter(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());

            Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String input;
                    try {
//                        only really need to read books from server when displaying catalog to user
//                        or showing user their current checked out books
                        while (true) {
                            Item item = (Item) (fromServer.readObject());
                            catalog.add(item);
                            System.out.println(item);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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

    protected void processRequest(String input) {
        return;
    }
}
