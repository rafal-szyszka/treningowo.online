package com.prodactivv.app.user.service;

import com.prodactivv.app.core.security.AuthService;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserDTO;
import com.prodactivv.app.user.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public RegistrationService(UserRepository userRepository, UserService userService, AuthService authService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authService = authService;
    }

    public UserDTO signUp(User user) throws UserRegistrationException {
        try {
            if (userRepository.findUserByEmail(user.getEmail()).isEmpty()) {
                user.setSignedUpDate(LocalDate.now());
                user.setAge(userService.calculateUserAge(user));
                user.setRole(User.Roles.USER.getRoleName());
                UserDTO userDto = UserDTO.of(userRepository.save(user));
                System.out.println(userDto.toString());
                userDto.setToken(authService.generateTokenForUser(user).getToken());
                return userDto;
            } else {
                throw new UserRegistrationException("Email is already taken");
            }
        } catch (Exception e) {
            throw new UserRegistrationException(e.getMessage());
        }
    }

}
