package com.prodactivv.app.admin.survey.controller;

import com.prodactivv.app.admin.survey.model.Questionnaire;
import com.prodactivv.app.admin.survey.model.QuestionnaireResult;
import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.files.DatabaseFile;
import com.prodactivv.app.core.security.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.prodactivv.app.admin.survey.model.QuestionnaireResult.QuestionnaireResultDto;

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

    @GetMapping(value = "/resultFor/{id}")
    public ResponseEntity<List<QuestionnaireResult>> getResultForQuestionnaire(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    service.getQuestionnaireResultForUser(
                            id, Long.valueOf(jwtUtils.obtainClaimWithIntegrityCheck(auth, JwtUtils.CLAIM_ID))
                    )
            );
        } catch (DisintegratedJwsException e) {
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

    @PostMapping("/{id}/files")
    public ResponseEntity<List<DatabaseFile>> sendFiles(@PathVariable Long id, @RequestParam MultipartFile[] files, @RequestParam Map<String, String> filesMap) {

        try {
            return ResponseEntity.ok(
                    service.saveQuestionnaireFiles(id, files, filesMap)
            );
        } catch (NotFoundException | IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

    }
}
