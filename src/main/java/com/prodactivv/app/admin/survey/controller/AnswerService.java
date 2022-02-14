package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.Answer;
import com.prodactivv.app.admin.survey.model.Answer.AnswerDto;
import com.prodactivv.app.admin.survey.model.AnswerRepository;
import com.prodactivv.app.admin.survey.model.QuestionRepository;
import com.prodactivv.app.admin.survey.model.QuestionnaireResult;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.files.DatabaseFile;
import com.prodactivv.app.core.files.DatabaseFileService;
import com.prodactivv.app.core.files.UnsupportedStorageTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository repository;
    private final DatabaseFileService fileService;
    private final QuestionRepository questionRepository;

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

    public void deleteAnswer(Answer answer) throws IOException, UnsupportedStorageTypeException {
        DatabaseFile answerFile = answer.getFile();
        if (answerFile != null) {
            answer.setFile(null);
            fileService.deleteFile(answerFile);
        }
        log.info(String.format("Deleting answer %s", answer.getId()));
        repository.delete(answer);
        repository.delete(answer);
    }

    public Answer getAnswer(Long questionId, Long questionnaireResultId) throws NotFoundException {
        return repository.findAnswerForQuestionInQuestionnaireResult(questionId, questionnaireResultId)
                .orElseThrow(new NotFoundException(String.format("Answer for question %s in questionnaire result %s not found!", questionId, questionnaireResultId)));
    }
}
