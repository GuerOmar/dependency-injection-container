package com.example.service.user;

public interface UserService {
    String createUser(String name, String email);
    String getUser(Long id);
}
