package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Repository.IRepositories.*;
import com.example.controllernode.Services.Helper.*;
import com.example.controllernode.Services.IServices.IWriterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Stream;

@Service
public class WriterService implements IWriterService {

    @Value("${spring.application.Data_Base_Path}")
    String Data_Base_Path;

    IIndexRepository indexRepository;
    IWriterRepository writerRepository;
    public WriterService(IIndexRepository indexRepository, IWriterRepository writerRepository){
        this.indexRepository=indexRepository;
        this.writerRepository = writerRepository;
    }

    @Override
    public synchronized ResponseModel<String> addDocument(String dataBase, String type, String document){
        try{
            List<String> oldVersionPath= new ArrayList<>();
            var filePath=MessageFormat.format("{0}/{1}/{2}", Data_Base_Path, dataBase,type);

            if(Files.exists(Path.of(filePath))){
                int id = IdGenerator.getId(filePath);
                var result= writerRepository.addDocument(Path.of(MessageFormat.format("{0}/{1}.json", filePath,id)),id,document);
                oldVersionPath.add(MessageFormat.format("{0}/{1}.json", filePath,id));
                var paths=indexRepository.addToIndex(filePath,result);
                oldVersionPath.addAll(paths);

                FileManger.removeFromOldVersion(oldVersionPath);
                return new ResponseModel.Builder<String>(true).Result(result).build();
            } else {
                return new ResponseModel.Builder<String>(false).message("Wrong dataBase or type").build();
            }
        } catch (Exception e) {
            return new ResponseModel.Builder<String>(false).message("error happened").build();
        }
    }

