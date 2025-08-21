package com.example.cirucalrcheckfail.repository;

public interface UserRepository {
    void save(String user);
    String findById(Long id);
}
