package com.example.controllernode.controllers;

import com.example.controllernode.Model.CurrentUser;
import com.example.controllernode.Helper.JWT;
import com.example.controllernode.Helper.NodesManger;
import com.example.controllernode.Model.DataBaseSchema;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.ISchemaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/Schema")
public class SchemaController {
    final ISchemaService schemaService;
    private static final Logger logger = LogManager.getLogger(SchemaController.class);
    public SchemaController(ISchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @PostMapping("/createDatabase")
    ResponseModel<Boolean> createDatabase(@RequestParam String dataBase) {
        try{
            var result= schemaService.createDatabase(dataBase);
            if (result.isSuccess()){
                NodesManger.updateNode(MessageFormat.format("/Schema/createDatabase?dataBase={0}", dataBase),"");
            }
            return result;
        }catch (Exception ex){
            logger.fatal("SchemaController_createDatabase Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping("/createType")
    ResponseModel<Boolean> createType(@RequestParam String dataBase,@RequestParam String type){
        try{
            var result= schemaService.createType(dataBase, type);
            if (result.isSuccess()){
                NodesManger.updateNode(MessageFormat.format("/Schema/createType?dataBase={0}&type={1}", dataBase,type),"");
            }
            return result;
        }catch (Exception ex){
            logger.fatal("SchemaController_createType Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping("/createIndex")
    ResponseModel<Boolean> createIndex(@RequestParam String dataBase,@RequestParam String type,@RequestParam String property){
        try{
            var result= schemaService.createIndex(dataBase, type, property);
            if (result.isSuccess()){
                NodesManger.updateNode(MessageFormat.format("/Schema/createIndex?dataBase={0}&type={1}&property={2}", dataBase,type,property),"");
            }
            return result;
        }catch (Exception ex){
            logger.fatal("SchemaController_createIndex Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping("/importSchema")
    ResponseModel<Boolean> importSchema(@RequestBody DataBaseSchema schema){
        try{
            var result= schemaService.importSchema(schema);
            if (result.isSuccess()){
                NodesManger.updateNode("/Schema/importSchema",schema.toString());
            }
            return result;
        }catch (Exception ex){
            logger.fatal("SchemaController_importSchema Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping("/exportSchema")
    ResponseModel<DataBaseSchema> exportSchema(@RequestParam String dataBase){
        try{
            return schemaService.exportSchema(dataBase);
        }catch (Exception ex){
            logger.fatal("SchemaController_exportSchema Exception: ",ex);
            return new ResponseModel.Builder<DataBaseSchema>(false)
                    .message("Error Happened").build();
        }
    }

    @GetMapping("/Connection")
    ResponseModel<Map<String,String>> Connection(@RequestParam String dataBase, HttpServletRequest request){
        try{
            if(schemaService.checkDatabaseExist(dataBase)){
                Map<String,String> map=new HashMap<>();
                String nodeUrl= NodesManger.getNode();
                var currentUser=(CurrentUser)request.getAttribute("CurrentUser");
                map.put("Token", JWT.createJWTWithDatabase(dataBase,nodeUrl,currentUser));
                map.put("NodeBaseUrl", nodeUrl);
                return new ResponseModel.Builder<Map<String,String>>(true).Result(map).build();
            }

            return new ResponseModel.Builder<Map<String,String>>(false).message("Database Not Found").build();
        }catch (Exception ex){
            logger.fatal("SchemaController_Connection Exception: ",ex);
            return new ResponseModel.Builder<Map<String,String>>(false)
                    .message("Error Happened").build();
        }
    }
}
