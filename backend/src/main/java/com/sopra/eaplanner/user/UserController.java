package com.sopra.eaplanner.user;

import com.sopra.eaplanner.user.dtos.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/search")
    public Optional<User> getUserByUsername(@RequestParam("username") String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users")
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody UserDTO requestBody) {
        return userService.createUser(requestBody);
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO requestBody) {
        return userService.updateUser(id, requestBody);
    }

    @PutMapping("/users/{userId}/eventRegistration")
    public User registerUserToEvent(@PathVariable Long userId, @RequestParam Long eventId) {
        return userService.registerUserToEvent(userId, eventId);
    }

    @PutMapping("users/{userId}/eventRemoval")
    public User removeUserFromEvent(@PathVariable Long userId, @RequestParam Long eventId) {
        return userService.removeUserFromEvent(userId, eventId);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}