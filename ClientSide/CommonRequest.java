package ClientSide;

import ClientSide.Item;

import java.io.Serializable;
import java.util.ArrayList;

public interface CommonRequest extends Serializable {

    public ArrayList<Item> getCatalog();

    public ArrayList<Item> getCart();

    public String getType();
}
