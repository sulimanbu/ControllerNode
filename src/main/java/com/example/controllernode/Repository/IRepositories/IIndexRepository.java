package com.example.controllernode.Repository.IRepositories;

import com.example.controllernode.Model.ResponseModel;

import java.io.IOException;
import java.util.List;

public interface IIndexRepository {
    void  setIndexValues(String folderPath, String property) throws IOException;
    List<String> addToIndex(String folderPath,String Document) throws IOException;
    List<String> deleteFromIndex(String folderPath,String Document) throws IOException;
    ResponseModel<List<Object>> tryGetUsingIndex(String folderPath, String filter) throws IOException;
}
