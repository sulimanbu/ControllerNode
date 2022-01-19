package com.example.controllernode.controllers;

import com.example.controllernode.Helper.NodesManger;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.ISchemaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Node")
public class NodeController {

    final ISchemaService schemaService;
    public NodeController(ISchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @PostMapping( "/addNode")
    public ResponseModel<Boolean> addNode(@RequestParam String node)  {
        schemaService.createNewNode(node);
        NodesManger.addNode(node);

        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }

    @PostMapping( "/addNodeUrl")
    public ResponseModel<Boolean> addNodeUrl(@RequestParam String node) {
        NodesManger.addNodeUrl(node);

        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }

}
