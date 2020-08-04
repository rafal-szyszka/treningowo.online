package com.prodactivv.app.user.service;

import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserDTO;
import com.prodactivv.app.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public RegistrationService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public UserDTO signUp(User user) throws UserRegistrationException {
        try {
            if (!userRepository.findUserByEmail(user.getEmail()).isPresent()) {
                user.setSignedUpDate(LocalDate.now());
                user.setAge(userService.calculateUserAge(user));
                return UserDTO.of(userRepository.save(user));
            } else {
                throw new UserRegistrationException("Email is already taken");
            }
        } catch (Exception e) {
            throw new UserRegistrationException(e.getMessage());
        }
    }

}
