package ch.heigvd.mac.hungryme.models;

public class User {
    private final String _id;
    private String _username;

    public User(String id, String username) {
        this._id = id;
        this._username = username;
    }

    public String getId() {
        return this._id;
    }

    public String getUsername() {
        return this._username;
    }
}
