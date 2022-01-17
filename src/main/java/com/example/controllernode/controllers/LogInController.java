package com.example.controllernode.controllers;

import com.example.controllernode.Helper.JWT;
import com.example.controllernode.Model.LogInModel;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Model.User;
import com.example.controllernode.Services.IServices.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/LogIn")
public class LogInController {
    final IUserService userService;

    public LogInController(IUserService userService) {
        this.userService = userService;
    }


    @PostMapping( "/LogIn")
    public ResponseModel logIn(@RequestBody LogInModel logInModel) {
        var userResponse=userService.validateUser(logInModel.username, logInModel.password);

        if(userResponse.isSuccess()){
            var token=JWT.createJWT(userResponse.getResult().getUsername(),userResponse.getResult().getRole().toString(),"");
            return new ResponseModel.Builder<String>(true).Result(token).build();
        }

        return userResponse;
    }
}
