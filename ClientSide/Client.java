package ClientSide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Application {

    private static String host = "11.20.0.186";
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private Scanner consoleInput = new Scanner(System.in);
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


        SceneController controller = loader.getController();
        controller.init(primaryStage);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupNetworking() {
        try {
            Socket socket = new Socket(host, 1056);
            System.out.println("Network established");

            toServer = new PrintWriter(socket.getOutputStream());
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String input;
                    try {
                        while ((input = fromServer.readLine()) != null) {
                            System.out.println("From server: " + input);
                            processRequest(input);
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
                    while (true) {
                        String input = consoleInput.nextLine();
                    }
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
