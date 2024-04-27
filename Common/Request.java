package Common;

import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable {
    private ArrayList<Item> catalog;
    private ArrayList<Item> cart;
    private String type;

    private String id;

    private String URI;

    public Request(ArrayList<Item> catalog, ArrayList<Item> cart, String type, String id, String URI) {
        this.catalog = catalog;
        this.cart = cart;
        this.type = type;
        this.id = id;
        this.URI = URI;

    }

    public String getURI() {return this.URI; }
    public ArrayList<Item> getCatalog() {
        return this.catalog;
    }

    public ArrayList<Item> getCart() {
        return this.cart;
    }

    public String getType() {
        return this.type;
    }

    public String getId() {return this.id; }

}
