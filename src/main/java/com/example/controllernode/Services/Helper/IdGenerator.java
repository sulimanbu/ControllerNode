package com.example.controllernode.Services.Helper;

import java.util.concurrent.ConcurrentHashMap;

public class IdGenerator {
    private IdGenerator(){
        throw new AssertionError();
    }

    private static final ConcurrentHashMap<String, Integer> ids= new ConcurrentHashMap<>();

    public static synchronized int getId(String path){
        if(ids.containsKey(path)){
            var id=ids.get(path);
            ids.put(path,id+1);
            return id;
        }

        ids.put(path,2);
        return 1;
    }

    public static void addNewType(String path){
        if(!ids.containsKey(path)){
            ids.put(path,1);
        }
    }
}
