package com.example.controllernode.controllers;

import com.example.controllernode.Helper.CurrentUser;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.IReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Reader")
public class ReaderController {

    @Autowired
    IReaderService readerService;

    @GetMapping("/GetById")
    public ResponseModel GetById(@RequestParam(value = "type", required = true) String type,
                                 @RequestParam(value = "id", required = true) int id) {
        return readerService.GetById(CurrentUser.getDatabase(), type, id);
    }

    @GetMapping("/Get")
    public ResponseModel Get(@RequestParam(value = "type", required = true) String type,
                                 @RequestBody String filter) {

        return readerService.Get(CurrentUser.getDatabase(), type, filter);
    }

    @GetMapping("/GetAll")
    public ResponseModel GetAll(@RequestParam(value = "type", required = true) String type)  {
        return readerService.GetAll(CurrentUser.getDatabase(), type);
    }
}
