package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Repository.IRepositories.IWriterRepository;
import com.example.controllernode.Services.Helper.FileManger;
import com.example.controllernode.Services.Helper.Helper;
import com.example.controllernode.Services.Helper.IdGenerator;
import com.example.controllernode.Services.IServices.IWriterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
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
        var folderPath=MessageFormat.format("{0}/{1}/{2}", Data_Base_Path, dataBase,type);
        try {
            var indexResult = indexRepository.tryGetUsingIndex(folderPath, filter);
            List<String> oldVersionPath = new ArrayList<>();

            if (indexResult.isSuccess()) {
                for (var id : indexResult.getResult()) {
                    var filePath = MessageFormat.format("{0}/{1}.json", folderPath, id);
                    String Result = FileManger.readFile(filePath);

                    writerRepository.deleteDocument(oldVersionPath,filePath,folderPath,Result);
                }
            } else if (indexResult.getResult() != null) {
                for (var id : indexResult.getResult()) {
                    var filePath = MessageFormat.format("{0}/{1}.json", folderPath, id);
                    String Result = FileManger.readFile(filePath);

                    if (Helper.isMatch(Result, filter)) {
                        writerRepository.deleteDocument(oldVersionPath,filePath,folderPath,Result);
                    }
                }
            } else {
                Stream<Path> paths = Files.walk(Path.of(folderPath),1).filter(Files::isRegularFile);
                for (var path : paths.toList()) {
                    String result = FileManger.readFile(path.toString());

                    if (Helper.isMatch(result, filter)) {
                        writerRepository.deleteDocument(oldVersionPath,path.toString(),folderPath,result);
                    }
                }
            }


            FileManger.removeFromOldVersion(oldVersionPath);
            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }  catch (JsonProcessingException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong Json").build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong database or type").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
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
        var folderPath=MessageFormat.format("{0}/{1}/{2}", Data_Base_Path, dataBase,type);

        try{
            var indexResult = indexRepository.tryGetUsingIndex(folderPath,filter);
            List<String> oldVersionPath= new ArrayList<>();

            if(indexResult.isSuccess()){
                for(var id: indexResult.getResult()){
                    var filePath=MessageFormat.format("{0}/{1}.json", folderPath,id);
                    String Result = FileManger.readFile(filePath);

                    writerRepository.updateDocument(oldVersionPath,filePath,folderPath,Result,newDocument);
                }
            }else if(indexResult.getResult() != null) {
                for(var id: indexResult.getResult()){
                    var filePath=MessageFormat.format("{0}/{1}.json", folderPath,id);
                    String Result = FileManger.readFile(filePath);

                    if(Helper.isMatch(Result,filter)){
                        writerRepository.updateDocument(oldVersionPath,filePath,folderPath,Result,newDocument);
                    }
                }
            } else {
                Stream<Path> paths = Files.walk(Paths.get(folderPath),1).filter(Files::isRegularFile) ;

                for(var path: paths.toList()){
                    String Result =FileManger.readFile(path.toString());

                    if(Helper.isMatch(Result,filter)){
                        writerRepository.updateDocument(oldVersionPath,path.toString(),folderPath,Result,newDocument);
                    }
                }
            }

            FileManger.removeFromOldVersion(oldVersionPath);
            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }  catch (JsonProcessingException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong Json").build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong database or type").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }
}
