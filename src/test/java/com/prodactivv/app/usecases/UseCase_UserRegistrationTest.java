package com.prodactivv.app.usecases;

import com.prodactivv.app.core.exceptions.MandatoryRegulationsNotAcceptedException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.service.RegistrationService;
import com.prodactivv.app.user.service.UserRegistrationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-tests.properties"
)
public class UseCase_UserRegistrationTest {

    @Autowired
    private RegistrationService registrationService;

    private final User.Dto.UserRegistration user = User.Dto.UserRegistration.builder()
            .sex("MALE")
            .email("test@mail.com")
            .birthday(LocalDate.parse("1994-03-20"))
            .name("TEST")
            .lastName("TEST")
            .password("test")
            .build();

    private User.Dto.Simple userDTO;

    @Test(expected = MandatoryRegulationsNotAcceptedException.class)
    public void test_registerUser_shouldThrowMandatoryRegulationsNotAcceptedException_1() throws MandatoryRegulationsNotAcceptedException {
        try {
            user.setTermsOfUse(false);
            user.setPrivacyPolicy(true);

            userDTO = registrationService.signUp(user);

            assertEquals(Long.valueOf(26), Long.valueOf(userDTO.getAge()));
            assertEquals("test@mail.com", userDTO.getEmail());
            assertEquals("user", userDTO.getRole());
        } catch (UserRegistrationException e) {
            assertNull(e);
        }
    }

    @Test(expected = MandatoryRegulationsNotAcceptedException.class)
    public void test_registerUser_shouldThrowMandatoryRegulationsNotAcceptedException_2() throws MandatoryRegulationsNotAcceptedException {
        try {
            user.setTermsOfUse(true);
            user.setPrivacyPolicy(false);

            userDTO = registrationService.signUp(user);

            assertEquals(Long.valueOf(26), Long.valueOf(userDTO.getAge()));
            assertEquals("test@mail.com", userDTO.getEmail());
            assertEquals("user", userDTO.getRole());
        } catch (UserRegistrationException e) {
            assertNull(e);
        }
    }

    @Test(expected = MandatoryRegulationsNotAcceptedException.class)
    public void test_registerUser_shouldThrowMandatoryRegulationsNotAcceptedException_3() throws MandatoryRegulationsNotAcceptedException {
        try {
            user.setTermsOfUse(false);
            user.setPrivacyPolicy(false);

            userDTO = registrationService.signUp(user);

            assertEquals(Long.valueOf(26), Long.valueOf(userDTO.getAge()));
            assertEquals("test@mail.com", userDTO.getEmail());
            assertEquals("user", userDTO.getRole());
        } catch (UserRegistrationException e) {
            assertNull(e);
        }
    }

    @Test
    public void test_registerUser_shouldRegisterUserAndCalculateItsBirthdayDate() {
        try {
            user.setTermsOfUse(true);
            user.setPrivacyPolicy(true);

            userDTO = registrationService.signUp(user);

            assertEquals(Long.valueOf(26), Long.valueOf(userDTO.getAge()));
            assertEquals("test@mail.com", userDTO.getEmail());
            assertEquals("user", userDTO.getRole());
        } catch (UserRegistrationException | MandatoryRegulationsNotAcceptedException e) {
            assertNull(e);
        }
    }
}
