package com.example.controllernode.Helper;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class NodesManger {
    static int nodeSelector =0;
    static Set<String> nodes = Collections.synchronizedSet(new HashSet<>());
    static Set<String> nodesUrl = Collections.synchronizedSet(new HashSet<>());

    public NodesManger(){
        throw new AssertionError();
    }

    public static synchronized Set<String> getNodes() {
        /*var node= nodes.get(nodeSelector);
        nodeSelector= (nodeSelector+1)% nodes.size();*/
        return nodesUrl;
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
        Thread thread = new Thread(){
            public void run(){
                try {
                    NodesManger.sendRequest(url,body);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (UnirestException e) {
                    e.printStackTrace();
                };
            }
        };

        thread.start();
    }
}
