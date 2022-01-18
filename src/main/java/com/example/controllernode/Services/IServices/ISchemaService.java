package com.example.controllernode.Services.IServices;

import com.example.controllernode.Model.DataBaseSchema;
import com.example.controllernode.Model.ResponseModel;

public interface ISchemaService {
    ResponseModel<Boolean> createDatabase(String dataBase);
    ResponseModel<Boolean> createType(String dataBase, String type);
    ResponseModel<Boolean> createIndex(String dataBase, String type, String property);

    ResponseModel<Boolean> importSchema(DataBaseSchema schema);
    ResponseModel<DataBaseSchema> exportSchema(String database);
    boolean checkDatabaseExist(String dataBase);
}
