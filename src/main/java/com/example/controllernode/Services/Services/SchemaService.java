package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.DataBaseSchema;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Model.Type;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Services.Helper.Helper;
import com.example.controllernode.Services.Helper.IdGenerator;
import com.example.controllernode.Services.IServices.ISchemaService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class SchemaService implements ISchemaService {

    IIndexRepository indexRepository;
    public SchemaService(IIndexRepository indexRepository){
        this.indexRepository=indexRepository;
    }

    @Override
    public synchronized ResponseModel<Boolean> createDatabase(String dataBase){
        try{
            var folderPath=MessageFormat.format("NoSqlDB/DB/{0}", dataBase);
            if(!Files.exists(Path.of(folderPath))){

                Files.createDirectories(Path.of(folderPath));
                return new ResponseModel.Builder<Boolean>(true).Result(true).build();
            } else {
                return new ResponseModel.Builder<Boolean>(false).message("Its already created").build();
            }
        } catch (Exception e) {
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }
    @Override
    public synchronized ResponseModel<Boolean> createType(String dataBase, String type){
        try{
            if(!Files.exists(Path.of(MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type)))){
                IdGenerator.addNewType(MessageFormat.format("{0}/{1}", dataBase,type));

                Files.createDirectory(Path.of(MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type)));
                return new ResponseModel.Builder<Boolean>(true).Result(true).build();
            } else {
                return new ResponseModel.Builder<Boolean>(false).message("Its already created").build();
            }
        } catch (Exception e) {
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }
    @Override
    public synchronized ResponseModel<Boolean> createIndex(String dataBase, String type, String property) {
        try{
            var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
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
        } catch (Exception e) {
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    @Override
    public ResponseModel<Boolean> importSchema(DataBaseSchema schema) {
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
    public ResponseModel<DataBaseSchema> exportSchema(String database) {
        try{
            var folderPath=MessageFormat.format("NoSqlDB/DB/{0}", database);

            List<Type> types= new ArrayList<>();
            Files.walk(Paths.get(folderPath),1).filter(Files::isDirectory).forEach(filePath -> {
                try {
                    if(!Files.isSameFile(Path.of(folderPath),filePath)){
                        var typeName=filePath.getFileName().toString();

                        types.add(new Type(typeName,getIndexes(folderPath,typeName)));
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            });

            DataBaseSchema schema=new DataBaseSchema(database,types);
            return new ResponseModel.Builder<DataBaseSchema>(true).Result(schema).build();
        } catch (NoSuchFileException ex){
            return new ResponseModel.Builder<DataBaseSchema>(false).message("database not found").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<DataBaseSchema>(false).message("error happened").build();
        }
    }

    private List<String> getIndexes(String folderPath,String typeName) throws IOException {
        List<String> indexes=new ArrayList<>();

        var indexPath=Path.of(MessageFormat.format("{0}/{1}/{2}", folderPath,typeName,"index"));
        if(Files.exists(indexPath)){
            Files.walk(indexPath,1).filter(Files::isRegularFile)
                    .forEach(filePath1 -> {
                        indexes.add(filePath1.getFileName().toString().replace(".json",""));
                    });
        }

        return indexes;
    }

    public boolean checkDatabaseExist(String dataBase){
        var folderPath=MessageFormat.format("NoSqlDB/DB/{0}", dataBase);
        return Files.exists(Path.of(folderPath));
    }
}
