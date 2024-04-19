package ClientSide;

import ServerSide.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class SceneController {

    private Stage stage;
    private List<Item> catalog;

    @FXML
    public void loginPressed(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            displayCatalog();

        } catch (Exception e) {};
    }

    public void displayCatalog() {
        for (Item i : catalog) {

        }
    }

    public void init(Stage primaryStage, List<Item> catalog) {
        this.stage = primaryStage;
        this.catalog = catalog;
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
