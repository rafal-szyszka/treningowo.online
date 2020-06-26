package com.prodactivv.app.admin.trainer.models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.prodactivv.app.admin.trainer.models.Workout.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDay {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(length = 10000)
    private String tips;

    @ManyToMany
    @JoinTable(
            name = "activity_day_workout",
            joinColumns = @JoinColumn(name = "activity_day_id"),
            inverseJoinColumns = @JoinColumn(name = "workout_id"))
    private List<Workout> workouts;

    @ManyToMany
    @JoinTable(
            name = "activity_day_super_exercies",
            joinColumns = @JoinColumn(name = "activity_day_id"),
            inverseJoinColumns = @JoinColumn(name = "detailed_exercise_id"))
    private List<DetailedExercise> superExercises;

    public void addWorkout(Workout workout) {
        if (workouts == null) {
            workouts = new ArrayList<>();
        }

        workouts.add(workout);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivityDayDTO {

        private String name;
        private String tips;
        private List<WorkoutDTO> workouts;

    }
}
