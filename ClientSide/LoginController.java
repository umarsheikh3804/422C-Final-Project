package ClientSide;

import Common.DBRequest;
import Common.Item;
import javafx.application.Platform;
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
    public Label forgotPassword;
    public Hyperlink forgotLabel;
    public Label strengthMessage;
    public Label pswdMatch1;
    private Stage stage;
    private static ObservableList<Item> log = FXCollections.observableArrayList();

    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private MediaPlayer player1 =  new MediaPlayer(new Media(new File("ClientSide/sounds/errorSound.mp3").toURI().toString()));
    private MediaPlayer player2 =  new MediaPlayer(new Media(new File("ClientSide/sounds/successSound.mp3").toURI().toString()));

    public void init(Stage primaryStage, ObjectOutputStream toServer, ObjectInputStream fromServer) {
        this.stage = primaryStage;
        this.toServer = toServer;
        this.fromServer = fromServer;
//        log.addAll(items);

    }

    public void populateCatalog(ArrayList<Item> items) {
        log.addAll(items);
    }

    @FXML
    public void signupPressed(ActionEvent actionEvent) {
        if (password2.getText().length() < 8) {
            pswdMatch.setVisible(false);
            strengthMessage.setVisible(false);
            length.setVisible(true);
            player1.play();
            player1.setOnEndOfMedia(() -> {
                player1.stop();
            });
        } else if (!strengthTest(password2.getText())) {
            pswdMatch.setVisible(false);
            length.setVisible(false);
            strengthMessage.setVisible(true);
            player1.play();
            player1.setOnEndOfMedia(() -> {
                player1.stop();
            });
        } else if (!password2.getText().equals(confirmPassword.getText())) {
            length.setVisible(false);
            strengthMessage.setVisible(false);
            pswdMatch.setVisible(true);
            player1.play();
            player1.setOnEndOfMedia(() -> {
                player1.stop();
            });
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
                Parent root = loader.load();
                HomeController controller = loader.getController();
                System.out.println(toServer == null);
                controller.init(stage, log, toServer, fromServer);
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

                toServer.writeObject(new DBRequest("addUser", username1.getText(), sb.toString(), false));
                toServer.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @FXML
    public void loginPressed(ActionEvent actionEvent) {
        try {
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

            toServer.writeObject(new DBRequest("check", username.getText(), sb.toString(), false));
            toServer.flush();
            Thread dbResponseHandler = new Thread(new Runnable() {
                @Override
                public void run() {
                    DBRequest response = null;
                    try {
                        response = (DBRequest) (fromServer.readObject());
                        if (response.getResult()) {
                            System.out.println(response.getResult());
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
                            Parent root = loader.load();
                            HomeController controller = loader.getController();
                            controller.init(stage, log, toServer, fromServer);
                            controller.displayClientSide();

                            Platform.runLater(() -> {
                                Scene scene = new Scene(root);
                                stage.setScene(scene);
                                stage.show();
                                invalidLogin.setVisible(false);
                            });
                        } else {
                            Platform.runLater(() -> {
                                invalidLogin.setVisible(true);
                                player1.play();
                                player1.setOnEndOfMedia(() -> {
                                    player1.stop();
                                });
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dbResponseHandler.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean strengthTest(String input) {
        boolean containsDigit = false;
        boolean containsUpper = false;
        boolean containsLower = false;
        for (int i = 0; i < input.length(); i++) {
            if (Character.isLowerCase(input.charAt(i))) {
                containsLower = true;
            } else if (Character.isUpperCase(input.charAt(i))) {
                containsUpper = true;
            } else if (Character.isDigit(input.charAt(i))) {
                containsDigit = true;
            }
        }

        return containsDigit && containsLower && containsUpper;
    }

    public void forgotPassword(ActionEvent actionEvent) {
    }
}