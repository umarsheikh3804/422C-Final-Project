package ClientSide;

import Common.Item;
import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.apache.logging.log4j.message.Message;
import org.bson.Document;

import javax.print.Doc;
import javax.swing.*;
import java.io.File;
import java.security.MessageDigest;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class LoginController {

    @FXML
    public Button loginButton;
    @FXML
    public TextField username;
    @FXML
    public PasswordField password;
    public Button signupButton;
    public TextField username1;
    public TextField confirmPassword;
    public TextField password2;
    public Label pswdMatch;
    public Label length;
    public Label invalidLogin;
    private Stage stage;
    private static ObservableList<Item> log = FXCollections.observableArrayList();
    private MongoClient mongoClient;

    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private MediaPlayer player1 =  new MediaPlayer(new Media(new File("ClientSide/sounds/errorSound.mp3").toURI().toString()));
    private MediaPlayer player2 =  new MediaPlayer(new Media(new File("ClientSide/sounds/successSound.mp3").toURI().toString()));

    public void init(Stage primaryStage, MongoClient mongoClient, ObjectOutputStream toServer, ObjectInputStream fromServer) {
        this.stage = primaryStage;
        this.mongoClient = mongoClient;
        this.toServer = toServer;
//        System.out.println(toServer == null);
        this.fromServer = fromServer;
    }

    public void populateCatalog(ArrayList<Item> items) {
        log.addAll(items);
    }

    @FXML
    public void signupPressed(ActionEvent actionEvent) {
        if (password2.getText().length() >= 8) {

            if (password2.getText().equals(confirmPassword.getText())) {
                ClientSession session = mongoClient.startSession();
                session.startTransaction();

                try {
                    MongoDatabase database = mongoClient.getDatabase("Users");

//                    player2.play();
//                    player2.setOnEndOfMedia(() -> {
//                        player2.stop();
//                    });
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
                    Parent root = loader.load();
                    HomeController controller = loader.getController();
                    System.out.println(toServer == null);
                    controller.init(stage, mongoClient, session, log, toServer, fromServer);
                    controller.displayClientSide();

                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    md.update(password2.getText().getBytes());
//            digest bytes w/algorithm and return hashed bytes
                    byte[] hashedBytes = md.digest();

                    StringBuilder sb = new StringBuilder();
                    for (byte b : hashedBytes) {
                        sb.append(String.format("%02x", b));
                    }

                    System.out.println(username1.getText());
                    System.out.println(sb.toString());
                    // Insert a user document into the collection
                    Document userDocument = new Document()
                            .append("username", username1.getText())
                            .append("password", sb.toString())
                            .append("checkedOutBooks", null);
                    // Set checked out books and other details as needed
                    database.getCollection("library_members").insertOne(userDocument);
                    System.out.println("User added successfully!");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                length.setVisible(false);
                pswdMatch.setVisible(true);
                player1.play();
                player1.setOnEndOfMedia(() -> {
                    player1.stop();
                });
            }

        } else {
            pswdMatch.setVisible(false);
            length.setVisible(true);
            player1.play();
            player1.setOnEndOfMedia(() -> {
                player1.stop();
            });
        }


    }

    @FXML
    public void loginPressed(ActionEvent actionEvent) {
        ClientSession session = mongoClient.startSession();
        session.startTransaction();

        try {
            MongoDatabase database = mongoClient.getDatabase("Users");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getText().getBytes());
//            digest bytes w/algorithm and return hashed bytes
            byte[] hashedBytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            System.out.println(username.getText());
            System.out.println(sb.toString());
            Document query = new Document("username", username.getText()).append("password", sb.toString());
            // Insert a user document into the collection
//            check username and password in database
            FindIterable<Document> result = database.getCollection("library_members").find(query);

            boolean mismatch = true;
            for (Document d : result) {
                if (d != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
                    Parent root = loader.load();
                    HomeController controller = loader.getController();
                    System.out.println(toServer == null);
                    controller.init(stage, mongoClient, session, log, toServer, fromServer);
                    controller.displayClientSide();

                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                    mismatch = false;
                    invalidLogin.setVisible(false);
//                    player2.play();
//                    player2.setOnEndOfMedia(() -> {
//                        player2.stop();
//                    });
                    break;
                }
            }

            if (mismatch) {
                invalidLogin.setVisible(true);
                player1.play();
                player1.setOnEndOfMedia(() -> {
                    player1.stop();
                });
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}