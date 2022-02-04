package com.example.controllernode.controllers;

import com.example.controllernode.ControllerNodeApplication;
import com.example.controllernode.Helper.JWT;
import com.example.controllernode.Model.LogInModel;
import com.example.controllernode.Model.ResponseModel;
import com.example.controllernode.Services.IServices.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/LogIn")
public class LogInController {
    final IUserService userService;
    private static final Logger logger = LogManager.getLogger(LogInController.class);

    public LogInController(IUserService userService) {
        this.userService = userService;
    }


    @PostMapping( "/LogIn")
    public ResponseModel<String> logIn(@RequestBody LogInModel logInModel) {
        try{
            var userResponse=userService.validateUser(logInModel.username, logInModel.password);

            if(userResponse.isSuccess()){
                var token=JWT.createJWT(userResponse.getResult().getUsername(),userResponse.getResult().getRole().toString(),"");
                return new ResponseModel.Builder<String>(true).Result(token).build();
            }

            return new ResponseModel.Builder<String>(userResponse.isSuccess()).message(userResponse.getMessage()).build();
        }catch (Exception ex){
            logger.fatal("LogInController_logIn Exception:",ex);
            return new ResponseModel.Builder<String>(false)
                    .message("Error Happened").build();
        }

    }
}