    @Override
    public synchronized ResponseModel<Boolean> deleteDocumentById(String dataBase, String type,int id) {
        try{
            var folderPath=MessageFormat.format("{0}/{1}/{2}", Data_Base_Path, dataBase,type);
            var filePath=Path.of(MessageFormat.format("{0}/{1}.json",folderPath ,id));

            List<String> oldVersionPath= new ArrayList<>();
            var result= FileManger.readFile(filePath.toString());
            FileManger.addToOldVersion(filePath.toString());
            oldVersionPath.add(filePath.toString());
            if(Files.deleteIfExists(filePath)){
                oldVersionPath.addAll(indexRepository.deleteFromIndex(folderPath,result));
            }else {
                FileManger.removeFromOldVersion(oldVersionPath);
                return new ResponseModel.Builder<Boolean>(false).message("Wrong Id").build();
            }

            FileManger.removeFromOldVersion(oldVersionPath);
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
        try {
            var filterNode = new JSONObject(filter);
            if(filterNode.has("_id")){
                return tryDeleteById(dataBase,type,filterNode.getInt("_id"));
            }

            return tryDelete(dataBase, type, filter);
        }  catch (JsonProcessingException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong Json").build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong database or type").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    private ResponseModel<Boolean> tryDeleteById(String dataBase, String type, int id){
        var result=deleteDocumentById(dataBase,type, id);
        return new ResponseModel.Builder<Boolean>(result.isSuccess()).Result(result.getResult()).message(result.getMessage()).build();
    }
    private ResponseModel<Boolean> tryDelete(String dataBase, String type,String filter) throws IOException {
        var folderPath=MessageFormat.format("{0}/{1}/{2}", Data_Base_Path, dataBase,type);

        var indexResult = indexRepository.tryGetUsingIndex(folderPath, filter);
        List<String> oldVersionPath = new ArrayList<>();

        if (indexResult.isSuccess()) {
            for (var id : indexResult.getResult()) {
                var filePath = MessageFormat.format("{0}/{1}.json", folderPath, id);
                String Result = FileManger.readFile(filePath);

                writerRepository.deleteDocument(oldVersionPath,filePath,folderPath,Result);
            }
        } else {
            deleteDocumentBySearch(indexResult.getResult(),filter,oldVersionPath,folderPath);
        }


        FileManger.removeFromOldVersion(oldVersionPath);
        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }
    private void deleteDocumentBySearch(List<Object> result,String filter, List<String> oldVersionPath,String folderPath) throws IOException {
        if (result != null) {
            for (var id : result) {
                var filePath = MessageFormat.format("{0}/{1}.json", folderPath, id);
                checkAndDelete(filePath,filter,oldVersionPath,folderPath);
            }
        } else {
            Stream<Path> paths = Files.walk(Path.of(folderPath),1).filter(Files::isRegularFile);
            for (var path : paths.toList()) {
                checkAndDelete(path.toString(),filter,oldVersionPath,folderPath);
            }
        }
    }
    private void checkAndDelete(String filePath,String filter, List<String> oldVersionPath,String folderPath) throws IOException {
        String Result = FileManger.readFile(filePath);

        if (Helper.isMatch(Result, filter)) {
            writerRepository.deleteDocument(oldVersionPath,filePath,folderPath,Result);
        }
    }

    @Override
    public synchronized ResponseModel<Boolean> updateDocumentById(String dataBase, String type,int id,String newDocument) {
        try{
            List<String> oldVersionPath= new ArrayList<>();
            var folderPath=MessageFormat.format("{0}/{1}/{2}", Data_Base_Path, dataBase,type);
            var filepath=Path.of(MessageFormat.format("{0}/{1}.json", folderPath,id));
            if(Files.exists(filepath)){
                var oldDocument=FileManger.readFile(filepath.toString());
                writerRepository.updateDocument(oldVersionPath,filepath.toString(),folderPath,oldDocument,newDocument);

                FileManger.removeFromOldVersion(oldVersionPath);
                return new ResponseModel.Builder<Boolean>(true).Result(true).build();
            }

            return new ResponseModel.Builder<Boolean>(false).message("Wrong Id").build();
        }  catch (NoSuchFileException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong database or type").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }
    @Override
    public synchronized ResponseModel<Boolean> updateDocument(String dataBase, String type,String filter,String newDocument) {

        try{
            var filterNode = new JSONObject(filter);
            if(filterNode.has("_id")){
                return tryUpdateById(dataBase,type,filterNode.getInt("_id"),newDocument);
            }

            return tryUpdate(dataBase, type, filter,newDocument);
        }  catch (JsonProcessingException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong Json").build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong database or type").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    private ResponseModel<Boolean> tryUpdateById(String dataBase, String type, int id,String newDocument){
        var result=updateDocumentById(dataBase,type, id,newDocument);
        return new ResponseModel.Builder<Boolean>(result.isSuccess()).Result(result.getResult()).message(result.getMessage()).build();
    }
    private ResponseModel<Boolean> tryUpdate(String dataBase, String type,String filter,String newDocument) throws IOException {
        var folderPath=MessageFormat.format("{0}/{1}/{2}", Data_Base_Path, dataBase,type);

        var indexResult = indexRepository.tryGetUsingIndex(folderPath,filter);
        List<String> oldVersionPath= new ArrayList<>();

        if(indexResult.isSuccess()){
            for(var id: indexResult.getResult()){
                var filePath=MessageFormat.format("{0}/{1}.json", folderPath,id);
                String Result = FileManger.readFile(filePath);

                writerRepository.updateDocument(oldVersionPath,filePath,folderPath,Result,newDocument);
            }
        }else{
            updateDocumentBySearch(indexResult.getResult(),filter,oldVersionPath,folderPath,newDocument);
        }

        FileManger.removeFromOldVersion(oldVersionPath);
        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }
    private void updateDocumentBySearch(List<Object> result,String filter, List<String> oldVersionPath,String folderPath,String newDocument) throws IOException {
        if(result != null) {
            for(var id: result){
                var filePath=MessageFormat.format("{0}/{1}.json", folderPath,id);
                checkAndUpdate(filePath,filter,oldVersionPath,folderPath,newDocument);
            }
        } else {
            Stream<Path> paths = Files.walk(Paths.get(folderPath),1).filter(Files::isRegularFile) ;

            for(var path: paths.toList()){
                checkAndUpdate(path.toString(),filter,oldVersionPath,folderPath,newDocument);
            }
        }
    }
    private void checkAndUpdate(String filePath,String filter, List<String> oldVersionPath,String folderPath,String newDocument) throws IOException {
        String Result = FileManger.readFile(filePath);

        if(Helper.isMatch(Result,filter)){
            writerRepository.updateDocument(oldVersionPath,filePath,folderPath,Result,newDocument);
        }
    }
}
