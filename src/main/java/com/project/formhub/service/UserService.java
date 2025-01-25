package com.project.formhub.service;

import org.springframework.stereotype.Service;

import com.project.formhub.domain.User;
import com.project.formhub.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }
}
