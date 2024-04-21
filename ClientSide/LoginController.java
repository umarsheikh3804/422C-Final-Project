package ClientSide;

import ServerSide.Item;
import com.mongodb.client.ClientSession;
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
import javafx.stage.Stage;
import org.bson.Document;

import java.util.List;

public class LoginController {

    @FXML
    public Button loginButton;
    @FXML
    public TextField username;
    @FXML
    public PasswordField password;
    private Stage stage = new Stage();
    private final ObservableList<Item> log = FXCollections.observableArrayList();
    private MongoClient mongoClient;

    public void init(Stage primaryStage, MongoClient mongoClient) {
        this.stage = primaryStage;
        this.mongoClient = mongoClient;
    }

    public void populateCatalog(List<Item> items) {
        log.addAll(items);
    }

    @FXML
    public void loginPressed(ActionEvent actionEvent) {
        ClientSession session = mongoClient.startSession();

        try {
            MongoDatabase database = mongoClient.getDatabase("Users");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
            Parent root = loader.load();
            HomeController controller = loader.getController();
            controller.init(stage, mongoClient, log);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            controller.displayCart();
            System.out.println(controller.listView == null);

            session.startTransaction();
            // Insert a user document into the collection
            Document userDocument = new Document()
                    .append("username", username.getText())
                    .append("password", password.getText())
                    .append("checkedOutBooks", null);
            // Set checked out books and other details as needed
            database.getCollection("library_members").insertOne(userDocument);
            System.out.println("User added successfully!");


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            mongoClient.close();
        }
    }


}