package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.*;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.files.DatabaseFile;
import com.prodactivv.app.core.files.DatabaseFileService;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.prodactivv.app.admin.survey.model.QuestionnaireResult.QuestionnaireResultDto;

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
        question.setQuestionOrder(questionnaire.getQuestions().size() + 1L);
        question.setQuestionnaire(questionnaire);
        question = questionRepository.save(question);

        questionnaire.addQuestion(question);
        return repository.save(questionnaire);
    }

    public Question editQuestion(Question question, Long questionId) throws NotFoundException {
        Question editable = getQuestion(questionId);
        editable.setTitle(question.getTitle());
        editable.setOptions(question.getOptions());
        editable.setType(question.getType());
        editable.setMandatory(question.getMandatory());

        return questionRepository.save(editable);
    }

    public Question getQuestion(Long questionId) throws NotFoundException {
        return questionRepository.findById(questionId).orElseThrow(new NotFoundException(String.format("Question %s not found!", questionId)));
    }

    public void deleteQuestion(Long questionId) throws NotFoundException {
        Question question = questionRepository.findById(questionId).orElseThrow(new NotFoundException(String.format("Question %s not found!", questionId)));
        questionRepository.delete(question);
    }

    public Questionnaire getQuestionnaire(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(new NotFoundException(String.format("Questionnaire %s not found!", id)));
    }

    public List<QuestionnaireResult> getQuestionnaireResultForUser(Long id, Long userId) {
        return questionnaireResultRepository.findAllUserQuestionnaireResults(id, userId);
    }

    public QuestionnaireResult submitQuestionnaire(Long id, QuestionnaireResultDto answers, Long userId) throws NotFoundException {
        Questionnaire questionnaire = repository.findById(id).orElseThrow(new NotFoundException(String.format("Questionnaire %s not found!", id)));
        User user = userRepository.findById(userId).orElseThrow(new NotFoundException(String.format("User %s not found!", userId)));

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

    public Question editQuestionMoveByStep(Long questionId, Long step) throws NotFoundException {
        Question question = questionRepository.findById(questionId).orElseThrow(new NotFoundException(String.format("Question %s not found.", questionId)));
        List<Question> collect = question.getQuestionnaire().getQuestions().stream()
                .filter(q -> q.getQuestionOrder().equals(question.getQuestionOrder() + step))
                .collect(Collectors.toList());

        if (collect.size() > 1) {
            throw new IllegalStateException(String.format("Found %s questions with order %s", collect.size(), question.getQuestionOrder() + step));
        }

        if (collect.size() == 1) {
            Question questionToSwitch = collect.get(0);
            Long tmpOrder = questionToSwitch.getQuestionOrder();
            questionToSwitch.setQuestionOrder(question.getQuestionOrder());
            question.setQuestionOrder(tmpOrder);
            questionRepository.save(questionToSwitch);
            return questionRepository.save(question);
        }

        return question;
    }
}
