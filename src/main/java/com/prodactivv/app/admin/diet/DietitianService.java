package com.prodactivv.app.admin.diet;

import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietitianService {

    private final UserRepository userRepository;

    public List<User> getAllDietitians() {
        return userRepository.findAllDietitians();
    }


}
