package com.example.cirucalrcheckfail.service.email;

import com.di.annotation.component.Component;
import com.example.cirucalrcheckfail.service.user.UserService;

@Component
public class EmailServiceImpl implements EmailService {

    private final UserService userService;

    public EmailServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void sendWelcomeEmail(Long userId) {
        String user = userService.getUser(userId);
        System.out.println("Sending welcome email to: " + user.split("/")[2]);
    }
}
