package com.prodactivv.app.core.security;

import com.prodactivv.app.user.model.UserSubscriptionDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {

    private Long userId;
    private String token;
    private String userEmail;
    private String userRole;
    private LocalDateTime validUntil;
    private UserSubscriptionDTO userData;

}
