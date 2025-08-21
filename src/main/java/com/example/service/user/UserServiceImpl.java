package com.example.service.user;


import com.di.annotation.component.Component;
import com.example.persistence.repository.UserRepository;
import com.example.service.email.EmailService;

@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public String createUser(String name, String email) {
        String user = "1/" + name + "/" + email;
        userRepository.save(user);
        emailService.sendWelcomeEmail(user);
        return user;
    }

    @Override
    public String getUser(Long id) {
        return userRepository.findById(id);
    }
}
