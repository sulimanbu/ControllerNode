package com.example.controllernode.Services.IServices;

import com.example.controllernode.Model.DataBaseSchema;
import com.example.controllernode.Model.ResponseModel;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

public interface ISchemaService {
    ResponseModel<Boolean> createDatabase(String dataBase) throws IOException;
    ResponseModel<Boolean> createType(String dataBase, String type) throws IOException;
    ResponseModel<Boolean> createIndex(String dataBase, String type, String property) throws IOException;

    ResponseModel<Boolean> importSchema(DataBaseSchema schema) throws IOException;
    ResponseModel<DataBaseSchema> exportSchema(String database) throws IOException;
    boolean checkDatabaseExist(String dataBase);
    void createNewNode(String url) throws IOException, UnirestException;
}
