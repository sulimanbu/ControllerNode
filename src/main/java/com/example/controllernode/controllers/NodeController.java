package com.example.controllernode.controllers;

import com.example.controllernode.Helper.ApiCall;
import com.example.controllernode.Helper.JWT;
import com.example.controllernode.Helper.NodesManger;
import com.example.controllernode.Model.LogInModel;
import com.example.controllernode.Model.ResponseModel;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/Node")
public class NodeController {

    @PostMapping( "/addNode")
    public ResponseModel addNode(@RequestParam String node)  {
        NodesManger.addNode(node);

        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }

    @PostMapping( "/addNodeUrl")
    public ResponseModel addNodeUrl(@RequestParam String node) {
        NodesManger.addNodeUrl(node);

        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }
}
