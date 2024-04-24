package Common;

import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable, CommonRequest {
    private ArrayList<Item> catalog;
    private ArrayList<Item> cart;
    private String type;

    public Request(ArrayList<Item> catalog, ArrayList<Item> cart, String type) {
        this.catalog = catalog;
        this.cart = cart;
        this.type = type;
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
}
