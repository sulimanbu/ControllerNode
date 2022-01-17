package com.example.controllernode.Repository.Repositories;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class IndexRepository implements IIndexRepository {

    @Override
    public void setIndexValues(String folderPath, String property) throws IOException {
        var rootPath= MessageFormat.format("{0}", folderPath);
        Stream<Path> paths = Files.walk(Paths.get(rootPath),1).filter(Files::isRegularFile);

        Map<String, ArrayList<Integer>> index=new HashMap<>();
        for(var path: paths.toList()) {
            var fileContent = new JSONObject(Files.readString(path));

            if (fileContent.has(property)) {
                var list = index.getOrDefault(fileContent.get(property).toString(), new ArrayList<>());
                list.add((Integer) fileContent.get("_id"));
                index.put(fileContent.get(property).toString(), list);
            }
        }
        String json = new ObjectMapper().writeValueAsString(index);
        Files.writeString(Path.of(rootPath+"/index/"+ property+ ".json"),json);
    }

    @Override
    public void addToIndex(String folderPath, String Document) throws IOException {
        if(Files.exists(Path.of(folderPath + "/index"))){
            var documentContent = new JSONObject(Document);

            var keys=documentContent.keys();
            while(keys.hasNext()){
                var property=keys.next();
                var filePath=Path.of(folderPath + "/index/" + property + ".json" );
                if(Files.exists(filePath)){
                    var fileContent=new JSONObject(Files.readString(filePath));
                    List array=new ArrayList<Object>();
                    if(fileContent.has(documentContent.get(property).toString())){
                        array=((JSONArray)fileContent.get(documentContent.get(property).toString())).toList();
                    }

                    array.add((Integer) documentContent.get("_id"));
                    fileContent.put(documentContent.get(property).toString(),array);
                    Files.writeString(filePath,fileContent.toString());
                }
            }
        }
    }

    @Override
    public void deleteFromIndex(String folderPath, String Document) throws IOException {
        if(Files.exists(Path.of(folderPath + "/index"))){
            var documentContent = new JSONObject(Document);
            var keys=documentContent.keys();
            while(keys.hasNext()){
                var property=keys.next();
                var filePath=Path.of(folderPath + "/index/" + property + ".json" );
                if(Files.exists(filePath)){
                    var fileContent=new JSONObject(Files.readString(filePath));
                    if(fileContent.has(documentContent.get(property).toString())){
                        var array=(JSONArray)fileContent.get(documentContent.get(property).toString());
                        var array1=array.toList();
                        array1.remove((Integer) documentContent.get("_id"));
                        if(array1.isEmpty()){
                            fileContent.remove(documentContent.get(property).toString());
                        }else {
                            fileContent.put(documentContent.get(property).toString(),array1);
                        }
                        Files.writeString(filePath,fileContent.toString());
                    }
                }
            }
        }
    }

    @Override
    public ResponseModel<List<Object>> tryGetUsingIndex(String folderPath, String filter) throws IOException {
        if(Files.exists(Path.of(folderPath + "/index"))){
            var filterContent = new JSONObject(filter);

            boolean isMatch=false;
            boolean isIndexJust = true;

            List<Object> list=new ArrayList<Object>();
            var keys=filterContent.keys();
            while(keys.hasNext()){
                List<Object> array=new ArrayList<Object>();
                var property=keys.next();
                var filePath=Path.of(folderPath + "/index/" + property + ".json" );
                if(Files.exists(filePath)){
                    var fileContent=new JSONObject(Files.readString(filePath));

                    var propertyValue=filterContent.get(property).toString();
                    if(fileContent.has(propertyValue)){
                        array.addAll(((JSONArray)fileContent.get(propertyValue)).toList());

                        if(!isMatch){
                            list.addAll(array);
                        }else {
                            list = list.stream().filter(l -> array.contains(l)).toList();
                        }

                        isMatch=true;
                    }else {
                        return new ResponseModel.Builder<List<Object>>(true).Result(new ArrayList<Object>()).build();
                    }
                }else {
                    isIndexJust = false;
                }
            }

            if(isMatch){
                if(isIndexJust){
                    return new ResponseModel.Builder<List<Object>>(true).Result(list).build();
                }else {
                    return new ResponseModel.Builder<List<Object>>(false).Result(list).build();
                }
            }

        }
        return new ResponseModel.Builder<List<Object>>(false).build();
    }
}
