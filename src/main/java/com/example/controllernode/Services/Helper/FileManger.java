package com.example.controllernode.Services.Helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FileManger {
    private FileManger(){
        throw new AssertionError();
    }

    private static final ConcurrentHashMap<String, String> FilesCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> FilesOldVersion= new ConcurrentHashMap<>();

    public synchronized static String readFile(String path) throws IOException {
        var absolutePath=Path.of(path).toAbsolutePath().toString();

        var valueFromOldVersion = FilesOldVersion.getOrDefault(absolutePath,null);
        if(valueFromOldVersion != null){
            return valueFromOldVersion;
        }

        var value= FilesCache.getOrDefault(absolutePath,null);
        if(value != null){
            return value;
        }
        var fileContent=Files.readString(Path.of(absolutePath));
        FilesCache.put(absolutePath,fileContent);
        return  fileContent;
    }
    public synchronized static void writeFile(String path,String content) throws IOException {
        var absolutePath=Path.of(path).toAbsolutePath().toString();

        addToOldVersion(path);
        deleteFromCache(absolutePath);

        Files.writeString(Path.of(absolutePath),content);
    }

    public synchronized static void deleteFromCache(String absolutePath){
        if(FilesCache.containsKey(absolutePath)){
            FilesCache.remove(absolutePath);
        }
    }

    public synchronized static void addToOldVersion(String path) throws IOException {
        FilesOldVersion.put(Path.of(path).toAbsolutePath().toString(),readFile(path));
        deleteFromCache(path);
    }

    public synchronized static void removeFromOldVersion(List<String> paths){
        for (var path:paths){
            FilesOldVersion.remove(Path.of(path).toAbsolutePath().toString());
        }
    }
}
