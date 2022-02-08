package com.example.controllernode.Repository.Repositories;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Services.Helper.FileManger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
public class IndexRepository implements IIndexRepository {

    @Override
    public void setIndexValues(String folderPath, String property) throws IOException {
        Stream<Path> paths = Files.walk(Paths.get(folderPath),1).filter(Files::isRegularFile);

        Map<String, ArrayList<Integer>> index=new HashMap<>();
        if(property.contains(",")){
            setMultiIndexValues(paths,property.split(","),index);
        }else {
            setSingleIndexValues(paths,property,index);
        }
        String json = new ObjectMapper().writeValueAsString(index);

        FileManger.writeFile(folderPath+"/index/"+ property+ ".json",json);
        FileManger.removeFromOldVersion(new ArrayList<>(Collections.singleton(folderPath + "/index/" + property + ".json")));
    }
    private void setMultiIndexValues(Stream<Path> paths, String[] indexPropertiesArray,Map<String, ArrayList<Integer>> index) throws IOException {
        for(var path: paths.toList()) {
            var fileContent = new JSONObject(Files.readString(path));
            String indexValue = getIndexValue(fileContent, indexPropertiesArray);

            if (!indexValue.equals("")) {
                var list = index.getOrDefault(indexValue, new ArrayList<>());
                list.add((Integer) fileContent.get("_id"));
                index.put(indexValue, list);
            }
        }
    }
    private void setSingleIndexValues(Stream<Path> paths, String property,Map<String, ArrayList<Integer>> index) throws IOException {
        for(var path: paths.toList()) {
            var fileContent = new JSONObject(Files.readString(path));

            if (fileContent.has(property)) {
                var list = index.getOrDefault(fileContent.get(property).toString(), new ArrayList<>());
                list.add((Integer) fileContent.get("_id"));
                index.put(fileContent.get(property).toString(), list);
            }
        }
    }

    @Override
    public List<String> addToIndex(String folderPath, String Document) throws IOException {
        List<String> oldVersionPaths=new ArrayList<>();

        if(Files.exists(Path.of(folderPath + "/index"))){
            var documentContent = new JSONObject(Document);

            Stream<Path> paths = Files.walk(Path.of(folderPath + "/index"),1).filter(Files::isRegularFile);
            for(var path: paths.toList()) {
                var propertyName=path.getFileName().toString().replace(".json","");

                if(propertyName.contains(",")){
                    addToMultiIndex(path,documentContent,propertyName.split(","),oldVersionPaths);
                }else {
                    addToSingleIndex(path,documentContent,propertyName,oldVersionPaths);
                }
            }
        }
        return oldVersionPaths;
    }
    private void addToMultiIndex(Path path,JSONObject documentContent,String[] propertiesName,List<String> oldVersionPaths) throws IOException {
        String indexValue = getIndexValue(documentContent, propertiesName);

        if(!indexValue.equals("")){
            addIndex(path,documentContent,indexValue,oldVersionPaths);
        }
    }
    private void addToSingleIndex(Path path,JSONObject documentContent,String propertyName,List<String> oldVersionPaths) throws IOException {
        if(documentContent.has(propertyName)){
            var propertyValue= documentContent.get(propertyName).toString();
            addIndex(path,documentContent,propertyValue,oldVersionPaths);
        }
    }
    private void addIndex(Path path,JSONObject documentContent,String propertyValue,List<String> oldVersionPaths) throws IOException {
        var fileContent = new JSONObject(Files.readString(path));
        List<Object> array= new ArrayList<>();

        if(fileContent.has(propertyValue)){
            array=((JSONArray)fileContent.get(propertyValue)).toList();
        }

        array.add( documentContent.get("_id"));
        fileContent.put(propertyValue,array);

        oldVersionPaths.add(path.toString());
        FileManger.writeFile(path.toString(),fileContent.toString());
    }

