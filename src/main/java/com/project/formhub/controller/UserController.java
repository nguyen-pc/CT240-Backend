package com.project.formhub.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.formhub.domain.User;
import com.project.formhub.service.UserService;
import com.project.formhub.util.error.IdInvalidException;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public User createNewUser(@RequestBody User postManUser) throws IdInvalidException {
        System.out.println(postManUser);
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email" + postManUser.getEmail() + "da ton tai, vui long su dung email khac");
        }
        String hasPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hasPassword);
        return this.userService.handleCreateUser(postManUser);
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User voi id = " + id + " khong ton tai");
        }

        this.userService.handleDeleteUser(id);
        return "Xoa thanh cong";
    }

}
