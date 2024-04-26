package Common;

import java.io.Serializable;
import java.util.ArrayList;

public class DBRequest implements Serializable {

    private String type;
    private String username;
    private String password;
    private String result;

    public DBRequest(String type, String username, String password, String result) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.result = result;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

//    public ArrayList<Item> getCart() {return cart;}
}
