package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.Question;
import com.prodactivv.app.admin.survey.model.Questionnaire;
import com.prodactivv.app.admin.survey.model.QuestionnaireResult;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.files.UnsupportedStorageTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/admin/questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService service;

    public QuestionnaireController(QuestionnaireService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Questionnaire> createQuestionnaire(@RequestParam String name) {
        return ResponseEntity.ok(service.createQuestionnaire(name));
    }

    @PostMapping(value = "/{id}/addQuestion")
    public ResponseEntity<Questionnaire> addQuestion(@RequestBody Question question, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.addQuestion(question, id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/{id}/imageQuestion/{typeName}")
    public ResponseEntity<Questionnaire> addImageQuestion(@PathVariable Long id, @PathVariable String typeName, @RequestParam MultipartFile image) {
        try {
            return ResponseEntity.ok(service.addImageQuestion(id, image, typeName));
        } catch (NotFoundException | IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/imageQuestion/{questionId}")
    public ResponseEntity<Question> editImageQuestion(@PathVariable Long questionId, @RequestParam MultipartFile image) {
        try {
            return ResponseEntity.ok(service.editImageQuestion(questionId, image));
        } catch (NotFoundException | IOException | UnsupportedStorageTypeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/question/{questionId}")
    public ResponseEntity<Question> getQuestion(@PathVariable Long questionId) {
        try {
            return ResponseEntity.ok(service.getQuestion(questionId));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/question/{questionId}")
    public ResponseEntity<Question> editQuestion(@RequestBody Question question, @PathVariable Long questionId) {
        try {
            return ResponseEntity.ok(service.editQuestion(question, questionId));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/question/{questionId}/move/{step}")
    public ResponseEntity<Question> editQuestionMoveByStep(@PathVariable Long questionId, @PathVariable Long step) {
        try {
            return ResponseEntity.ok(service.editQuestionMoveByStep(questionId, step));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/question/{questionId}")
    public ResponseEntity<Long> deleteQuestion(@PathVariable Long questionId) {
        try {
            service.deleteQuestion(questionId);
            return ResponseEntity.ok(questionId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

    }

    @GetMapping(value = "/{id}/user/{userId}")
    public ResponseEntity<List<QuestionnaireResult>> getQuestionnaireResultForUser(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(service.getQuestionnaireResultForUser(id, userId));
    }

}
