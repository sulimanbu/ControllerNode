package com.example.controllernode.Model;

import java.util.List;

public class Type {
    String name;
    List<String> index;

    public Type() {}

    public Type(String name, List<String> index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIndex() {
        return index;
    }

    public void setIndex(List<String> index) {
        this.index = index;
    }
}
