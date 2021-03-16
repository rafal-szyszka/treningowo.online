package com.prodactivv.app.core.security;

import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan.UsersWorkoutPlanDTO.SimpleWorkoutPlanView;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserDiet;
import com.prodactivv.app.user.model.UserSubscription;
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
    private User.Dto.Simple user;
    private List<UserSubscription.Dto.FullUserLess> subscriptions;
    private List<SimpleWorkoutPlanView> workoutPlans;
    private List<UserDiet.Dto.Diet> diets;

}
