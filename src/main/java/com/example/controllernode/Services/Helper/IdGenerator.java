package com.example.controllernode.Services.Helper;

import org.apache.logging.log4j.*;

import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class IdGenerator {
    private static final Logger logger = LogManager.getLogger(IdGenerator.class);
    static String Data_Base_Path="NoSqlDB/DB";

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
        try(Stream<Path> paths = Files.walk(Paths.get(Data_Base_Path),1).filter(Files::isDirectory)) {

            for(var databasePath: paths.toList()){
                if(!Files.isSameFile(databasePath,Path.of(Data_Base_Path))){
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
            logger.fatal("IdGenerator-getOldIds Exception: ",ex);
        }
    }
}
