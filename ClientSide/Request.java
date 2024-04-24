package ClientSide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Request<T> implements Serializable {
    private ArrayList<T> list;
    private String type;

    public Request(ArrayList<T> list, String type) {
        this.list = list;
        this.type = type;
    }

    public ArrayList<T> getList() {
        return this.list;
    }

    public String getType() {
        return this.type;
    }
}
