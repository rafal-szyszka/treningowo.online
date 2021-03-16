package com.prodactivv.app.admin.trainer.models;

import com.prodactivv.app.admin.trainer.models.WorkoutPlan.WorkoutPlanManagerDTO;
import com.prodactivv.app.user.model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UsersWorkoutPlan {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "workout_plan_id", referencedColumnName = "id")
    private WorkoutPlan workoutPlan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(columnDefinition = "boolean DEFAULT true")
    private Boolean isActive;

    private LocalDate createdAt;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UsersWorkoutPlanDTO {
        private Long id;
        private WorkoutPlanManagerDTO workoutPlan;
        private User.Dto.Simple user;
        private Boolean isActive;
        private LocalDate createdAt;
        private LocalDate until;

        public static UsersWorkoutPlanDTO of(UsersWorkoutPlan plan) {
            return new UsersWorkoutPlanDTO(
                    plan.id,
                    WorkoutPlanManagerDTO.of(plan.workoutPlan),
                    User.Dto.Simple.fromUser(plan.user),
                    plan.isActive,
                    plan.createdAt,
                    plan.createdAt.plusWeeks(plan.workoutPlan.getActivityWeeks().size())
            );
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class SimpleWorkoutPlanView {
            private Long id;
            private WorkoutPlanManagerDTO workoutPlan;
            private Boolean isActive;
            private LocalDate createdAt;
            private LocalDate until;

            public static SimpleWorkoutPlanView of(UsersWorkoutPlan plan) {
                return new SimpleWorkoutPlanView(
                        plan.id,
                        WorkoutPlanManagerDTO.of(plan.workoutPlan),
                        plan.isActive,
                        plan.createdAt,
                        plan.createdAt.plusWeeks(plan.workoutPlan.getActivityWeeks().size())
                );
            }

            public static SimpleWorkoutPlanView of(UsersWorkoutPlanDTO plan) {
                return new SimpleWorkoutPlanView(
                        plan.id,
                        plan.workoutPlan,
                        plan.isActive,
                        plan.createdAt,
                        plan.createdAt.plusWeeks(plan.workoutPlan.getActivityWeeks().size())
                );
            }
        }
    }

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class WorkoutPlanData {
            private Long id;
            private String name;
            private Boolean isActive;
            private Long daysLeft;
            private LocalDate createdAt;
            private LocalDate until;

            public static WorkoutPlanData fromWorkoutPlan(UsersWorkoutPlan userPlan) {
                return builder()
                        .id(userPlan.id)
                        .name(userPlan.workoutPlan.getName())
                        .isActive(userPlan.isActive)
                        .createdAt(userPlan.createdAt)
                        .until(userPlan.createdAt.plusWeeks(userPlan.workoutPlan.getActivityWeeks().size()))
                        .daysLeft(DAYS.between(LocalDate.now(), userPlan.createdAt.plusWeeks(userPlan.workoutPlan.getActivityWeeks().size())))
                        .build();
            }
        }

    }
}
