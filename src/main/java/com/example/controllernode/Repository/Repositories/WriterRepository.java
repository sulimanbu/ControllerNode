package com.example.controllernode.Repository.Repositories;

import com.example.controllernode.Repository.IRepositories.IIndexRepository;
import com.example.controllernode.Repository.IRepositories.IWriterRepository;
import com.example.controllernode.Services.Helper.FileManger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class WriterRepository implements IWriterRepository {

    IIndexRepository indexRepository;
    public WriterRepository(IIndexRepository indexRepository){
        this.indexRepository=indexRepository;
    }

    @Override
    public String addDocument(Path path, int id, String document) throws IOException {
        var newDocumentContent=new JSONObject(document);

        newDocumentContent.put("_id",id);
        FileManger.writeFile(path.toString(),newDocumentContent.toString());
        return newDocumentContent.toString();
    }

    @Override
    public void deleteDocument(List<String> oldVersionPath, String filePath, String folderPath, String Result) throws IOException {
        FileManger.addToOldVersion(filePath);
        oldVersionPath.add(filePath);
        Files.deleteIfExists(Path.of(filePath));
        oldVersionPath.addAll(indexRepository.deleteFromIndex(folderPath, Result));
    }

    @Override
    public void updateDocument(List<String> oldVersionPath, String filePath, String folderPath, String Result, String newDocument) throws IOException {
        var updatedDocument=update(Path.of(filePath),newDocument);
        oldVersionPath.addAll(indexRepository.deleteFromIndex(folderPath,Result));
        oldVersionPath.addAll(indexRepository.addToIndex(folderPath,updatedDocument));
        oldVersionPath.add(filePath);
    }

    private String update(Path path,String newDocument) throws IOException {
        var fileContent=new JSONObject(FileManger.readFile(path.toString()));
        var newDocumentContent=new JSONObject(newDocument);

        newDocumentContent.put("_id",fileContent.get("_id"));

        FileManger.writeFile(path.toString(),newDocumentContent.toString());
        return newDocumentContent.toString();
    }

}
