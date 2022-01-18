package com.example.controllernode.controllers;

import com.example.controllernode.Helper.NodesManger;
import com.example.controllernode.Model.DataBaseSchema;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.ISchemaService;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/Schema")
public class SchemaController {
    final ISchemaService schemaService;

    public SchemaController(ISchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @PostMapping("/createDatabase")
    ResponseModel<Boolean> createDatabase(@RequestParam String dataBase) {
        var result= schemaService.createDatabase(dataBase);
        if (result.isSuccess()){
            NodesManger.updateNode(MessageFormat.format("/Schema/createDatabase?dataBase={0}", dataBase),"");
        }
        return result;
    }

    @PostMapping("/createType")
    ResponseModel<Boolean> createType(@RequestParam String dataBase,@RequestParam String type){
        var result= schemaService.createType(dataBase, type);
        if (result.isSuccess()){
            NodesManger.updateNode(MessageFormat.format("/Schema/createType?dataBase={0}&type={1}", dataBase,type),"");
        }
        return result;
    }

    @PostMapping("/importSchema")
    ResponseModel<Boolean> importSchema(@RequestBody DataBaseSchema schema){
        var result= schemaService.importSchema(schema);
        if (result.isSuccess()){
            NodesManger.updateNode("/Schema/importSchema",schema.toString());
        }
        return result;
    }

    @PostMapping("/exportSchema")
    ResponseModel<DataBaseSchema> exportSchema(@RequestParam String dataBase){
        var result= schemaService.exportSchema(dataBase);

        return result;
    }

    @GetMapping("/Connection")
    ResponseModel<Set<String>> Connection(@RequestParam String dataBase){
        return new ResponseModel.Builder<Set<String>>(true).Result(NodesManger.getNodes()).build();
       /* var result= schemaService.exportSchema(dataBase);

        return result;*/
    }
}
