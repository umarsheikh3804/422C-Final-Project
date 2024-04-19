package ServerSide;

import javafx.scene.image.Image;

import java.awt.*;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class Item implements Serializable {
    private Description description;
    private String current;
    private List<String> previous;
    private Date lastCheckout;
    private URL image;


    public Item(Description description, String current, List<String> previous, Date lastCheckout, URL image) {
        this.description = description;
        this.current = current;
        this.previous = previous;
        this.lastCheckout = lastCheckout;
        this.image = image;
    }

    @Override
    public String toString() {
        return current + ": " + description.toString();
    }
}

