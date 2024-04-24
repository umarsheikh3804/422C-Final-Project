package Common;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Item implements Serializable {
    private String current;
    private List<String> previous;
    private Date lastCheckout;
    private String image;
    private String itemType;
    private String title;
    private String author;
    private int pages;
    private String summary;


//    need to add unique ID, in case of multiple copes do later
    public Item(String itemType, String title, String author, int pages, String summary, String current, List<String> previous, Date lastCheckout, String image) {
        this.current = current;
        this.previous = previous;
        this.lastCheckout = lastCheckout;
        this.image = image;
        this.itemType = itemType;
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.summary = summary;
    }

    public StringProperty itemTypeProperty() {
        return new SimpleStringProperty(this.itemType);
    }

    public ObservableValue<String> titleProperty() {
        return new SimpleStringProperty(this.title);
    }

    public ObservableValue<String> authorProperty() {
        return new SimpleStringProperty(this.author);
    }

    public ObservableValue<ImageView> imageProperty() {

        try {
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(200/3.0);
            return new SimpleObjectProperty<>(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getTitle() {return this.title;}

    public String getAuthor() {return this.author;}

    public int getPages() {return this.pages;}

    public String getSummary() {return this.summary;}

    public String getItemType() {return this.itemType;}

    @Override
    public boolean equals(Object other) {
        if (other instanceof Item) {
            Item otherItem = (Item)(other);
            return (this.title.equals(otherItem.title) && this.author.equals(otherItem.author) && this.pages == otherItem.pages);
        }

        return false;
    }

    @Override
    public String toString() {
        return title;
    }


}

