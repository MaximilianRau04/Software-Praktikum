package com.sopra.eaplanner.controller;

import com.sopra.eaplanner.model.User;
import com.sopra.eaplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public int addUser(@RequestBody User user) {
        return service.saveUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return service.getUser(id);
    }

}
