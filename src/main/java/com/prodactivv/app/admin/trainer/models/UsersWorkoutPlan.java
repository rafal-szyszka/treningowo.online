package com.prodactivv.app.admin.trainer.models;

import com.prodactivv.app.core.user.User;
import lombok.*;

import javax.persistence.*;

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

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
