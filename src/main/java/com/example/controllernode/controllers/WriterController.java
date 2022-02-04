package com.example.controllernode.controllers;

import com.example.controllernode.Helper.NodesManger;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.Helper.Helper;
import com.example.controllernode.Services.IServices.IWriterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping("/api/Writer")
public class WriterController {

    final IWriterService writerService;
    private static final Logger logger = LogManager.getLogger(WriterController.class);
    public WriterController(IWriterService writerService) {
        this.writerService = writerService;
    }

    @PostMapping("/addDocument")
    ResponseModel<String> addDocument(@RequestParam String dataBase,@RequestParam String type, @RequestBody String document){
        try{
            if(!Helper.isValidJSON(document)){
                return new ResponseModel.Builder<String>(false).message("Not Valid Json").build();
            }

            var result= writerService.addDocument(dataBase, type, document);
            if (result.isSuccess()){
                NodesManger.updateNode(MessageFormat.format("/Writer/addDocument?dataBase={0}&type={1}", dataBase,type),document);
            }
            return result;
        }catch (Exception ex){
            logger.fatal("WriterController-addDocument Exception: ",ex);
            return new ResponseModel.Builder<String>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping("/deleteDocumentById")
    ResponseModel<Boolean> deleteDocumentById(@RequestParam String dataBase,@RequestParam String type,@RequestParam int id){
        try{
            var result= writerService.deleteDocumentById(dataBase, type, id);
            if (result.isSuccess()){
                NodesManger.updateNode(MessageFormat.format("/Writer/deleteDocumentById?dataBase={0}&type={1}&id={2}", dataBase,type,id),"");
            }
            return result;
        }catch (Exception ex){
            logger.fatal("WriterController-deleteDocumentById Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping("/deleteDocument")
    ResponseModel<Boolean> deleteDocument(@RequestParam String dataBase,@RequestParam String type,@RequestBody String filter){
        try{
            if(!Helper.isValidJSON(filter)){
                return new ResponseModel.Builder<Boolean>(false).message("Not Valid filter Json").build();
            }

            var result= writerService.deleteDocument(dataBase, type, filter);
            if (result.isSuccess()){
                NodesManger.updateNode(MessageFormat.format("/Writer/deleteDocument?dataBase={0}&type={1}", dataBase,type),filter);
            }
            return result;
        }catch (Exception ex){
            logger.fatal("WriterController-deleteDocument Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }

    }

    @PostMapping("/updateDocumentById")
    ResponseModel<Boolean> updateDocumentById(@RequestParam String dataBase,@RequestParam String type,@RequestParam int id,@RequestBody String newDocument){
        try{
            if(!Helper.isValidJSON(newDocument)){
                return new ResponseModel.Builder<Boolean>(false).message("Not Valid Json").build();
            }

            var result= writerService.updateDocumentById(dataBase, type, id, newDocument);
            if (result.isSuccess()){
                NodesManger.updateNode(MessageFormat.format("/Writer/updateDocumentById?dataBase={0}&type={1}&id={2}", dataBase,type,id),newDocument);
            }
            return result;
        }catch (Exception ex){
            logger.fatal("WriterController-updateDocumentById Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping("/updateDocument")
    ResponseModel<Boolean> updateDocument(@RequestParam String dataBase, @RequestParam String type, @RequestBody String filterDocument){
        try{
            var json=new JSONObject(filterDocument);

            if(!Helper.isValidJSON(json.getJSONObject("newDocument").toString())){
                return new ResponseModel.Builder<Boolean>(false).message("Not Valid Json").build();
            }

            var result= writerService.updateDocument(dataBase, type, json.getJSONObject("filter").toString(), json.getJSONObject("newDocument").toString());

            if (result.isSuccess()){
                NodesManger.updateNode(MessageFormat.format("/Writer/updateDocument?dataBase={0}&type={1}", dataBase,type),filterDocument);
            }

            return result;
        }catch (JSONException ex){
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Not Valid Json").build();
        }catch (Exception ex){
            logger.fatal("WriterController-updateDocument Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }
}
