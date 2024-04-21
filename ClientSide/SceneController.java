package ClientSide;

import ServerSide.Item;
import ServerSide.MongoClientConnection;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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

import java.util.Arrays;
import java.util.List;

public class SceneController {

    @FXML
    public TableView tableV;
    @FXML
    public Button checkout_button;
    @FXML
    public ListView listView;
    @FXML
    public Button return_button;
    @FXML
    public Button logout_button;
    @FXML
    public Button loginButton;
    @FXML
    public TextField username;
    @FXML
    public PasswordField password;
    private Stage stage = new Stage();
    private final ObservableList<Item> log = FXCollections.observableArrayList();;

    private final String connectionString = "mongodb+srv://umarsheikh4804:u1Q6b5NZfcgCICu4@cluster0.w2ijtfr.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

    public void init(Stage primaryStage) {
        this.stage = primaryStage;
    }



    public void populateCatalog(List<Item> items) {
        log.addAll(items);
    }



    @FXML
    public void loginPressed(ActionEvent actionEvent) {
        MongoClient mongoClient = MongoClients.create(new MongoClientConnection().connectDB());
         ClientSession session = mongoClient.startSession();
//        ClientSession session = null;
        try {
            MongoDatabase database = mongoClient.getDatabase("Users");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

//            System.out.println(Arrays.toString(log.toArray()));

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


    public void displayCart() {
//        listView.getItems().clear();
        for (Item i : log) {
            listView.getItems().add(i.getTitle());
        }
    }

    public void displayCatalog() {
        System.out.println(tableV == null);
        tableV.getColumns().clear();

        // Set the items for the TableView
        tableV.setItems(log);

        tableV.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableV.setEditable(true);

        // Define columns and set cell value factories
        TableColumn<Item, String> itemTypeColumn = new TableColumn<>("Item Type");
        itemTypeColumn.setCellValueFactory(log -> log.getValue().itemTypeProperty());

        TableColumn<Item, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(log -> log.getValue().titleProperty());

        TableColumn<Item, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(log -> log.getValue().authorProperty());

    }

    public void checkout_clicked(ActionEvent actionEvent) {
    }

    public void return_clicked(ActionEvent actionEvent) {

    }

    @FXML
    public void logout_clicked(ActionEvent actionEvent) {
        System.out.println("logout clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_login.fxml"));
            Parent root = loader.load();

//          for some reason stage is null here, what do I do?
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {e.printStackTrace();};
    }
}
