package com.prodactivv.app.core.user;

import com.prodactivv.app.core.subscription.SubscriptionPlan;
import lombok.*;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Email
    @Column(name = "email", unique = true)
    private String email;

    private String name;

    private String lastName;

    private LocalDate birthday;

    private LocalDate signedUpDate;

    private Integer age;

    private String password;

    private String sex;

    public static User of(UserDTO userDto) {
        return User.builder()
                .age(userDto.getAge())
                .email(userDto.getEmail())
                .lastName(userDto.getLastName())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .birthday(userDto.getBirthday())
                .signedUpDate(userDto.getSignedUpDate())
                .sex(userDto.getSex())
                .build();
    }

    @PrePersist
    public void hashPassword() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8)
        );
        password = new String(Hex.encode(hash));
    }

}
