package com.prodactivv.app.admin.trainer.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

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

    @Column(length = 10000)
    private String tips;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "exercise_id", referencedColumnName = "id")
    private Exercise exercise;

    @ManyToMany(mappedBy = "exercises")
    private List<Workout> workouts;

    @ManyToMany(mappedBy = "superExercises")
    private List<ActivityDay> superDays;

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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailedExerciseDTO {

        private Integer setCount;
        private Integer perSetCount;
        private String weight;
        private String pace;
        private String time;
        private String tips;
        private Long exerciseId;

    }

}
