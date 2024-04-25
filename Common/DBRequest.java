package Common;

import ServerSide.Server;

import java.io.Serializable;

public class DBRequest implements Serializable {

    private String type;
    private String username;
    private String password;
    private Boolean result;

    public DBRequest(String type, String username, String password, Boolean result) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.result =result;
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

    public Boolean getResult() {
        return result;
    }
}
