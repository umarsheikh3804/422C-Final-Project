package Common;

import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable {
    private ArrayList<Item> catalog;
    private ArrayList<Item> cart;
    private String type;

    private String username;
    private String password;
    private String id;

    public Request(ArrayList<Item> catalog, ArrayList<Item> cart, String type, String id) {
        this.catalog = catalog;
        this.cart = cart;
        this.type = type;
        this.id = id;
//        this.username = username;
//        this.password = password;
    }

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
