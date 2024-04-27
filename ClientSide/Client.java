package ClientSide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {

    private static String host = "localhost";
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            setupNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();

        controller.init(primaryStage, toServer, fromServer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupNetworking() {
        try {
            Socket socket = new Socket(host, 1056);
            System.out.println("Network established");

            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
