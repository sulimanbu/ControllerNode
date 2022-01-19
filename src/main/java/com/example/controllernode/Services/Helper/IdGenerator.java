package com.example.controllernode.Services.Helper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class IdGenerator {
    private IdGenerator(){
        throw new AssertionError();
    }

    private static final ConcurrentHashMap<String, Integer> ids= new ConcurrentHashMap<>();

    public static synchronized int getId(String path){
        var absolutePath=Path.of(path).toAbsolutePath().toString();
        if(ids.containsKey(absolutePath)){
            var id=ids.get(absolutePath);
            ids.put(absolutePath,id+1);
            return id;
        }

        ids.put(absolutePath,2);
        return 1;
    }

    public static void addNewType(String path){
        var absolutePath=Path.of(path).toAbsolutePath().toString();
        if(!ids.containsKey(absolutePath)){
            ids.put(absolutePath,1);
        }
    }

    public static void getOldIds(){
        try(Stream<Path> paths = Files.walk(Paths.get("NoSqlDB/DB"),1).filter(Files::isDirectory)) {

            for(var databasePath: paths.toList()){
                if(!Files.isSameFile(databasePath,Path.of("NoSqlDB/DB"))){
                    for (var typePath:Files.walk(databasePath,1).filter(Files::isDirectory).toList()){
                        if(!Files.isSameFile(typePath,databasePath)){
                            final int[] max = {0};

                            Files.walk(typePath,1).filter(Files::isRegularFile)
                                    .forEach(path1 -> {
                                        var number=Integer.parseInt(path1.getFileName().toString().replace(".json",""));
                                        max[0] = Math.max(number, max[0]);
                                    });
                            ids.put(typePath.toAbsolutePath().toString(), max[0]+1);
                        }
                    }
                }
            }

        }
        catch (Exception ex){
        }
    }
}
