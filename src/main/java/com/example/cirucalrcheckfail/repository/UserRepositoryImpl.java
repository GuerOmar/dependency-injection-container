package com.example.cirucalrcheckfail.repository;

import com.di.annotation.component.Component;

@Component
public class UserRepositoryImpl implements UserRepository {

    public UserRepositoryImpl() {
    }

    @Override
    public void save(String user) {
        System.out.println("Saving user to database: " + user);
    }

    @Override
    public String findById(Long id) {
        System.out.println("Finding user by id: " + id);
        return "1/John/john@jhonny.com";
    }
}