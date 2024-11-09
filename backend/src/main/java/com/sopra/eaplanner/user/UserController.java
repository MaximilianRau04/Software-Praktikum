package com.sopra.eaplanner.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/users")
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User requestBody) {
        return userService.createUser(requestBody);
    }

    /* may be obsolete
    @PostMapping("/users/admin")
    public User createAdminUser(@Valid @RequestBody User requestBody){
        requestBody.setUsername(requestBody.getUsername());
        requestBody.setRole(User.Role.ADMIN);
        return userService.createUser(requestBody);
    }
    */

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable("id") Long id, @Valid @RequestBody User requestBody) {
        return userService.updateUser(id, requestBody);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
