package com.example.controllernode.Helper;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class NodesManger {
    static int nodeSelector =0;
    static int connectCount = 1;
    static Set<String> nodes = Collections.synchronizedSet(new HashSet<>());
    static Set<String> nodesUrl = Collections.synchronizedSet(new HashSet<>());

    public NodesManger(){
        throw new AssertionError();
    }

    public static synchronized String getNode() {
        var controller=ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        var nodeNumber=nodesUrl.size();
        if(nodeNumber < 1){
            return controller;
        }else {
            if(connectCount == 0) {
                connectCount++;
                return controller;
            }
            else if(connectCount==nodeNumber*2)
                connectCount=0;
            else
                connectCount++;
        }

        var node=(String) nodesUrl.toArray()[nodeSelector];
        nodeSelector= (nodeSelector+1)% nodeNumber;
        return node;
    }

    public static synchronized void addNode(String node){
        nodes.add(node);
    }

    public static synchronized void addNodeUrl(String node){
        nodesUrl.add(node);
    }

    public static void sendRequest(String url,String body) throws UnsupportedEncodingException, UnirestException {
        for (var node:nodes){
            ApiCall.post(node+url,body);
        }
    }

    public static void updateNode(String url,String body){
        Thread thread = new Thread(() -> {
            try {
                NodesManger.sendRequest(url,body);
            } catch (UnsupportedEncodingException | UnirestException e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}
