package com.example.controllernode.Helper;

import com.example.controllernode.Model.Role;
import com.example.controllernode.Model.User;

public class CurrentUser {
    static User user = null;

    private CurrentUser(){
        throw new AssertionError();
    }

    public static void setUser(User user) {
        CurrentUser.user = user;
    }

    public static User getUser() {
        return user;
    }
}
