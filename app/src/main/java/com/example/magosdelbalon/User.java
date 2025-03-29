package com.example.magosdelbalon;

import java.util.List;

public class User {
    private String username;
    private String email;

    public User() {
        // Constructor vacío
    }

    //Registro
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    //Agregar Alergias
    public User(String username, String email, List<String> dietPreferences) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }



}