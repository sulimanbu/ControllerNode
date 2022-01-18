package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Services.Helper.FileManger;
import com.example.controllernode.Services.Helper.Helper;
import com.example.controllernode.Services.Helper.IdGenerator;
import com.example.controllernode.Services.IServices.IWriterService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
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
            List<String> oldVersionPath= new ArrayList<>();
            var filePath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
            if(Files.exists(Path.of(filePath))){
                int id = IdGenerator.getId(filePath);
                var result= add(Path.of(MessageFormat.format("{0}/{1}.json", filePath,id)),id,document);
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
    private String add(Path path,int id,String document) throws IOException {
        var newDocumentContent=new JSONObject(document);

        newDocumentContent.put("_id",id);
        FileManger.writeFile(path.toString(),newDocumentContent.toString());
        return newDocumentContent.toString();
    }

    @Override
    public synchronized ResponseModel<Boolean> deleteDocumentById(String dataBase, String type,int id) {
        try{
            var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
            var filePath=Path.of(MessageFormat.format("NoSqlDB/DB/{0}/{1}/{2}.json", dataBase,type,id));

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
        var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
        try(Stream<Path> paths = Files.walk(Path.of(folderPath)).filter(Files::isRegularFile)) {
            List<String> oldVersionPath= new ArrayList<>();

            for(var path: paths.toList()){
                String result = FileManger.readFile(path.toString());

                if(Helper.isMatch(result,filter)){
                    FileManger.addToOldVersion(path.toString());
                    oldVersionPath.add(path.toString());
                    Files.deleteIfExists(path);
                    oldVersionPath.addAll(indexRepository.deleteFromIndex(folderPath,result));
                }
            }

            FileManger.removeFromOldVersion(oldVersionPath);
            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    @Override
    public synchronized ResponseModel<Boolean> updateDocumentById(String dataBase, String type,int id,String newDocument) {
        try{
            List<String> oldVersionPath= new ArrayList<>();

            var folderPath=MessageFormat.format("NoSqlDB/DB/{0}/{1}", dataBase,type);
            var filepath=Path.of(MessageFormat.format("{0}/{1}.json", folderPath,id));
            if(Files.exists(filepath)){
                var oldDocument=FileManger.readFile(filepath.toString());
                var updatedDocument=update(filepath,newDocument);
                oldVersionPath.addAll(indexRepository.deleteFromIndex(folderPath,oldDocument));
                oldVersionPath.addAll(indexRepository.addToIndex(folderPath,updatedDocument));
                oldVersionPath.add(filepath.toString());
                return new ResponseModel.Builder<Boolean>(true).Result(true).build();
            }
            FileManger.removeFromOldVersion(oldVersionPath);

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

            List<String> oldVersionPath= new ArrayList<>();

            for(var path: paths.toList()){
                String Result = FileManger.readFile(path.toString());

                if(Helper.isMatch(Result,filter)){
                    var updatedDocument=update(path,newDocument);
                    oldVersionPath.addAll(indexRepository.deleteFromIndex(folderPath,Result));
                    oldVersionPath.addAll(indexRepository.addToIndex(folderPath,updatedDocument));
                    oldVersionPath.add(path.toString());
                }
            }

            FileManger.removeFromOldVersion(oldVersionPath);
            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }catch (NoSuchFileException ex){
            return new ResponseModel.Builder<Boolean>(false).message("Wrong database or type").build();
        }
        catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    private String update(Path path,String newDocument) throws IOException {
        var fileContent=new JSONObject(FileManger.readFile(path.toString()));
        var newDocumentContent=new JSONObject(newDocument);

        newDocumentContent.put("_id",fileContent.get("_id"));

        FileManger.writeFile(path.toString(),newDocumentContent.toString());
        return newDocumentContent.toString();
    }
}
