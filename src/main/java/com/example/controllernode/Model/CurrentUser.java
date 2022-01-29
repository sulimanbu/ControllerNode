package com.example.controllernode.Model;

public class CurrentUser {
     User user = null;
     String database;

    public  void setUser(User user) {
        this.user = user;
    }

    public  User getUser() {
        return user;
    }

    public  String getDatabase() {
        return database;
    }

    public  void setDatabase(String database) {
        this.database = database;
    }
}
