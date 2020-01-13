package ch.heigvd.mac.hungryme.models;

public class User {
    private final String _id;
    private String _userName;
    private String _firstName;
    private String _lastName;

    private int _timeFast = 30;
    private int _timeVeryFast = 15;

    public User(String id, String username, String firstName, String lastName) {
        this._id = id;
        this._userName = username;
        this._firstName = firstName;
        this._lastName = lastName;
    }

    public String getId() {
        return this._id;
    }
    public String getUserName() {
        return this._userName;
    }
    public String getFirstName() {return this._firstName;}
    public String getLastName() {return this._lastName;}

    public int getTimeFast(){return this._timeFast;}
    public int getTimeVeryFast(){return this._timeVeryFast;}

    public void setTimeFast(int t){this._timeFast = t;}
    public void setTimeVeryFast(int t){this._timeVeryFast = t;}
}
