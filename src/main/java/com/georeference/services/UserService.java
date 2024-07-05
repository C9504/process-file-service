package com.georeference.services;

import com.georeference.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void createUser(String username, String password);
    Optional<User> getUserById(Long userId);
    List<User> getAllUsers();
}
