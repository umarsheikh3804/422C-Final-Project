package ClientSide;

import Common.DBRequest;
import Common.Item;
import Common.Request;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class HomeController {
    @FXML
    public TableView<Item> tableView;
//    public Button checkout_button;
//    public Button return_button;
    @FXML
    public ListView<Item> listView;
//    public Button logout_button;
//    @FXML
//    public Button search_button;
    public TextField search_text;
    public ComboBox typeFilter;
    public ComboBox genreFilter;
    public ComboBox languageFilter;
//    public Button resetFilter;
    public ComboBox sortBy;
    public ImageView userProfile;
    private Stage stage;
    private ObservableList<Item> log;
    private ObservableList<Item> cart;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private String id;
    private String imageURI;

    private final Object lock = new Object();

    public void init(Stage primaryStage, ObservableList<Item> log, ObservableList<Item> cart, String imageURI, ObjectOutputStream toServer, ObjectInputStream fromServer, String id) {
        this.stage = primaryStage;
        this.log = log;
        this.cart = cart;
        this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.fromServer = fromServer;
        this.toServer = toServer;
        this.id = id;
        this.imageURI = imageURI;
    }

    public void displayClientSide() {
        listView.getItems().clear();
        listView.setItems(cart);

        tableView.getColumns().clear();
        tableView.setItems(log);

        loadImage();

        typeFilter.getItems().addAll(new String[]{"Book", "Ebook", "DVD", "Magazine", "Newspaper", "Music CDs", "Maps"});
        genreFilter.getItems().addAll(new String[]{"Mystery", "Fantasy", "Romance", "History", "Adventure", "Horror", "Politics", "Biography", "Science", "Food", "Art", "Poetry", "Drama"});
        languageFilter.getItems().addAll(new String[]{"English", "Spanish", "French", "Chinese", "Arabic", "Hindi", "Russian", "German", "Japanese", "Portuguese"});

        sortBy.getItems().addAll(new String[]{"Title", "Author", "Genre", "Language"});

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

        toServer.writeObject(new Request(toSend, new ArrayList<>(cart), "checkout", id, null));
        toServer.flush();

        Thread t = new Thread(new ServerResponseHandler());
        t.start();
    }

    @FXML
    public void return_clicked(ActionEvent actionEvent) throws IOException {
        ObservableList<Item> selected = listView.getSelectionModel().getSelectedItems();
        toServer.reset();

        toServer.writeObject(new Request(new ArrayList<>(selected), new ArrayList<>(cart), "return", id, null));
        toServer.flush();

        for (Item s : selected) {
            cart.remove(s);
        }

        Thread t = new Thread(new ServerResponseHandler());
        t.start();
    }



    @FXML
    public void logout_clicked(ActionEvent actionEvent) {
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
        FilteredList<Item> filteredList = new FilteredList<>(tableView.getItems(), item ->
                item.getTitle().toLowerCase().contains(search) || item.getAuthor().toLowerCase().contains(search));

        tableView.setItems(filteredList);
    }

    @FXML
    public void selectType(ActionEvent actionEvent) {
        String type = (String)(typeFilter.getSelectionModel().getSelectedItem());
        FilteredList<Item> filteredList = new FilteredList<>(tableView.getItems(), item ->
                Objects.equals(item.getItemType(), type));

        tableView.setItems(filteredList);
    }

    @FXML
    public void selectLanguage(ActionEvent actionEvent) {
//        System.out.println(languageFilter.isPressed());
        String type = (String)(languageFilter.getSelectionModel().getSelectedItem());
        FilteredList<Item> filteredList = new FilteredList<>(tableView.getItems(), item ->
                Objects.equals(item.getLanguage(), type));

        tableView.setItems(filteredList);
    }

    @FXML
    public void selectGenre(ActionEvent actionEvent) {
        String type = (String)(genreFilter.getSelectionModel().getSelectedItem());
        FilteredList<Item> filteredList = new FilteredList<>(tableView.getItems(), item ->
                Objects.equals(item.getGenre(), type));

        tableView.setItems(filteredList);
    }

    public void resetClicked(ActionEvent actionEvent) {
        typeFilter.setValue("Type");
        languageFilter.setValue("Language");
        genreFilter.setValue("Genre");
        tableView.setItems(log);

    }

    public void selectSort(ActionEvent actionEvent) {
//        sort by title, author, genre, language
        String type = (String)(sortBy.getSelectionModel().getSelectedItem());
        System.out.println(type);
        Comparator<Item> method;
        if (type.equals("Title")) {
//            create custom comparator, based on getTitle
            method = Comparator.comparing(Item::getTitle);

        } else if (type.equals("Author")) {
            method = Comparator.comparing(Item::getAuthor);
        } else if (type.equals("Language")) {
            method = Comparator.comparing(Item::getLanguage);
        } else {
            method = Comparator.comparing(Item::getGenre);
        }
        log.sort(method);
    }

    @FXML
    public void profileSelected(MouseEvent mouseEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            imageURI = selectedFile.toURI().toString();
            loadImage();
        }

        toServer.writeObject(new DBRequest("addImage", null, null, id, imageURI));
        toServer.flush();

    }

    private void loadImage() {
        if (imageURI != null && !imageURI.equals("")) {
            Image selectedImage = new Image(imageURI);
            userProfile.setLayoutX(503); // Move the ImageView to the right
            userProfile.setLayoutY(3); // Move the ImageView down
            userProfile.setFitWidth(40);
            userProfile.setFitHeight(40);
            userProfile.setImage(selectedImage);
        }
    }


    class ServerResponseHandler implements Runnable {
        @Override
        public void run() {

            try {
                synchronized (lock) {
                    Request response = (Request) (fromServer.readObject());
                    ArrayList<Item> catalog = response.getCatalog();
                    ArrayList<Item> updatedCart = response.getCart();
                    System.out.println(Arrays.toString(catalog.toArray()));
                    System.out.println(Arrays.toString(updatedCart.toArray()));
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
