package com.prodactivv.app.admin.trainer.models;

import lombok.*;

import javax.persistence.*;
import java.util.*;

import static com.prodactivv.app.admin.trainer.models.DetailedExercise.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Workout {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "workout_exercies",
            joinColumns = @JoinColumn(name = "workout_id"),
            inverseJoinColumns = @JoinColumn(name = "detailed_exercise_id"))
    private Set<DetailedExercise> exercises;

    public void addExercise(DetailedExercise detailedExercise) {
        if (exercises == null) {
            exercises = new HashSet<>();
        }

        exercises.add(detailedExercise);
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutDTO {

        private String name;
        private List<DetailedExerciseDTO> exercises;

        public static WorkoutDTO getEmpty(String name) {
            return new WorkoutDTO(
                    name,
                    Collections.emptyList()
            );
        }
    }
}
