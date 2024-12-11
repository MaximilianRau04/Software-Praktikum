package com.sopra.eaplanner.user;

import com.sopra.eaplanner.user.dtos.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public Optional<User> getUserByUsername(@RequestParam("username") String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("")
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("")
    public User createUser(@Valid @RequestBody UserDTO requestBody) {
        return userService.createUser(requestBody);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO requestBody) {
        return userService.updateUser(id, requestBody);
    }

    @PutMapping("/{userId}/eventRegistration")
    public User registerUserToEvent(@PathVariable Long userId, @RequestParam Long eventId) {
        return userService.registerUserToEvent(userId, eventId);
    }

    @PutMapping("/{userId}/eventRemoval")
    public User removeUserFromEvent(@PathVariable Long userId, @RequestParam Long eventId) {
        return userService.removeUserFromEvent(userId, eventId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}