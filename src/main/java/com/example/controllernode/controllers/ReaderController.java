package com.example.controllernode.controllers;

import com.example.controllernode.Model.CurrentUser;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.IReaderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/Reader")
public class ReaderController {

    final IReaderService readerService;
    private static final Logger logger = LogManager.getLogger(ReaderController.class);
    public ReaderController(IReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping("/GetById")
    public ResponseModel<String> GetById(@RequestParam(value = "type") String type,
                                         @RequestParam(value = "id") int id, HttpServletRequest request) {
        try{
            var currentUser=(CurrentUser)request.getAttribute("CurrentUser");
            return readerService.GetById(currentUser.getDatabase(), type, id);
        }catch (Exception ex){
            logger.fatal("ReaderController_GetById Exception: ",ex);
            return new ResponseModel.Builder<String>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping("/Get")
    public ResponseModel<List<String>> Get(@RequestParam(value = "type") String type,
                                   @RequestBody String filter, HttpServletRequest request) {
        try{
            var currentUser=(CurrentUser)request.getAttribute("CurrentUser");
            return readerService.Get(currentUser.getDatabase(), type, filter);
        }catch (Exception ex){
            logger.fatal("ReaderController_Get Exception: ",ex);
            return new ResponseModel.Builder<List<String>>(false)
                    .message("Error Happened").build();
        }
    }

    @GetMapping("/GetAll")
    public ResponseModel<List<String>> GetAll(@RequestParam(value = "type") String type, HttpServletRequest request)  {
        try{
            var currentUser=(CurrentUser)request.getAttribute("CurrentUser");
            return readerService.GetAll(currentUser.getDatabase(), type);
        }catch (Exception ex){
            logger.fatal("ReaderController_GetAll Exception: ",ex);
            return new ResponseModel.Builder<List<String>>(false)
                    .message("Error Happened").build();
        }
    }
}
