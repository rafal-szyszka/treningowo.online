//package com.prodactivv.app.user.model;
//
//import com.prodactivv.app.subscription.model.SubscriptionPlan;
//import com.prodactivv.app.subscription.model.SubscriptionPlan.SubscriptionPlanDto;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static java.time.temporal.ChronoUnit.DAYS;
//
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserSubscriptionDTO {
//
//    private List<SubscriptionPlan> subscriptions;
//    private LocalDate until;
//    private UserDTO user;
//    private Long expiresInDays;
//
//    public UserSubscriptionDTO(UserSubscriptionDTO userSubscriptionDTO) {
//        subscriptions = userSubscriptionDTO.subscriptions;
//        until = userSubscriptionDTO.until;
//        user = userSubscriptionDTO.user;
//        expiresInDays = userSubscriptionDTO.expiresInDays;
//    }
//
//    public static UserSubscriptionDTO of(UserSubscription userSubscription) {
//        return new UserSubscriptionDTO(
//                Collections.singletonList(userSubscription.getPlan()),
//                userSubscription.getUntil(),
//                UserDTO.of(userSubscription.getUser()),
//                DAYS.between(LocalDate.now(), userSubscription.getUntil())
//        );
//    }
//
//    public static SimpleSubscriptionView simpleOf(UserSubscription userSubscription) {
//        return SimpleSubscriptionView.of(userSubscription);
//    }
//
//    public static UserSubscriptionDTO notSubscribedOf(User user) {
//        return NotSubscribedUser.builder()
//                .user(UserDTO.of(user))
//                .expiresInDays(Long.MAX_VALUE)
//                .build();
//    }
//
//    public static UserSubscriptionDTO notSubscribedOf(UserDTO user) {
//        return NotSubscribedUser.builder()
//                .user(user)
//                .build();
//    }
//
//    @Getter
//    @Setter
//    public static class UsersListDTO extends UserSubscriptionDTO {
//
//        private Integer workoutPlansCount;
//        private Long workoutPlanExpiresInDays;
//
//        public UsersListDTO(UserSubscriptionDTO userSubscriptionDTO) {
//            super(userSubscriptionDTO);
//        }
//
//        public static UsersListDTO of(UserSubscriptionDTO userSubscriptionDTO) {
//            return new UsersListDTO(userSubscriptionDTO);
//        }
//
//    }
//
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class NotSubscribedUser extends UserSubscriptionDTO {
//        private UserDTO user;
//    }
//
//}
