package com.example.controllernode.controllers;


import com.example.controllernode.Helper.CurrentUser;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Model.Role;
import com.example.controllernode.Model.User;
import com.example.controllernode.Services.IServices.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/User")
public class UserController {
    final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping( "/addUser")
    public ResponseModel addUser(@RequestBody User user) {

        if(CurrentUser.getUser().getRole()!= Role.administrator){
            return new ResponseModel.Builder<String>(false).message("Your are not an administrator").build();
        }

        return userService.addUser(user);
    }

    @PostMapping( "/updatePassword")
    public ResponseModel updatePassword(@RequestBody String password){
        return userService.updatePassword(password);
    }
}
