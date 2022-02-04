package com.example.controllernode.controllers;


import com.example.controllernode.Model.CurrentUser;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Model.Role;
import com.example.controllernode.Model.User;
import com.example.controllernode.Services.IServices.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/User")
public class UserController {
    final IUserService userService;
    private static final Logger logger = LogManager.getLogger(UserController.class);
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping( "/addUser")
    public ResponseModel<Boolean> addUser(@RequestBody User user, HttpServletRequest request) {
        try{
            var currentUser=(CurrentUser)request.getAttribute("CurrentUser");

            if(currentUser.getUser().getRole()!= Role.administrator){
                return new ResponseModel.Builder<Boolean>(false).message("Your are not an administrator").build();
            }

            return userService.addUser(user);
        }catch (Exception ex){
            logger.fatal("UserController-addUser Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }

    @PostMapping( "/updatePassword")
    public ResponseModel<Boolean> updatePassword(@RequestBody String password,HttpServletRequest request){
        try{
            var currentUser=(CurrentUser)request.getAttribute("CurrentUser");

            return userService.updatePassword(password,currentUser.getUser().getUsername());
        }catch (Exception ex){
            logger.fatal("UserController-updatePassword Exception: ",ex);
            return new ResponseModel.Builder<Boolean>(false)
                    .message("Error Happened").build();
        }
    }
}
