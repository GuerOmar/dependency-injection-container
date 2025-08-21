package com.example.valid.repository;

public interface UserRepository {
    void save(String user);
    String findById(Long id);
}
