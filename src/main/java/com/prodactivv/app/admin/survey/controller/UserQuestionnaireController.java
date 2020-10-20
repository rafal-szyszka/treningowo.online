package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.Questionnaire;
import com.prodactivv.app.admin.survey.model.QuestionnaireResult;
import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.security.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.prodactivv.app.admin.survey.model.QuestionnaireResult.*;

@RestController
@RequestMapping(value = "/user/questionnaire")
public class UserQuestionnaireController {

    private final QuestionnaireService service;
    private final JwtUtils jwtUtils;

    public UserQuestionnaireController(QuestionnaireService service, JwtUtils jwtUtils) {
        this.service = service;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Questionnaire> getQuestionnaire(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getQuestionnaire(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<QuestionnaireResult> submitQuestionnaire(
            @PathVariable Long id,
            @RequestBody QuestionnaireResultDto answers,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(
                    service.submitQuestionnaire(
                            id,
                            answers,
                            Long.parseLong(jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID))
                    )
            );
        } catch (DisintegratedJwsException | NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
