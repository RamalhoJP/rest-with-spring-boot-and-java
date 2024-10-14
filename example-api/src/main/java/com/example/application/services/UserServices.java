package com.example.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import com.example.application.repositories.UserRepository;

@Service
public class UserServices implements UserDetailsService {

    @Autowired
    UserRepository repository;
    
    public UserServices(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.findByUsername(username);
        if(user != null) {
            System.out.println(user.getUsername());
            System.out.println(user.getPassword());
            return user;
        } else {
            throw new UsernameNotFoundException("User" + username + " not found!");
        }
    }
    
}