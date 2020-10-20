package com.prodactivv.app.admin.survey.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireResultRepository extends JpaRepository<QuestionnaireResult, Long> {

    @Query("SELECT qr FROM QuestionnaireResult qr WHERE qr.questionnaire.id = ?1 AND qr.user.id = ?2 ORDER BY qr.dateTaken DESC")
    List<QuestionnaireResult> findAllUserQuestionnaireResults(Long id, Long userId);
}
