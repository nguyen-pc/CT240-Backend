package com.project.formhub.service;

import java.util.Optional;

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

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }
}
