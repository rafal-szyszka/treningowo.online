package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.Question;
import com.prodactivv.app.admin.survey.model.Questionnaire;
import com.prodactivv.app.admin.survey.model.QuestionnaireResult;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService service;

    public QuestionnaireController(QuestionnaireService service) {
        this.service = service;
    }

    @GetMapping
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

    @GetMapping(value = "/{id}/user/{userId}")
    public ResponseEntity<List<QuestionnaireResult>> getQuestionnaireResultForUser(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(service.getQuestionnaireResultForUser(id, userId));
    }

}
