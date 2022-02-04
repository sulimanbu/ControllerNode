package com.example.controllernode;

import com.example.controllernode.Services.Helper.IdGenerator;
import com.example.controllernode.Services.IServices.IUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;

@SpringBootApplication
public class ControllerNodeApplication {

    final IUserService userService;
    public ControllerNodeApplication(IUserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ControllerNodeApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws IOException {
        userService.addFirstUser();
        IdGenerator.getOldIds();

    }
}
