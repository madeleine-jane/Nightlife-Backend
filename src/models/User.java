package models;

import java.util.ArrayList;

public class User {
    public User(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }
    public User(String firstName, String lastName, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public String firstName = null;
    public String lastName = null;
    public String username = null;
    public String password = null;
    public String profilePic = null;
    public ArrayList<User> followers;
    public ArrayList<User>following;
    public Story story = new Story();
    public Feed feed = new Feed();
}
