package com.example.controllernode.Services.IServices;

import com.example.controllernode.Model.ResponseModel;

public interface IWriterService {
    ResponseModel<String> addDocument(String dataBase, String type, String document);
    ResponseModel<Boolean> deleteDocumentById(String dataBase, String type,int id);
    ResponseModel<Boolean> deleteDocument(String dataBase, String type,String filter);
    ResponseModel<Boolean> updateDocumentById(String dataBase, String type,int id,String newDocument);
    ResponseModel<Boolean> updateDocument(String dataBase, String type,String filter,String newDocument);
}
