package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Services.Helper.Helper;
import com.example.controllernode.Services.Helper.IdGenerator;
import com.example.controllernode.Services.IServices.IWriterService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.stream.Stream;

@Service
public class WriterService implements IWriterService {

    IIndexRepository indexRepository;
    public WriterService(IIndexRepository indexRepository){
        this.indexRepository=indexRepository;
    }

    @Override
    public synchronized ResponseModel<String> addDocument(String dataBase, String type, String document){
        try{
            var filePath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
            if(Files.exists(Path.of(filePath))){
                int id = IdGenerator.getId(filePath);
                var result= add(Path.of(MessageFormat.format("{0}/{1}.json", filePath,id)),id,document);
                indexRepository.addToIndex(filePath,result);
                return new ResponseModel.Builder<String>(true).Result(result).build();
            } else {
                return new ResponseModel.Builder<String>(false).message("Wrong dataBase or type").build();
            }
        } catch (Exception e) {
            return new ResponseModel.Builder<String>(false).message("error happened").build();
        }
    }
    private String add(Path path,int id,String document) throws IOException {
        var newDocumentContent=new JSONObject(document);

        newDocumentContent.put("_id",id);
        Files.writeString(path,newDocumentContent.toString());
        return newDocumentContent.toString();
    }

    @Override
    public synchronized ResponseModel<Boolean> deleteDocumentById(String dataBase, String type,int id) {
        try{
            var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
            var filePath=Path.of(MessageFormat.format("NoSqlDB/DB/{0}/{1}/{2}.json", dataBase,type,id));

            var result=Files.readString(filePath);
            if(Files.deleteIfExists(filePath)){
                indexRepository.deleteFromIndex(folderPath,result);
            }else {
                return new ResponseModel.Builder<Boolean>(false).message("Wrong Id").build();
            }

            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong Id").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }
    @Override
    public synchronized ResponseModel<Boolean> deleteDocument(String dataBase, String type,String filter) {
        var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
        try(Stream<Path> paths = Files.walk(Path.of(folderPath)).filter(Files::isRegularFile)) {

            for(var path: paths.toList()){
                String result = Files.readString(path);

                if(Helper.isMatch(result,filter)){
                    Files.deleteIfExists(path);
                    indexRepository.deleteFromIndex(folderPath,result);
                }
            }

            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    @Override
    public synchronized ResponseModel<Boolean> updateDocumentById(String dataBase, String type,int id,String newDocument) {
        try{
            var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
            var filepath=Path.of(MessageFormat.format("{0}/{1}.json", folderPath,id));
            if(Files.exists(filepath)){
                var oldDocument=Files.readString(filepath);
                var updatedDocument=update(filepath,newDocument);
                indexRepository.deleteFromIndex(folderPath,oldDocument);
                indexRepository.addToIndex(folderPath,updatedDocument);
                return new ResponseModel.Builder<Boolean>(true).Result(true).build();
            }

            return new ResponseModel.Builder<Boolean>(false).message("Wrong Id").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }
    @Override
    public synchronized ResponseModel<Boolean> updateDocument(String dataBase, String type,String filter,String newDocument) {
        var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
        try(Stream<Path> paths = Files.walk(Paths.get(folderPath)).filter(Files::isRegularFile)) {

            for(var path: paths.toList()){
                String Result = Files.readString(path);

                if(Helper.isMatch(Result,filter)){
                    var updatedDocument=update(path,newDocument);
                    indexRepository.deleteFromIndex(folderPath,Result);
                    indexRepository.addToIndex(folderPath,updatedDocument);
                }
            }

            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong database or type").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    private String update(Path path,String newDocument) throws IOException {
        var fileContent=new JSONObject(Files.readString(path));
        var newDocumentContent=new JSONObject(newDocument);

        newDocumentContent.put("_id",fileContent.get("_id"));

        Files.writeString(path,newDocumentContent.toString());
        return newDocumentContent.toString();
    }
}