    @Override
    public List<String> deleteFromIndex(String folderPath, String Document) throws IOException {
        List<String> oldVersionPaths=new ArrayList<>();

        if(Files.exists(Path.of(folderPath + "/index"))){
            var documentContent = new JSONObject(Document);

            Stream<Path> paths = Files.walk(Path.of(folderPath + "/index"),1).filter(Files::isRegularFile);
            for(var path: paths.toList()) {
                var propertyName=path.getFileName().toString().replace(".json","");

                if(propertyName.contains(",")){
                    deleteFromMultiIndex(path,documentContent,propertyName.split(","),oldVersionPaths);
                }else {
                    deleteFromSingleIndex(path,documentContent,propertyName,oldVersionPaths);
                }
            }
        }
        return oldVersionPaths;
    }
    private void deleteFromMultiIndex(Path path,JSONObject documentContent,String[] propertiesName,List<String> oldVersionPaths) throws IOException {
        String indexValue= getIndexValue(documentContent, propertiesName);

        if(!indexValue.equals("")){
            deleteIndex(path,documentContent,indexValue,oldVersionPaths);
        }
    }
    private void deleteFromSingleIndex(Path path,JSONObject documentContent,String propertyName,List<String> oldVersionPaths) throws IOException {
        if(documentContent.has(propertyName)){
            var propertyValue= documentContent.get(propertyName).toString();
            deleteIndex(path,documentContent,propertyValue,oldVersionPaths);
        }
    }
    private void deleteIndex(Path path,JSONObject documentContent,String propertyValue,List<String> oldVersionPaths) throws IOException {
        var fileContent = new JSONObject(Files.readString(path));
        var array=(JSONArray)fileContent.get(propertyValue);
        var array1=array.toList();
        array1.remove(documentContent.get("_id"));
        if(array1.isEmpty()){
            fileContent.remove(propertyValue);
        }else {
            fileContent.put(propertyValue,array1);
        }

        oldVersionPaths.add(path.toString());
        FileManger.writeFile(path.toString(),fileContent.toString());
    }

    @Override
    public ResponseModel<List<Object>> tryGetUsingIndex(String folderPath, String filter) throws IOException {
        if(Files.exists(Path.of(folderPath + "/index"))){
            var filterContent = new JSONObject(filter);
            boolean isMatchIndex=false;

            Set<String> propertyUsed = new HashSet<>();
            List<Object> IdsList = new ArrayList<>();
            Stream<Path> paths = Files.walk(Path.of(folderPath + "/index"),1).filter(Files::isRegularFile);
            for (var path:paths.toList()) {
                var propertyName=path.getFileName().toString().replace(".json","");

                if(propertyName.contains(",")){
                    String[] propertiesName=propertyName.split(",");
                    String indexValue= getIndexValue(filterContent, propertiesName);

                    if(!indexValue.equals("")){
                        propertyUsed.addAll(Arrays.stream(propertiesName).toList());
                        addIdsToList(path,indexValue,isMatchIndex,IdsList);
                        isMatchIndex = true;
                    }
                }else {
                    if(filterContent.has(propertyName)){
                        propertyUsed.add(propertyName);
                        var propertyValue= filterContent.get(propertyName).toString();

                        addIdsToList(path,propertyValue,isMatchIndex,IdsList);
                        isMatchIndex=true;
                    }
                }

                if(isMatchIndex && IdsList.isEmpty()){
                    return new ResponseModel.Builder<List<Object>>(true).Result(new ArrayList<>()).build();
                }
            }

            if(isMatchIndex){
                return new ResponseModel.Builder<List<Object>>(isIndexJust(propertyUsed,filterContent)).Result(IdsList).build();
            }
        }
        return new ResponseModel.Builder<List<Object>>(false).build();
    }
    private void addIdsToList(Path path,String propertyValue,Boolean isMatchIndex,List<Object> IdsList) throws IOException {
        var fileContent=new JSONObject(FileManger.readFile(path.toString()));

        if(fileContent.has(propertyValue)){
            List<Object> array = new ArrayList<>(((JSONArray) fileContent.get(propertyValue)).toList());

            if(!isMatchIndex){
                IdsList.addAll(array);
            }else {
                IdsList.removeIf(item -> !array.contains(item));
            }
        }else {
            IdsList.clear();
        }
    }
    private String getIndexValue(JSONObject filterContent, String[] propertiesName) {
        StringBuilder indexValue= new StringBuilder();
        for (var item:propertiesName) {
            if (filterContent.has(item)) {
                if(!indexValue.toString().equals(""))
                    indexValue.append("(*&&*)");
                indexValue.append(filterContent.get(item));
            }else {
                indexValue = new StringBuilder();
                break;
            }
        }

        return indexValue.toString();
    }
    private boolean isIndexJust(Set<String> propertyUsed,JSONObject filterContent){
        var keys=filterContent.keys();
        while(keys.hasNext()){
            var property=keys.next();
            if(!propertyUsed.contains(property)){
                return false;
            }
        }
        return true;
    }
}
