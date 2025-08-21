package com.example.service.email;

import com.di.annotation.component.Component;

@Component
public class EmailServiceImpl implements EmailService {

    public EmailServiceImpl() {
    }

    @Override
    public void sendWelcomeEmail(String user) {
        System.out.println("Sending welcome email to: " + user.split("/")[2]);
    }
}
