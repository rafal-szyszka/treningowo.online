package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.Answer;
import com.prodactivv.app.admin.survey.model.Answer.AnswerDto;
import com.prodactivv.app.admin.survey.model.AnswerRepository;
import com.prodactivv.app.admin.survey.model.QuestionRepository;
import com.prodactivv.app.admin.survey.model.QuestionnaireResult;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnswerService {

    private final AnswerRepository repository;
    private final QuestionRepository questionRepository;

    public AnswerService(AnswerRepository repository, QuestionRepository questionRepository) {
        this.repository = repository;
        this.questionRepository = questionRepository;
    }

    public List<Answer> createAnswers(List<AnswerDto> answersDtos, QuestionnaireResult questionnaireResult) throws NotFoundException {
        List<Answer> list = new ArrayList<>();
        for (AnswerDto answersDto : answersDtos) {
            Optional<Answer> answer = createAnswer(answersDto, questionnaireResult);
            if (answer.isPresent()) {
                Answer save = repository.save(answer.get());
                list.add(save);
            }
        }
        return list;
    }

    private Optional<Answer> createAnswer(AnswerDto answerDto, QuestionnaireResult questionnaireResult) throws NotFoundException {
        if (answerDto != null) {
            return Optional.ofNullable(Answer.builder()
                    .answer(answerDto.getAnswer())
                    .question(questionRepository.findById(answerDto.getQuestionId()).orElseThrow(NotFoundException::new))
                    .questionnaireResult(questionnaireResult)
                    .file(answerDto.getFile())
                    .build());
        }

        return Optional.empty();
    }

}
