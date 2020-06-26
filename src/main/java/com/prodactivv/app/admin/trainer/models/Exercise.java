package com.prodactivv.app.admin.trainer.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String videoUrl;

    @Column(length = 10000)
    private String description;

    public Exercise update(Exercise exercise) {
        this.id = exercise.id;
        this.name = exercise.name;
        this.videoUrl = exercise.videoUrl;
        this.description = exercise.description;

        return this;
    }
}
