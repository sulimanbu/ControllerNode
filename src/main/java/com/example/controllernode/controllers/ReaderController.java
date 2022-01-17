package com.example.controllernode.controllers;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.IReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/api/Reader")
public class ReaderController {

    @Autowired
    IReaderService readerService;

    @GetMapping("/GetById")
    public ResponseModel GetById(@RequestParam(value = "database", required = true) String database,
                                 @RequestParam(value = "type", required = true) String type,
                                 @RequestParam(value = "id", required = true) int id) {
        return readerService.GetById(database, type, id);
    }

    @GetMapping("/Get")
    public ResponseModel Get(@RequestParam(value = "dataBase", required = true) String dataBase,
                                 @RequestParam(value = "type", required = true) String type,
                                 @RequestBody String filter) {

        return readerService.Get(dataBase, type, filter);
    }

    @GetMapping("/GetAll")
    public ResponseModel GetAll(@RequestParam(value = "dataBase", required = true) String dataBase,
                                 @RequestParam(value = "type", required = true) String type) throws IOException, ParseException {
        return readerService.GetAll(dataBase, type);
    }
}
