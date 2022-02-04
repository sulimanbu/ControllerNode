package com.example.controllernode.Services.IServices;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Model.User;

import java.io.IOException;


public interface IUserService {
    ResponseModel<Boolean> addUser(User user) throws IOException;
    ResponseModel<Boolean> addFirstUser() throws IOException;
    ResponseModel<User> validateUser(String username, String password) throws IOException;
    ResponseModel<Boolean> updatePassword(String password,String username) throws IOException;
}
