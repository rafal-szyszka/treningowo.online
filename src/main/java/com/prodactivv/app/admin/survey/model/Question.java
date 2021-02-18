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

    @Column(columnDefinition = "TEXT", length = 10000)
    private String type;

    @Column(columnDefinition = "TEXT", length = 10000)
    private String title;

    @Column(columnDefinition = "TEXT", length = 10000)
    private String options;

    @Column(columnDefinition = "BIGINT(20) DEFAULT 0")
    private Long mandatory;

    private Long questionOrder;

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