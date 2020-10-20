package com.prodactivv.app.admin.survey.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(length = 2048)
    private String answer;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_result_id")
    private QuestionnaireResult questionnaireResult;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDto {
        private Long questionId;
        private String answer;
    }
}
