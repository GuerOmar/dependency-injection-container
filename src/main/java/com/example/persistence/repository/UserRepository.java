package com.example.persistence.repository;

public interface UserRepository {
    void save(String user);
    String findById(Long id);
}
