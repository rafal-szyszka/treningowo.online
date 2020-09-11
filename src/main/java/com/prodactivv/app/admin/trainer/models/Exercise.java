package com.prodactivv.app.admin.trainer.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Exercise {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String videoUrl;

    @Column(length = 10000)
    private String description;

    private String section;

    @OneToMany(
            mappedBy = "exercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<DetailedExercise> detailedExercises;

    public Exercise update(Exercise exercise) {
        this.id = exercise.id;
        this.name = exercise.name;
        this.videoUrl = exercise.videoUrl;
        this.description = exercise.description;
        this.section = exercise.section;

        return this;
    }
}
