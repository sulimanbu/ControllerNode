package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.IUserService;
import com.example.controllernode.Model.User;
import com.fasterxml.jackson.databind.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.text.MessageFormat;

@Service
public class UserService implements IUserService {

    @Value("${spring.application.Users_Base_Path}")
    String Users_Base_Path;

    @Override
    public synchronized ResponseModel<Boolean> addUser(User user) throws IOException {
        return Add(user.getUsername(), user.toString());

    }

    @Override
    public synchronized ResponseModel<Boolean> addFirstUser() throws IOException {
        return Add("Admin", "{\"username\":\"Admin\",\"password\":\"123\",\"role\":\"administrator\",\"isDefault\":true}");
    }

    private ResponseModel<Boolean> Add(String username, String user ) throws IOException {
        createUsersDir();

        var filePath= Path.of(MessageFormat.format("{0}/{1}.json",Users_Base_Path, username));
        if(!Files.exists(filePath)){
            Files.writeString(filePath, user);

            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }else {
            return new ResponseModel.Builder<Boolean>(false).message("The UserName already exist").build();
        }
    }

    @Override
    public synchronized ResponseModel<User> validateUser(String username, String password) throws IOException {
        try {
            var filePath=Path.of(MessageFormat.format("{0}/{1}.json",Users_Base_Path, username));
            String Result = Files.readString(filePath);

            User user = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(Result, User.class);
            if(user.getPassword().equals(password)){
                user.setPassword("");
                return new ResponseModel.Builder<User>(true).Result(user).build();
            }

            return new ResponseModel.Builder<User>(false).message("Wrong password").build();
        } catch (NoSuchFileException ex){
            return new ResponseModel.Builder<User>(false).message("Wrong username").build();
        }
    }

    @Override
    public synchronized ResponseModel<Boolean> updatePassword(String password,String username) throws IOException {
        var filePath=Path.of(MessageFormat.format("{0}/{1}.json", Users_Base_Path,username));
        String Result =Files.readString(filePath);

        var user=new JSONObject(Result);
        user.put("password", password);
        if(user.has("isDefault")){
            user.put("isDefault",false);
        }

        Files.writeString(filePath,user.toString());

        return new ResponseModel.Builder<Boolean>(true).Result(true).build();
    }

    private void createUsersDir() throws IOException {
        var path=Path.of(Users_Base_Path);
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
    }
}
