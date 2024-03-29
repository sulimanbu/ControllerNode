package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Services.Helper.*;
import com.example.controllernode.Services.IServices.IReaderService;
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
public class ReaderService implements IReaderService {

    @Value("${spring.application.Data_Base_Path}")
    String Data_Base_Path;

    IIndexRepository indexRepository;
    public ReaderService(IIndexRepository indexRepository){
        this.indexRepository=indexRepository;
    }

    @Override
    public ResponseModel<String> GetById(String dataBase,String type,int id) throws IOException {
        try {
            var filePath=MessageFormat.format("{0}/{1}/{2}/{3}.json",Data_Base_Path ,dataBase,type,id);
            String Result = FileManger.readFile(filePath);

            return new ResponseModel.Builder<String>(true).Result(Result).build();
        } catch (NoSuchFileException ex){
            return new ResponseModel.Builder<String>(false).message("Wrong Id").build();
        }
    }

    @Override
    public ResponseModel<List<String>> Get(String dataBase, String type, String filter) throws IOException {
        try{
            var filterNode = new JSONObject(filter);
            if(filterNode.has("_id")){
                return tryGetById(dataBase,type,filterNode.getInt("_id"));
            }

            return tryGet(dataBase, type, filter);
        }  catch (JsonProcessingException ex){
            return new ResponseModel.Builder<List<String>>(false).message("Wrong Json").build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<List<String>>(false).message("Type Not Found").build();
        }
    }

    @Override
    public ResponseModel<List<String>> GetAll(String dataBase, String type) throws IOException {
        try(Stream<Path> paths = Files.walk(Paths.get(MessageFormat.format("{0}/{1}/{2}", Data_Base_Path,dataBase,type)),1)
                .filter(Files::isRegularFile)) {

            List<String> list=new ArrayList<>();
            for(var path: paths.toList()){
                String Result = FileManger.readFile(path.toString());
                list.add(Result);
            }

            return new ResponseModel.Builder<List<String>>(true).Result(list).build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<List<String>>(false).message("Type Not Found").build();
        }
    }

    private ResponseModel<List<String>> tryGetById(String dataBase,String type,int id) throws IOException {
        List<String> list=new ArrayList<>();
        var result=GetById(dataBase,type, id);
        if(result.isSuccess()){
            list.add(result.getResult());
            return new ResponseModel.Builder<List<String>>(true).Result(list).build();
        }else
        {
            return new ResponseModel.Builder<List<String>>(false).message(result.getMessage()).build();
        }
    }
    private ResponseModel<List<String>> tryGet(String dataBase, String type, String filter) throws IOException {
        var folderPath=MessageFormat.format("{0}/{1}/{2}",Data_Base_Path ,dataBase,type);

        List<String> list=new ArrayList<>();
        var indexResult = indexRepository.tryGetUsingIndex(folderPath,filter);

        if(indexResult.isSuccess()){
            for(var id: indexResult.getResult()){

                var filePath=MessageFormat.format("{0}/{1}.json", folderPath,id);
                String Result = FileManger.readFile(filePath);
                list.add(Result);
            }
        }else if(indexResult.getResult() != null) {
            for(var id: indexResult.getResult()){

                var filePath=MessageFormat.format("{0}/{1}.json", folderPath,id);
                String Result = FileManger.readFile(filePath);
                if(Helper.isMatch(Result,filter)){
                    list.add(Result);
                }
            }
        } else {
            Stream<Path> paths = Files.walk(Paths.get(folderPath),1).filter(Files::isRegularFile);

            for(var path: paths.toList()){
                String Result = FileManger.readFile(path.toString());

                if(Helper.isMatch(Result,filter)){
                    list.add(Result);
                }
            }
        }

        return new ResponseModel.Builder<List<String>>(true).Result(list).build();
    }
}
