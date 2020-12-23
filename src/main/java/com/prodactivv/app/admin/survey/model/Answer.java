package com.prodactivv.app.admin.survey.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodactivv.app.core.files.DatabaseFile;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    private DatabaseFile file;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_result_id")
    private QuestionnaireResult questionnaireResult;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class AnswerDto {

        @NonNull
        private Long questionId;

        @NonNull
        private String answer;

        private DatabaseFile file;
    }
}
