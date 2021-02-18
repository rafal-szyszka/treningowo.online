package com.prodactivv.app.usecases;

import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserDTO;
import com.prodactivv.app.user.service.RegistrationService;
import com.prodactivv.app.user.service.UserRegistrationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-tests.properties"
)
public class UseCase_UserRegistrationTest {

    @Autowired
    private RegistrationService registrationService;

    private final User user = User.builder()
            .sex("MALE")
            .email("test@mail.com")
            .birthday(LocalDate.parse("1994-03-20"))
            .name("TEST")
            .lastName("TEST")
            .password("test")
            .build();

    private UserDTO userDTO;

    @Test
    public void test_registerUser_shouldRegisterUserAndCalculateItsBirthdayDate() {
        try {
            userDTO = registrationService.signUp(user);

            assertEquals(Long.valueOf(26), Long.valueOf(userDTO.getAge()));
            assertEquals("test@mail.com", userDTO.getEmail());
            assertEquals("user", userDTO.getRole());
            assertNotEquals("test", userDTO.getPassword());
        } catch (UserRegistrationException e) {
            assertNull(e);
        }
    }

}
