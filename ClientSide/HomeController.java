package ClientSide;

import Common.Item;
import Common.Request;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    public TableView<Item> tableView;
    public Button checkout_button;
    public Button return_button;
    @FXML
    public ListView<Item> listView;
    public Button logout_button;
    @FXML
    public Button search_button;
    public TextField search_text;
    private Stage stage;
    private ObservableList<Item> log;
    private static ObservableList<Item> cart = FXCollections.observableArrayList();;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    private final Object lock = new Object();

    public void init(Stage primaryStage, ObservableList<Item> log, ObjectOutputStream toServer, ObjectInputStream fromServer) {
        this.stage = primaryStage;
        this.log = log;
        this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.fromServer = fromServer;
        this.toServer = toServer;
    }

    public void displayClientSide() {

//        actually need to pull
        listView.getItems().clear();
        listView.setItems(cart);

        tableView.getColumns().clear();

        // Set the items for the TableView
        tableView.setItems(log);

//        TableColumn<Item, String> isbnColumn = new TableColumn<>("isbn");
//        isbnColumn.setCellValueFactory(log -> log.getValue().isbnProperty());

        TableColumn<Item, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(log -> log.getValue().titleProperty());

        TableColumn<Item, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(log -> log.getValue().authorProperty());

        TableColumn<Item, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(log -> {
            return log.getValue().imageProperty(); // Assuming you have a method to get the image
        });

        tableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double availableWidth = newWidth.doubleValue() - 50;
            titleColumn.setPrefWidth(availableWidth * 0.4);
            authorColumn.setPrefWidth(availableWidth * 0.4);
            imageColumn.setPrefWidth(availableWidth * 0.2);
        });

        tableView.getColumns().addAll(titleColumn, authorColumn, imageColumn);

    }

    @FXML
    public void checkout_clicked(ActionEvent actionEvent) throws IOException {
        ObservableList<Item> selected = tableView.getSelectionModel().getSelectedItems();

        ArrayList<Item> toSend = new ArrayList<>(selected);
        toServer.reset();

        toServer.writeObject(new Request(toSend, new ArrayList<>(cart), "checkout"));
        toServer.flush();

        Thread t = new Thread(new ServerResponseHandler());
        t.start();
    }

    @FXML
    public void return_clicked(ActionEvent actionEvent) throws IOException {
        ObservableList<Item> selected = listView.getSelectionModel().getSelectedItems();
        toServer.reset();

        toServer.writeObject(new Request(new ArrayList<>(selected), new ArrayList<>(cart), "return"));
        toServer.flush();

        for (Item s : selected) {
            cart.remove(s);
        }

        Thread t = new Thread(new ServerResponseHandler());
        t.start();
    }



    @FXML
    public void logout_clicked(ActionEvent actionEvent) {
        System.out.println("logout clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.init(stage, toServer, fromServer);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void search_clicked(ActionEvent actionEvent) {
        tableView.setItems(log);
        String search = search_text.getText().toLowerCase().trim();
        System.out.println(search);
        System.out.println(Arrays.toString(tableView.getItems().toArray()));
        FilteredList<Item> filteredList = new FilteredList<>(tableView.getItems(), item ->
                item.getTitle().toLowerCase().contains(search) || item.getAuthor().toLowerCase().contains(search));

        tableView.setItems(filteredList);
        System.out.println(Arrays.toString(log.toArray()));
    }

    class ServerResponseHandler implements Runnable {
        @Override
        public void run() {

            try {
                synchronized (lock) {
                    Request response = (Request) (fromServer.readObject());
                    ArrayList<Item> catalog = response.getCatalog();
                    ArrayList<Item> updatedCart = response.getCart();
//                    can only update JavaFX UI elements from application thread
                    Platform.runLater(() -> {
                        log.clear();
                        log.addAll(catalog);
                        cart.clear();
                        cart.addAll(updatedCart);
                    });
//
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

}
