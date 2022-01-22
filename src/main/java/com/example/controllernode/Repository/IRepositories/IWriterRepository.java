package com.example.controllernode.Repository.IRepositories;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IWriterRepository {
    String addDocument(Path path, int id, String document) throws IOException;
    void deleteDocument(List<String> oldVersionPath, String filePath, String folderPath, String Result) throws IOException;
    void updateDocument(List<String> oldVersionPath,String filePath,String folderPath,String Result, String newDocument) throws IOException;
}
