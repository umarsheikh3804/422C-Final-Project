package ServerSide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Request<T> implements Serializable {
    private ArrayList<T> list;
    private String type;

    public Request(ArrayList<T> list, String type) {
        this.list = list;
        this.type = type;
    }

    public List<T> getList() {
        return this.list;
    }

    public String getType() {
        return this.type;
    }
}
