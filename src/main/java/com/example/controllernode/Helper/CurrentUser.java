package com.example.controllernode.Helper;

import com.example.controllernode.Model.Role;
import com.example.controllernode.Model.User;

public class CurrentUser {
    static User user = null;
    static String database;

    private CurrentUser(){
        throw new AssertionError();
    }

    public static void setUser(User user) {
        CurrentUser.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static String getDatabase() {
        return database;
    }

    public static void setDatabase(String database) {
        CurrentUser.database = database;
    }
}
