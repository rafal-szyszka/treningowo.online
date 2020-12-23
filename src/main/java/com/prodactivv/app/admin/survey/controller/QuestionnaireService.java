package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.*;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.files.DatabaseFile;
import com.prodactivv.app.core.files.DatabaseFileService;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserRepository;
import com.prodactivv.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static com.prodactivv.app.admin.survey.model.QuestionnaireResult.*;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    public static final String QUESTIONNAIRE_NOT_FOUND_MSG = "Questionnaire %s not found";

    private final AnswerService answerService;
    private final UserRepository userRepository;
    private final QuestionnaireRepository repository;
    private final QuestionRepository questionRepository;
    private final QuestionnaireResultRepository questionnaireResultRepository;
    private final DatabaseFileService fileService;

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

    public List<DatabaseFile> saveQuestionnaireFiles(Long id, MultipartFile[] files, Map<String, String> filesMap) throws NotFoundException, IOException {
        QuestionnaireResult questionnaireResult = questionnaireResultRepository
                .findById(id).orElseThrow(new NotFoundException(String.format("Questionnaire %s not found!", id)));

        List<DatabaseFile> savedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            Long questionId = Long.valueOf(filesMap.get(file.getOriginalFilename()));
            if (questionId != 0L) {
                DatabaseFile savedFile = fileService.uploadFileToLocalStorage(file);
                answerService.createAnswers(
                        Collections.singletonList(
                                new Answer.AnswerDto(
                                        questionId,
                                        "",
                                        savedFile
                                )
                        ),
                        questionnaireResult
                );

                savedFiles.add(savedFile);
            }
        }

        return savedFiles;

    }
}