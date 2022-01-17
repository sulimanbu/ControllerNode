package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Services.Helper.Helper;
import com.example.controllernode.Services.IServices.IReaderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;


import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReaderService implements IReaderService {

    IIndexRepository indexRepository;
    public ReaderService(IIndexRepository indexRepository){
        this.indexRepository=indexRepository;
    }

    @Override
    public ResponseModel<String> GetById(String dataBase,String type,int id) {
        try {
            String Result = Files.readString(Path.of(MessageFormat.format("NoSqlDB/DB/{0}/{1}/{2}.json", dataBase,type,id)));

            return new ResponseModel.Builder<String>(true).Result(Result).build();
        } catch (NoSuchFileException ex){
            return new ResponseModel.Builder<String>(false).message("Wrong Id").build();
        }catch (Exception ex){
            return new ResponseModel.Builder<String>(false).message("error happened").build();
        }
    }

    @Override
    public ResponseModel<List<String>> Get(String dataBase, String type, String filter) {
        var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);

        try{
        var indexResult = indexRepository.tryGetUsingIndex(folderPath,filter);

        List<String> list=new ArrayList<>();
        if(indexResult.isSuccess()){
            for(var id: indexResult.getResult()){

                var fileResult=Path.of(MessageFormat.format("{0}/{1}.json", folderPath,id));
                String Result = Files.readString(fileResult);
                list.add(Result);
            }
        }else if(indexResult.getResult() != null) {
            for(var id: indexResult.getResult()){

                var fileResult=Path.of(MessageFormat.format("{0}/{1}.json", folderPath,id));
                String Result = Files.readString(fileResult);
                if(Helper.isMatch(Result,filter)){
                    list.add(Result);
                }
            }
        } else {
            Stream<Path> paths = Files.walk(Paths.get(folderPath),1).filter(Files::isRegularFile);

            for(var path: paths.toList()){
                String Result = Files.readString(path);

                if(Helper.isMatch(Result,filter)){
                    list.add(Result);
                }
            }
        }

        return new ResponseModel.Builder<List<String>>(true).Result(list).build();
        }  catch (JsonProcessingException ex){
            return new ResponseModel.Builder<List<String>>(false).message("Wrong Json").build();
        }catch (Exception ex){
            return new ResponseModel.Builder<List<String>>(false).message("error happened").build();
        }
    }

    @Override
    public ResponseModel<List<String>> GetAll(String dataBase, String type) {
        try(Stream<Path> paths = Files.walk(Paths.get(MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type)),1).filter(Files::isRegularFile)) {

            List<String> list=new ArrayList<>();
            for(var path: paths.toList()){
                String Result = Files.readString(path);
                list.add(Result);
            }

            return new ResponseModel.Builder<List<String>>(true).Result(list).build();
        }catch (Exception ex){
            return new ResponseModel.Builder<List<String>>(false).message("error happened").build();
        }
    }

}
