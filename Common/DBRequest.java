package Common;

import java.io.Serializable;
import java.util.ArrayList;

public class DBRequest implements Serializable {

    private String type;
    private String username;
    private String password;
    private String result;
    private String id;

    public DBRequest(String type, String username, String password, String id, String result) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.result = result;
        this.id = id;
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

    public String getId() {
        return id;
    }

}
