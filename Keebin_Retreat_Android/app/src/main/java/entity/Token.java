package entity;

/**
 * Created by mrlef on 12/6/2016.
 */

public class Token {

    private String name;
    private String tokenData;
    private int id;


    public Token(int id, String name, String tokenData) {
        this.id = id;
        this.name = name;
        this.tokenData = tokenData;
    }

    public Token(String name, String tokenData) {
        this.name = name;
        this.tokenData = tokenData;
    }

    public Token() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenData() {
        return tokenData;
    }

    public void setTokenData(String tokenData) {
        this.tokenData = tokenData;
    }
}

