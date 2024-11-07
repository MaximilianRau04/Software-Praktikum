package com.sopra.eaplanner.service;

import com.sopra.eaplanner.model.User;
import com.sopra.eaplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public int saveUser(User user) {
        return repository.save(user);
    }

    public User getUser(Long id) {
        return repository.findByUsername(id);
    }
}
