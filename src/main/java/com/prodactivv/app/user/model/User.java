package com.prodactivv.app.user.model;

import lombok.*;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

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

    private String role;

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
                .role(userDto.getRole())
                .build();
    }

    @PrePersist
    public void hashPassword() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8)
        );
        password = new String(Hex.encode(hash));
    }

    public enum Roles {
        USER("user"), ADMIN("admin");

        @Getter
        private final String roleName;

        Roles(String roleName) {
            this.roleName = roleName;
        }

        public static boolean hasUserAccess(String role) {
            return role.equalsIgnoreCase(ADMIN.getRoleName()) || role.equalsIgnoreCase(USER.getRoleName());
        }

        public static boolean hasAdminAccess(String role) {
            return role.equalsIgnoreCase(ADMIN.getRoleName());
        }
    }

}
