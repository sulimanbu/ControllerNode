package com.example.controllernode.controllers;

import com.example.controllernode.Helper.NodesManger;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.ISchemaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Node")
public class NodeController {

    final ISchemaService schemaService;
    private static final Logger logger = LogManager.getLogger(NodeController.class);
    public NodeController(ISchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @PostMapping( "/addNode")
    public ResponseModel<Boolean> addNode(@RequestParam String node)  {
        try{

            schemaService.createNewNode(node);
            NodesManger.addNode(node);

            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }catch (Exception ex){
            logger.fatal("NodeController_addNode Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping( "/addNodeUrl")
    public ResponseModel<Boolean> addNodeUrl(@RequestParam String node) {
        try{
            NodesManger.addNodeUrl(node);

            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }catch (Exception ex){
            logger.fatal("NodeController_addNodeUrl Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

}
