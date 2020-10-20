package com.prodactivv.app.admin.survey.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue
    private Long id;

    private String type;

    private String title;

    @Column(length = 2048)
    private String options;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

    @JsonIgnore
    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Answer> answers;
}