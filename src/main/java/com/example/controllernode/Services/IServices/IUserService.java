package com.example.controllernode.Services.IServices;

import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Model.User;


public interface IUserService {
    ResponseModel<Boolean> addUser(User user);
    ResponseModel<Boolean> addFirstUser();
    ResponseModel<User> validateUser(String username, String password);
    ResponseModel<Boolean> updatePassword(String password,String username);
}
