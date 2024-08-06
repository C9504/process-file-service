package com.georeference.impl;

import com.georeference.appregca.entities.User;
import com.georeference.appregca.repositories.UserRepository;
import com.georeference.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(String document) {
        return userRepository.findByDocument(document);
    }
}
