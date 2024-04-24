package ServerSide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Request<T> implements Serializable {
    private ArrayList<T> catalog;
    private ArrayList<T> cart;
    private String type;

    public Request(ArrayList<T> catalog, ArrayList<T> cart, String type) {
        this.catalog = catalog;
        this.cart = cart;
        this.type = type;
    }

    public ArrayList<T> getCatalog() {
        return this.catalog;
    }

    public ArrayList<T> getCart() {
        return this.cart;
    }

    public String getType() {
        return this.type;
    }
}
