package com.prodactivv.app.core.security;

import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan.UsersWorkoutPlanDTO.SimpleWorkoutPlanView;
import com.prodactivv.app.user.model.UserDTO;
import com.prodactivv.app.user.model.UserSubscriptionDTO.SimpleSubscriptionView;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {

    private String token;
    private String userEmail;
    private String userRole;
    private LocalDateTime validUntil;
    private UserDTO user;
    private SimpleSubscriptionView subscription;
    private List<SimpleWorkoutPlanView> workoutPlans;

}
