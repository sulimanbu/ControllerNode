package com.example.controllernode.Services.IServices;

import com.example.controllernode.Model.ResponseModel;

import java.io.IOException;

public interface IWriterService {
    ResponseModel<String> addDocument(String dataBase, String type, String document) throws IOException;
    ResponseModel<Boolean> deleteDocumentById(String dataBase, String type,int id) throws IOException;
    ResponseModel<Boolean> deleteDocument(String dataBase, String type,String filter) throws IOException;
    ResponseModel<Boolean> updateDocumentById(String dataBase, String type,int id,String newDocument) throws IOException;
    ResponseModel<Boolean> updateDocument(String dataBase, String type,String filter,String newDocument) throws IOException;
}
