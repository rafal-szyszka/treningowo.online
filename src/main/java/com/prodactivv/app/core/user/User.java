package com.prodactivv.app.core.user;

import lombok.*;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    private Integer age;

    private String password;

    public static User of(UserDTO userDto) {
        return User.builder()
                .age(userDto.getAge())
                .email(userDto.getEmail())
                .lastName(userDto.getLastName())
                .name(userDto.getName())
                .email(userDto.getEmail())
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
