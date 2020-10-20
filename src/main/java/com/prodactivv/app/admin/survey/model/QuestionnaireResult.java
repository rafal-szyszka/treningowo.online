package com.prodactivv.app.admin.survey.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodactivv.app.admin.survey.model.Answer.AnswerDto;
import com.prodactivv.app.core.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireResult {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "questionnaireResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Answer> answers;

    private LocalDate dateTaken;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionnaireResultDto {

        private List<AnswerDto> answers;

    }
}
