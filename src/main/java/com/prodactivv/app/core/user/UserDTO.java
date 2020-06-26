package com.prodactivv.app.core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodactivv.app.registration.service.StrongPassword;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String name;
    private String lastName;
    private Integer age;

    @JsonIgnore @StrongPassword
    private String password;

    public static UserDTO of(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .age(user.getAge())
                .build();
    }
}
