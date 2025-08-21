package com.example;

import com.di.*;
import com.example.service.user.UserService;

public class ApplicationStarter {
    public static void main(String[] args) {
        Container container = new Container();
        container.scanPackage("com.example");
        System.out.println("======================================================== APPLICATION STARTED ========================================================");
        UserService userService = container.getInstance(UserService.class);
        System.out.println("Getting user with id 1L..");
        System.out.println(userService.getUser(1L));
        System.out.println("Creating a new user..");
        System.out.println(userService.createUser("test", "testi@toto.com"));
    }
}
