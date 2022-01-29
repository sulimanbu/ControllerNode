package com.example.controllernode.controllers;

import com.example.controllernode.Model.CurrentUser;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.IReaderService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
                                         @RequestParam(value = "id") int id, HttpServletRequest request) {
        var currentUser=(CurrentUser)request.getAttribute("CurrentUser");
        return readerService.GetById(currentUser.getDatabase(), type, id);
    }

    @PostMapping("/Get")
    public ResponseModel<List<String>> Get(@RequestParam(value = "type") String type,
                                   @RequestBody String filter, HttpServletRequest request) {
        var currentUser=(CurrentUser)request.getAttribute("CurrentUser");
        return readerService.Get(currentUser.getDatabase(), type, filter);
    }

    @GetMapping("/GetAll")
    public ResponseModel<List<String>> GetAll(@RequestParam(value = "type") String type, HttpServletRequest request)  {
        var currentUser=(CurrentUser)request.getAttribute("CurrentUser");
        return readerService.GetAll(currentUser.getDatabase(), type);
    }
}
