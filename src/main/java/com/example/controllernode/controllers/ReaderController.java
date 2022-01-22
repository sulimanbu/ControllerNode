package com.example.controllernode.controllers;

import com.example.controllernode.Helper.CurrentUser;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.IReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Reader")
public class ReaderController {

    final IReaderService readerService;

    public ReaderController(IReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping("/GetById")
    public ResponseModel<String> GetById(@RequestParam(value = "type") String type,
                                 @RequestParam(value = "id") int id) {
        return readerService.GetById(CurrentUser.getDatabase(), type, id);
    }

    @PostMapping("/Get")
    public ResponseModel<List<String>> Get(@RequestParam(value = "type") String type,
                                   @RequestBody String filter) {

        return readerService.Get(CurrentUser.getDatabase(), type, filter);
    }

    @GetMapping("/GetAll")
    public ResponseModel<List<String>> GetAll(@RequestParam(value = "type") String type)  {
        return readerService.GetAll(CurrentUser.getDatabase(), type);
    }
}
