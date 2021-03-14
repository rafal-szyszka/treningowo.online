package com.prodactivv.app.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import lombok.*;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@ToString
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

    @JsonIgnore
    private String password;

    private String sex;

    private String role;

    @PrePersist
    public void hashPassword() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8)
        );
        password = new String(Hex.encode(hash));
    }

    public enum Roles {
        USER("user"), ADMIN("admin"), DIETITIAN("dietitian");

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

        public static boolean isDietitian(String role) {
            return role.equalsIgnoreCase(DIETITIAN.getRoleName());
        }

        public static boolean isAdmin(String role) {
            return role.equalsIgnoreCase(ADMIN.getRoleName());
        }

        public static boolean isUser(String role) {
            return role.equalsIgnoreCase(USER.getRoleName());
        }
    }

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Full {
            private Long id;
            private String email;
            private String name;
            private String lastName;
            private Integer age;
            private LocalDate birthday;
            private LocalDate signedUpDate;
            private String sex;
            private String role;
            private String token;

            public static Full fromUser(User user) {
                return builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .lastName(user.getLastName())
                        .age(user.getAge())
                        .birthday(user.getBirthday())
                        .signedUpDate(user.getSignedUpDate())
                        .sex(user.getSex())
                        .role(user.getRole())
                        .build();
            }

            public User toUser() {
                return User.builder()
                        .age(getAge())
                        .email(getEmail())
                        .lastName(getLastName())
                        .name(getName())
                        .email(getEmail())
                        .birthday(getBirthday())
                        .signedUpDate(getSignedUpDate())
                        .sex(getSex())
                        .role(getRole())
                        .build();
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Invitation {
            private String email;
            private String subject;
            private String message;
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class UserRegistration {
            private String email;
            private String name;
            private String lastName;
            private LocalDate birthday;
            private String password;
            private String sex;

            public User toUser() {
                return User.builder()
                        .email(email)
                        .lastName(lastName)
                        .name(name)
                        .birthday(birthday)
                        .password(password)
                        .sex(sex)
                        .build();
            }

        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Subscriptions {
            private List<UserSubscription.Dto.FullUserLess> subscriptions;
            private Full user;

            public boolean isSubscribedToPlanWithDiet() {
                return subscriptions.stream().anyMatch(subscription -> subscription.getPlan().hasDietPlan());
            }

            public boolean isSubscribedToPlanWithTrainings() {
                return subscriptions.stream().anyMatch(subscription -> subscription.getPlan().hasTrainingPlan());
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Workouts {
            private List<UsersWorkoutPlan.Dto.WorkoutPlanData> workoutPlans;
            private Full user;
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class SubscriptionsAndWorkouts {
            private List<UserSubscription.Dto.FullUserLess> subscriptions;
            private List<UsersWorkoutPlan.Dto.WorkoutPlanData> workoutPlans;
            private Full user;

            public boolean isSubscribedToPlanWithDiet() {
                return subscriptions.stream().anyMatch(subscription -> subscription.getPlan().hasDietPlan());
            }

            public boolean isSubscribedToPlanWithTrainings() {
                return subscriptions.stream().anyMatch(subscription -> subscription.getPlan().hasTrainingPlan());
            }
        }
    }

}
