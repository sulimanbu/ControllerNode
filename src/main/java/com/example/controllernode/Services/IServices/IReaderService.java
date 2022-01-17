package com.example.controllernode.Services.IServices;

import com.example.controllernode.Model.ResponseModel;

import java.util.List;

public interface IReaderService {
    ResponseModel<String> GetById(String dataBase, String type, int id);
    ResponseModel<List<String>> Get(String dataBase, String type, String filter);
    ResponseModel<List<String>> GetAll(String dataBase, String type);
}
