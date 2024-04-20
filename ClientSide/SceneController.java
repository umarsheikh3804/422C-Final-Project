package ClientSide;

import ServerSide.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class SceneController {

    @FXML
    public TableView tableV;
    private Stage stage = new Stage();
    private final ObservableList<Item> log;


    public SceneController() {
        this.log = FXCollections.observableArrayList();
        this.tableV = new TableView<Item>();
    }
    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
//        this.table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        this.table.setItems(log);

    }



    public void populateCatalog(List<Item> items) {
        log.addAll(items);
    }



    @FXML
    public void loginPressed(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            System.out.println(Arrays.toString(log.toArray()));
            displayCatalog();

        } catch (Exception e) {e.printStackTrace();};
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
