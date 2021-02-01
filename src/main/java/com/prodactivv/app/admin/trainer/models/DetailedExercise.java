package com.prodactivv.app.admin.trainer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DetailedExercise {

    @Id
    @GeneratedValue
    private Long id;

    private Integer setCount;

    private Integer perSetCount;

    private String weight;

    private String pace;

    private String time;

    @Column(length = 20000)
    private String tips;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToMany(mappedBy = "exercises")
    @JsonIgnore
    private Set<Workout> workouts;

    @OneToMany(mappedBy = "detailedExercise")
    @JsonIgnore
    private Set<ActivityDaySuperExercise> superDays;

    public void deleteActivityDaySuperExercise(ActivityDaySuperExercise superExercise) {
        if (superDays != null) {
            superDays.remove(superExercise);
        }
    }

    public DetailedExercise(DetailedExerciseDTO detailedExerciseDTO, Exercise exercise) {
        this.setCount = detailedExerciseDTO.getSetCount();
        this.perSetCount = detailedExerciseDTO.getPerSetCount();
        this.weight = detailedExerciseDTO.getWeight();
        this.pace = detailedExerciseDTO.getPace();
        this.time = detailedExerciseDTO.getTime();
        this.tips = detailedExerciseDTO.getTips();
        this.exercise = exercise;
    }

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailedExerciseDTO {

        protected Long id;
        protected Integer setCount;
        protected Integer perSetCount;
        protected String weight;
        protected String pace;
        protected String time;
        protected String tips;
        protected Long exerciseId;

        public static DetailedExerciseDTO of(DetailedExercise detailedExercise) {
            return new DetailedExerciseDTO(
                    detailedExercise.id,
                    detailedExercise.setCount,
                    detailedExercise.perSetCount,
                    detailedExercise.weight,
                    detailedExercise.pace,
                    detailedExercise.time,
                    detailedExercise.tips,
                    detailedExercise.exercise.getId()
            );
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailedExerciseManagerDTO extends DetailedExerciseDTO {

        protected String name;
        protected String videoUrl;
        protected String description;

        public static DetailedExerciseManagerDTO of (DetailedExercise detailedExercise) {
            DetailedExerciseManagerDTO simpleDTO = new DetailedExerciseManagerDTO(
                    detailedExercise.exercise.getName(),
                    detailedExercise.exercise.getVideoUrl(),
                    detailedExercise.exercise.getDescription()
            );

            simpleDTO.id = detailedExercise.id;
            simpleDTO.setCount = detailedExercise.setCount;
            simpleDTO.perSetCount = detailedExercise.perSetCount;
            simpleDTO.weight = detailedExercise.weight;
            simpleDTO.pace = detailedExercise.pace;
            simpleDTO.time = detailedExercise.time;
            simpleDTO.tips = detailedExercise.tips;
            simpleDTO.exerciseId = detailedExercise.exercise.getId();

            return simpleDTO;
        }
    }
}
