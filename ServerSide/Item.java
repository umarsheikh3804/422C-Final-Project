package ServerSide;

import java.awt.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Item implements Serializable {
    private Description description;
    private String current;
    private List<String> previous;
    private Date lastCheckout;
    private Image image;

    public Item(Description description, String current, List<String> previous, Date lastCheckout, Image image) {
        this.description = description;
        this.current = current;
        this.previous = previous;
        this.lastCheckout = lastCheckout;
        this.image = image;
    }

}

