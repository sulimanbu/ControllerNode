package com.example.controllernode.Repository.IRepositories;

import com.example.controllernode.Model.ResponseModel;

import java.io.IOException;
import java.util.List;

public interface IIndexRepository {
    void setIndexValues(String folderPath, String property) throws IOException;
    void addToIndex(String folderPath,String Document) throws IOException;
    void deleteFromIndex(String folderPath,String Document) throws IOException;
    ResponseModel<List<Object>> tryGetUsingIndex(String folderPath, String filter) throws IOException;
}
