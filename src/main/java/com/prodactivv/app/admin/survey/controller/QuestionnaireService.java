package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.*;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserRepository;
import com.prodactivv.app.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.prodactivv.app.admin.survey.model.QuestionnaireResult.*;

@Service
public class QuestionnaireService {

    public static final String QUESTIONNAIRE_NOT_FOUND_MSG = "Questionnaire %s not found";

    private final AnswerService answerService;
    private final UserRepository userRepository;
    private final QuestionnaireRepository repository;
    private final QuestionRepository questionRepository;
    private final QuestionnaireResultRepository questionnaireResultRepository;

    public QuestionnaireService(AnswerService answerService, UserRepository userRepository, QuestionnaireRepository repository, QuestionRepository questionRepository, QuestionnaireResultRepository questionnaireResultRepository) {
        this.answerService = answerService;
        this.userRepository = userRepository;
        this.repository = repository;
        this.questionRepository = questionRepository;
        this.questionnaireResultRepository = questionnaireResultRepository;
    }

    public Questionnaire createQuestionnaire(String name) {
        return repository.save(
                Questionnaire.builder()
                        .name(name)
                        .build()
        );
    }

    public Questionnaire addQuestion(Question question, Long id) throws NotFoundException {
        Questionnaire questionnaire = repository.findById(id)
                .orElseThrow(new NotFoundException(
                        String.format(QUESTIONNAIRE_NOT_FOUND_MSG, id)
                ));
        question.setQuestionnaire(questionnaire);
        question = questionRepository.save(question);

        questionnaire.addQuestion(question);
        return repository.save(questionnaire);
    }

    public Questionnaire getQuestionnaire(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<QuestionnaireResult> getQuestionnaireResultForUser(Long id, Long userId) {
        return questionnaireResultRepository.findAllUserQuestionnaireResults(id, userId);
    }

    public QuestionnaireResult submitQuestionnaire(Long id, QuestionnaireResultDto answers, Long userId) throws NotFoundException {
        Questionnaire questionnaire = repository.findById(id).orElseThrow(NotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

        QuestionnaireResult result = QuestionnaireResult.builder()
                .dateTaken(LocalDate.now())
                .questionnaire(questionnaire)
                .user(user)
                .build();

        result = questionnaireResultRepository.save(result);
        result.setAnswers(
                answerService.createAnswers(answers.getAnswers(), result)
        );

        return result;
    }
}
