package com.example.controllernode.Services.Services;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.IUserService;
import com.example.controllernode.Model.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.text.MessageFormat;
import com.example.controllernode.Helper.CurrentUser;

@Service
public class UserService implements IUserService {
    @Override
    public synchronized ResponseModel<Boolean> addUser(User user) {
        return Add(user.getUsername(), user.toString());

    }

    @Override
    public synchronized ResponseModel<Boolean> addFirstUser() {
        return Add("Admin", "{\"username\":\"Admin\",\"password\":\"123\",\"role\":\"administrator\",\"isDefault\":true}");

    }

    private ResponseModel<Boolean> Add(String username, String user ) {
        try{
            createUsersDir();

            var filePath= Path.of(MessageFormat.format("NoSqlDB/Users/{0}.json", username));
            if(!Files.exists(filePath)){
                Files.writeString(filePath, user);

                return new ResponseModel.Builder<Boolean>(true).Result(true).build();
            }else {
                return new ResponseModel.Builder<Boolean>(false).message("The UserName already exist").build();
            }
        } catch (IOException ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    @Override
    public synchronized ResponseModel<User> validateUser(String username, String password){
        try {
            var filePath=Path.of(MessageFormat.format("NoSqlDB/Users/{0}.json", username));
            String Result = Files.readString(filePath);

            User user = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(Result, User.class);
            if(user.getPassword().equals(password)){
                user.setPassword("");
                return new ResponseModel.Builder<User>(true).Result(user).build();
            }

            return new ResponseModel.Builder<User>(false).message("Wrong password").build();
        } catch (NoSuchFileException ex){
            return new ResponseModel.Builder<User>(false).message("Wrong username").build();
        }catch (Exception ex){
            return new ResponseModel.Builder<User>(false).message("error happened").build();
        }
    }

    @Override
    public synchronized ResponseModel<Boolean> updatePassword(String password) {
        try {
            var filePath=Path.of(MessageFormat.format("NoSqlDB/Users/{0}.json", CurrentUser.getUser().getUsername()));
            String Result = Files.readString(filePath);

            var user=new JSONObject(Result);
            user.put("password", password);
            if(user.has("isDefault")){
                user.put("isDefault",false);
            }

            Files.writeString(filePath,user.toString());

            return new ResponseModel.Builder<Boolean>(true).Result(true).build();
        }catch (Exception ex){
            return new ResponseModel.Builder<Boolean>(false).message("error happened").build();
        }
    }

    private void createUsersDir() throws IOException {
        var path=Path.of("NoSqlDB/Users");
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
    }
}
