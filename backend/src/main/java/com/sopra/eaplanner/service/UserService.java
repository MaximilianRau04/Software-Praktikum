package com.sopra.eaplanner.service;

import com.sopra.eaplanner.model.User;
import com.sopra.eaplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User createUser(User user) {
        return repository.save(user);
    }

    public Iterable<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return repository.findById(id);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

}
