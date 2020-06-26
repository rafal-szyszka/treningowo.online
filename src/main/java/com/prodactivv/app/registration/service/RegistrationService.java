package com.prodactivv.app.registration.service;

import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserDTO;
import com.prodactivv.app.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;

    @Autowired
    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO signUp(User user) throws UserRegistrationException {
        try {
            if (!userRepository.findUserByEmail(user.getEmail()).isPresent()) {
                return UserDTO.of(userRepository.save(user));
            } else {
                throw new UserRegistrationException("Email is already taken");
            }
        } catch (Exception e) {
            throw new UserRegistrationException(e.getMessage());
        }
    }

}
