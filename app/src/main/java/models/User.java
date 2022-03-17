package models;

import java.util.ArrayList;

public class User {
    private final String Id;
    private final String Email;
    private final String UserName;
    private final String ImageUrl;
    private ArrayList<String> Users;
    private ArrayList<String> Channels;

    public User(String id, String email, String userName, String imageUrl, ArrayList<String> users, ArrayList<String> channels) {
        Id = id;
        Email = email;
        UserName = userName;
        ImageUrl = imageUrl;
        Users = users;
        Channels = channels;
    }
    public  boolean Is_Friend(String UserId){
        return Users.contains(UserId);
    }
    public String getId() {
        return Id;
    }

    public String getEmail() {
        return Email;
    }

    public String getUserName() {
        return UserName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public ArrayList<String> getUsers() {
        return Users;
    }

    public void setUsers(ArrayList<String> users) {
        Users = users;
    }

    public ArrayList<String> getChannels() {
        return Channels;
    }

    public void setChannels(ArrayList<String> channels) {
        Channels = channels;
    }
}
