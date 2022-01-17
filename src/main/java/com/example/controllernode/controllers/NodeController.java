package com.example.controllernode.controllers;

import com.example.controllernode.Helper.ApiCall;
import com.example.controllernode.Helper.JWT;
import com.example.controllernode.Helper.NodesManger;
import com.example.controllernode.Model.LogInModel;
import com.example.controllernode.Model.ResponseModel;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/Node")
public class NodeController {

    @PostMapping( "/addNode")
    public ResponseModel addNode(@RequestParam String node) throws UnsupportedEncodingException, UnirestException {
        NodesManger.addNode(node);

        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }
}
