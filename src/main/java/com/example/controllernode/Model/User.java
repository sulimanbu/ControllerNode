package com.example.controllernode.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class User {
    private String username;
    private String password;
    private Role role;

    public User() {}

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        try {
            ObjectMapper Obj = new ObjectMapper();
            return Obj.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
