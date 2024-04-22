package ClientSide;

import ServerSide.Item;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.bson.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HomeController {
    @FXML
    public TableView tableView;
    public Button checkout_button;
    public Button logout_button1;
    public Button return_button;
    @FXML
    public ListView listView;
    public Button logout_button;
    private Stage stage;
    private ObservableList<Item> log;
    private MongoClient mongoClient;
    private ClientSession session;

    public void init(Stage primaryStage, MongoClient mongoClient, ClientSession session, ObservableList<Item> log) {
        this.stage = primaryStage;
        this.mongoClient = mongoClient;
        this.session = session;
        this.log = log;
        this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void displayCart() {
        listView.getItems().clear();
        for (Item i : log) {
            listView.getItems().add(i.getTitle());
        }

        displayCatalog();
    }

    public void displayCatalog() {
        System.out.println(tableView == null);
        tableView.getColumns().clear();

        // Set the items for the TableView
        tableView.setItems(log);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        tableView.setEditable(true);

        // Define columns and set cell value factories
//        TableColumn<Item, String> itemTypeColumn = new TableColumn<>("Item Type");
//        itemTypeColumn.setCellValueFactory(log -> log.getValue().itemTypeProperty());

        TableColumn<Item, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(log -> log.getValue().titleProperty());

        TableColumn<Item, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(log -> log.getValue().authorProperty());

        TableColumn<Item, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(log -> {
            return log.getValue().imageProperty(); // Assuming you have a method to get the image
        });

        tableView.getColumns().addAll(titleColumn, authorColumn, imageColumn);

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
            LoginController controller = loader.getController();
            controller.init(stage, mongoClient);

//          for some reason stage is null here, what do I do?
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
