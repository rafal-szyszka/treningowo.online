package com.prodactivv.app.admin.trainer.models;

import com.prodactivv.app.admin.trainer.models.WorkoutPlan.WorkoutPlanManagerDTO;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserDTO;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

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
        private UserDTO user;
        private Boolean isActive;
        private LocalDate createdAt;
        private LocalDate until;

        public static UsersWorkoutPlanDTO of(UsersWorkoutPlan plan) {
            return new UsersWorkoutPlanDTO(
                    plan.id,
                    WorkoutPlanManagerDTO.of(plan.workoutPlan),
                    UserDTO.of(plan.user),
                    plan.isActive,
                    plan.createdAt,
                    plan.createdAt.plusWeeks(plan.workoutPlan.getActivityWeeks().size())
            );
        }
    }
}
