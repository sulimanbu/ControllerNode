package com.example.controllernode.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class DataBaseSchema {
    String name;
    List<Type> Types;

    public DataBaseSchema() {}

    public DataBaseSchema(String name, List<Type> types) {
        this.name = name;
        Types = types;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Type> getTypes() {
        return Types;
    }

    public void setTypes(List<Type> types) {
        Types = types;
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
