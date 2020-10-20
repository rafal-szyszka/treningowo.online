package com.prodactivv.app.admin.survey.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Questionnaire {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(
            mappedBy = "questionnaire",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Question> questions;

    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }

        questions.add(question);
    }
}
