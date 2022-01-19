package com.example.controllernode;

import com.example.controllernode.Model.Role;
import com.example.controllernode.Services.Helper.IdGenerator;
import com.example.controllernode.Services.IServices.IUserService;
import com.example.controllernode.Services.Services.SchemaService;
import com.example.controllernode.Services.Services.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ControllerNodeApplication {

    public static void main(String[] args) {
        final IUserService userService=new UserService();

        userService.addFirstUser();
        IdGenerator.getOldIds();
        SpringApplication.run(ControllerNodeApplication.class, args);
    }

}
