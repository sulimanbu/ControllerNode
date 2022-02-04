package com.example.controllernode;

import com.example.controllernode.Services.Helper.IdGenerator;
import com.example.controllernode.Services.IServices.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class ControllerNodeApplication {

    final IUserService userService;
    @Value("${spring.application.Data_Base_Path}")
    String Data_Base_Path;
    public ControllerNodeApplication(IUserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ControllerNodeApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws IOException {
        Files.createDirectories(Path.of(Data_Base_Path));
        userService.addFirstUser();
        IdGenerator.getOldIds();
    }
}
