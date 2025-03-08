package com.project.formhub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.formhub.domain.User;
import com.project.formhub.domain.response.ResCreateUserDTO;
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

    public User handleGetUserByUserName(String userName) {
        return this.userRepository.findByEmail(userName);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUserName(email);

        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }

    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        return resCreateUserDTO;
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    public User handleUpdateUser(User user) {
        // Check if the user exists in the database
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null and must have a valid ID");
        }

        // Find the user by ID
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            throw new IllegalArgumentException("User with id = " + user.getId() + " does not exist");
        }

        // Update the user's fields (email, full name, etc.)
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setPassword(user.getPassword()); // If password is being updated, it will be already hashed
        existingUser.setCreatedAt(user.getCreatedAt());
        // Save the updated user back to the database
        return userRepository.save(existingUser);
    }
}
