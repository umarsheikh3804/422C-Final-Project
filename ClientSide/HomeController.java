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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    public void init(Stage primaryStage, MongoClient mongoClient, ClientSession session, ObservableList<Item> log, ObjectOutputStream toServer, ObjectInputStream fromServer) {
        this.stage = primaryStage;
        this.mongoClient = mongoClient;
        this.session = session;
        this.log = log;
        this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.fromServer = fromServer;
        this.toServer = toServer;
        System.out.println(toServer == null);
    }

    public void displayClientSide() {
        listView.getItems().clear();
        for (Item i : log) {
            listView.getItems().add(i.getTitle());
        }

        tableView.getColumns().clear();

        // Set the items for the TableView
        tableView.setItems(log);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

    @FXML
    public void checkout_clicked(ActionEvent actionEvent) throws IOException {
        ObservableList<Item> selected = tableView.getSelectionModel().getSelectedItems();
        System.out.println(toServer == null);
//        System.out.println(Arrays.toString(selected.toArray()));
        List<Item> toSend = new ArrayList<>();
        for (Item i : selected) {
            toSend.add(i);
        }
        System.out.println(Arrays.toString(toSend.toArray()));
        toServer.reset();

        toServer.writeObject(toSend);
        toServer.flush();
    }

    public void return_clicked(ActionEvent actionEvent) {
        ObservableList<String> selected = listView.getSelectionModel().getSelectedItems();
        for (String s : selected) {
            System.out.println(s);
        }
    }

    @FXML
    public void logout_clicked(ActionEvent actionEvent) {
        System.out.println("logout clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.init(stage, mongoClient, toServer, fromServer);

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
