package ClientSide;

import ServerSide.Item;
import ServerSide.Request;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

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
    private ObservableList<Item> cart = FXCollections.observableArrayList();;
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
//        System.out.println(toServer == null);
    }

    public void displayClientSide() {

//        actually need to pull
        listView.getItems().clear();
        listView.setItems(cart);
//        for (Item i : log) {
//            listView.getItems().add(i);
//        }

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
//        System.out.println(toServer == null);
        ArrayList<Item> toSend = new ArrayList<>(selected);
        System.out.println(Arrays.toString(toSend.toArray()));
        toServer.reset();

        toServer.writeObject(new Request<Item>(toSend, "checkout"));
        toServer.flush();

        for (Item i : selected) {
            cart.add(i);
        }
    }

    public void return_clicked(ActionEvent actionEvent) throws IOException {
        ObservableList<Item> selected = listView.getSelectionModel().getSelectedItems();
//        System.out.println(insta);
//        System.out.println(toServer == null);
        ArrayList<Item> toSend = new ArrayList<>(selected);
//        System.out.println(Arrays.toString(toSend.toArray()));
        toServer.reset();

        toServer.writeObject(new Request<Item>(toSend, "return"));
        toServer.flush();

        for (Item s : selected) {
            listView.getItems().remove(s);
        }
    }

//    private ArrayList<Item> toItems(ObservableList<Item> selected) {
//        for (String s : selected) {
//
//        }
//    }

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
