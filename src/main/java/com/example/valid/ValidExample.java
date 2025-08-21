package com.example.valid;

import com.di.*;
import com.example.valid.service.user.UserService;

public class ValidExample {
    public static void main(String[] args) {
        Container container = new Container();
        container.scanPackage(ValidExample.class.getPackageName());
        System.out.println("======================================================== APPLICATION STARTED ========================================================");
        UserService userService = container.getInstance(UserService.class);
        System.out.println("Getting user with id 1L..");
        System.out.println(userService.getUser(1L));
        System.out.println("Creating a new user..");
        System.out.println(userService.createUser("test", "testi@toto.com"));
    }
}
