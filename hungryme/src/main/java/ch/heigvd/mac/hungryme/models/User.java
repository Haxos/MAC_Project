package ch.heigvd.mac.hungryme.models;

public class User {
    private String _username;

    public User(String username) {
        this._username = username;
    }

    public String getUsername() {
        return this._username;
    }
}
