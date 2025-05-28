package com.example.magosdelbalon;

public class User {
    private String uid;
    private String username;
    private String email;

    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
    }

    // getters y setters
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
