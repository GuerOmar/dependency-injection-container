package com.example.valid.service.email;

import com.di.annotation.component.Component;
import com.example.valid.service.user.UserService;

@Component
public class EmailServiceImpl implements EmailService {

    public EmailServiceImpl() {
    }

    @Override
    public void sendWelcomeEmail(String user) {
        System.out.println("Sending welcome email to: " + user.split("/")[2]);
    }
}
