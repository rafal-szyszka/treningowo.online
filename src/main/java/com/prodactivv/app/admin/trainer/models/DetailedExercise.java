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

    private String setCount;

    private String perSetCount;

    private String restBetweenSets;

    private String weight;

    private String rir;

    private String pace;

    private String time;

    @Column(columnDefinition = "TEXT", length = 10000)
    private String tips;

    private String workoutPlanPart;

    private String indexName;

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
        this.restBetweenSets = detailedExerciseDTO.getRestBetweenSets();
        this.weight = detailedExerciseDTO.getWeight();
        this.rir = detailedExerciseDTO.getRir();
        this.pace = detailedExerciseDTO.getPace();
        this.time = detailedExerciseDTO.getTime();
        this.tips = detailedExerciseDTO.getTips();
        this.workoutPlanPart = detailedExerciseDTO.getWorkoutPlanPart();
        this.indexName = detailedExerciseDTO.getIndexName();
        this.exercise = exercise;
    }

    public void update(DetailedExerciseDTO detailedExerciseDTO) {
        this.setCount = detailedExerciseDTO.setCount;
        this.perSetCount = detailedExerciseDTO.perSetCount;
        this.restBetweenSets = detailedExerciseDTO.restBetweenSets;
        this.weight = detailedExerciseDTO.weight;
        this.rir = detailedExerciseDTO.rir;
        this.pace = detailedExerciseDTO.pace;
        this.time = detailedExerciseDTO.time;
        this.tips = detailedExerciseDTO.tips;
        this.workoutPlanPart = detailedExerciseDTO.workoutPlanPart;
        this.indexName = detailedExerciseDTO.indexName;
    }

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailedExerciseDTO {

        protected Long id;
        protected String setCount;
        protected String perSetCount;
        protected String restBetweenSets;
        protected String weight;
        protected String rir;
        protected String pace;
        protected String time;
        protected String tips;
        protected Long exerciseId;
        protected String workoutPlanPart;
        protected String indexName;

        public static DetailedExerciseDTO of(DetailedExercise detailedExercise) {
            return new DetailedExerciseDTO(
                    detailedExercise.id,
                    detailedExercise.setCount,
                    detailedExercise.perSetCount,
                    detailedExercise.restBetweenSets,
                    detailedExercise.weight,
                    detailedExercise.rir,
                    detailedExercise.pace,
                    detailedExercise.time,
                    detailedExercise.tips,
                    detailedExercise.exercise.getId(),
                    detailedExercise.workoutPlanPart,
                    detailedExercise.indexName
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

        public static DetailedExerciseManagerDTO of(DetailedExercise detailedExercise) {
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
            simpleDTO.rir = detailedExercise.rir;
            simpleDTO.restBetweenSets = detailedExercise.restBetweenSets;
            simpleDTO.exerciseId = detailedExercise.exercise.getId();
            simpleDTO.workoutPlanPart = detailedExercise.workoutPlanPart;
            simpleDTO.indexName = detailedExercise.indexName;

            return simpleDTO;
        }
    }
}
