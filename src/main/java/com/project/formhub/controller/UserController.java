package com.project.formhub.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.formhub.domain.User;
import com.project.formhub.service.UserService;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createNewUser(@RequestBody User postManUser) {
        System.out.println(postManUser);
        // String hasPassword = this.passwordEncoder.encode(postManUser.getPassword());
        // postManUser.setPassword(hasPassword);
        return this.userService.handleCreateUser(postManUser);
    }

}
