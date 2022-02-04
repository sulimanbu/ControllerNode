package com.example.controllernode.Services.IServices;

import com.example.controllernode.Model.ResponseModel;

import java.io.IOException;
import java.util.List;

public interface IReaderService {
    ResponseModel<String> GetById(String dataBase, String type, int id) throws IOException;
    ResponseModel<List<String>> Get(String dataBase, String type, String filter) throws IOException;
    ResponseModel<List<String>> GetAll(String dataBase, String type) throws IOException;
}
