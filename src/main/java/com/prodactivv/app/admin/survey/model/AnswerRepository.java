package com.prodactivv.app.admin.survey.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a WHERE a.question.id = ?1 AND a.questionnaireResult.id = ?2")
    Optional<Answer> findAnswerForQuestionInQuestionnaireResult(Long questionId, Long questionnaireId);

}
