package com.sopra.eaplanner.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(User requestBody) {
        return userRepository.save(requestBody);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User requestBody) {
        requestBody.setId(id);
        if (userRepository.existsById(id)) {
            User userToUpdate = userRepository.findById(id).get();
            userToUpdate.updateInformation(requestBody);
            return userRepository.save(userToUpdate);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("User with id %s not found", id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
