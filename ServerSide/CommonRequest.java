package ServerSide;


import java.io.Serializable;
import java.util.ArrayList;

public interface CommonRequest extends Serializable {

    ArrayList<Item> getCatalog();

    ArrayList<Item> getCart();

    String getType();
}
