package com.example.controllernode.Services.Services;

import com.example.controllernode.Helper.ApiCall;
import com.example.controllernode.Model.DataBaseSchema;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Model.Type;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Services.Helper.FileManger;
import com.example.controllernode.Services.Helper.IdGenerator;
import com.example.controllernode.Services.IServices.ISchemaService;
import com.example.controllernode.controllers.LogInController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class SchemaService implements ISchemaService {

    @Value("${spring.application.Data_Base_Path}")
    String Data_Base_Path;
    private static final Logger logger = LogManager.getLogger(SchemaService.class);
    IIndexRepository indexRepository;
    public SchemaService(IIndexRepository indexRepository){
        this.indexRepository=indexRepository;
    }

    @Override
    public synchronized ResponseModel<Boolean> createDatabase(String dataBase) throws IOException {
        var folderPath=Path.of(MessageFormat.format("{0}/{1}", Data_Base_Path,dataBase));

        if(Files.exists(folderPath)){
            return new ResponseModel.Builder<Boolean>(false).message("Its already created").build();
        } else {
            Files.createDirectories(folderPath);
            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }
    }
    @Override
    public synchronized ResponseModel<Boolean> createType(String dataBase, String type) throws IOException {
        var databasePath=Path.of(MessageFormat.format("{0}/{1}", Data_Base_Path,dataBase));
        var folderPath=Path.of(MessageFormat.format("{0}/{1}/{2}", Data_Base_Path,dataBase,type));

        if(!Files.exists(databasePath)){
            return new ResponseModel.Builder<Boolean>(false).message("Database not found").build();
        } else if(Files.exists(folderPath)){
            return new ResponseModel.Builder<Boolean>(false).message("Its already created").build();
        } else {
            IdGenerator.addNewType(MessageFormat.format("{0}/{1}", dataBase,type));

            Files.createDirectory(folderPath);
            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }
    }
    @Override
    public synchronized ResponseModel<Boolean> createIndex(String dataBase, String type, String property) throws IOException {
        var folderPath=MessageFormat.format("{0}/{1}/{2}", Data_Base_Path, dataBase,type);
        if(Files.exists(Path.of(folderPath))){
            var indexFolder=Path.of(MessageFormat.format("{0}/index", folderPath));
            if(!Files.exists(indexFolder)){
                Files.createDirectory(indexFolder);
            }

            indexRepository.setIndexValues(folderPath, property);
            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        } else {
            return new ResponseModel.Builder<Boolean>(false).message(MessageFormat.format("{0}/{1} Not Exist", dataBase,type)).build();
        }
    }

    @Override
    public ResponseModel<Boolean> importSchema(DataBaseSchema schema) throws IOException {
        var databaseName=schema.getName();
        var createResponse=createDatabase(databaseName);

        if(createResponse.isSuccess()){
            for (var type:schema.getTypes()){
                var typeName=type.getName();
                createType(databaseName,typeName);

                for (var index:type.getIndex()){
                    createIndex(databaseName,typeName,index);
                }
            }

        }else {
            return createResponse;
        }

        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }

    @Override
    public ResponseModel<DataBaseSchema> exportSchema(String database) throws IOException {
        try{
            var folderPath=MessageFormat.format("{0}/{1}",Data_Base_Path, database);

            List<Type> types= new ArrayList<>();
            Files.walk(Paths.get(folderPath),1).filter(Files::isDirectory)
                    .forEach(filePath -> {
                        try {
                            if(!Files.isSameFile(Path.of(folderPath),filePath)){
                                var typeName=filePath.getFileName().toString();

                                types.add(new Type(typeName,getIndexes(folderPath,typeName)));
                            }
                        } catch (IOException ex) {
                            logger.fatal("exportSchema :", ex);
                        }
            });

            DataBaseSchema schema=new DataBaseSchema(database,types);
            return new ResponseModel.Builder<DataBaseSchema>(true).Result(schema).build();
        } catch (NoSuchFileException ex){
            return new ResponseModel.Builder<DataBaseSchema>(false).message("database not found").build();
        }
    }

    private List<String> getIndexes(String folderPath,String typeName) throws IOException {
        List<String> indexes=new ArrayList<>();

        var indexPath=Path.of(MessageFormat.format("{0}/{1}/{2}", folderPath,typeName,"index"));
        if(Files.exists(indexPath)){
            Files.walk(indexPath,1).filter(Files::isRegularFile)
                    .forEach(filePath1 -> indexes.add(filePath1.getFileName().toString().replace(".json","")));
        }

        return indexes;
    }

    public boolean checkDatabaseExist(String dataBase){
        var folderPath=MessageFormat.format("{0}/{1}", Data_Base_Path,dataBase);
        return Files.exists(Path.of(folderPath));
    }

    public void createNewNode(String url) throws IOException, UnirestException {
        Map<String,String> database = new HashMap<>();
        try(Stream<Path> paths = Files.walk(Paths.get(Data_Base_Path))) {

            for (var path : paths.toList()) {
                if (Files.isRegularFile(path)){
                    String Result = FileManger.readFile(path.toString());
                    database.put(path.toString(),Result);
                }else if(Files.isDirectory(path)) {
                    database.put(path.toString(),"");
                }

            }

            if(database.isEmpty()){
                return;
            }

            ObjectMapper Obj = new ObjectMapper();
            ApiCall.post(MessageFormat.format("{0}/Schema/initDatabase",url),Obj.writeValueAsString(database));
        }
    }
}
