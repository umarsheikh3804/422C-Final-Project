package ClientSide;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {

    private Stage stage;

    public void loginPressed(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/final_home.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {};
    }

    public void init(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void checkout_clicked(ActionEvent actionEvent) {
    }

    public void return_clicked(ActionEvent actionEvent) {
    }

    public void logout_clicked(ActionEvent actionEvent) {
    }
}
