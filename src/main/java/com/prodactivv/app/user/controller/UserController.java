package com.prodactivv.app.user.controller;

import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.IllegalAccessException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UnreachableFileStorageTypeException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.files.UnsupportedStorageTypeException;
import com.prodactivv.app.core.security.JwtUtils;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserProgress;
import com.prodactivv.app.user.model.UserSubscription;
import com.prodactivv.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @GetMapping(value = "/admin/users/{id}")
    public ResponseEntity<User.Dto.Full> getUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getFullUser(id));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }

    @GetMapping(value = "/admin/users/getAll")
    public ResponseEntity<List<User.Dto.Full>> getUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(userService.getUsersWithSubscriptions(token));
        } catch (DisintegratedJwsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/admin/user/{id}/questionnaires")
    public ResponseEntity<List<Pair<Long, String>>> getUsersSubscriptionPlanQuestionnaires(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getPlanQuestionnaires(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/admin/user/{id}/diet")
    public ResponseEntity<User.Dto.Diet> uploadDiet(@PathVariable Long id, @RequestParam MultipartFile diet) {
        try {
            return ResponseEntity.ok(userService.addDiet(id, diet));
        } catch (NotFoundException | IOException | UnsupportedStorageTypeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/admin/user/diet/{dietId}")
    public void uploadDiet(@PathVariable Long dietId) {
        try {
            userService.deleteDiet(dietId);
        } catch (NotFoundException | IOException | UnsupportedStorageTypeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/user/subscription/questionnaires")
    public ResponseEntity<List<Pair<Long, String>>> getUserSubscriptionPlanQuestionnaires(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(userService.getPlanQuestionnaires(Long.valueOf(jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID))));
        } catch (NotFoundException | DisintegratedJwsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/user/getDiet/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] downloadDiet(@PathVariable Long id) {
        try {
            return IOUtils.toByteArray(userService.getDietFile(id));
        } catch (IOException | NotFoundException | UnreachableFileStorageTypeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/user/{userId}/subscribe/{planId}")
    public ResponseEntity<UserSubscription.Dto.Full> subscribe(@PathVariable Long userId, @PathVariable Long planId) {
        try {
            return ResponseEntity.ok(userService.subscribe(userId, planId));
        } catch (UserNotFoundException | NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }

    @GetMapping(value = "/user/progress")
    public ResponseEntity<List<UserProgress.Dto.ShowProgress>> getUserProgress(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(userService.getProgress(jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID)));
        } catch (DisintegratedJwsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/user/progress")
    public ResponseEntity<UserProgress.Dto.ShowProgress> addProgress(@RequestBody UserProgress.Dto.CreateProgress progress, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(userService.addProgress(progress, jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID)));
        } catch (DisintegratedJwsException | NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/user/progress/{id}")
    public ResponseEntity<UserProgress.Dto.ShowProgress> updateProgress(@PathVariable Long id, @RequestBody UserProgress.Dto.CreateProgress progress, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(userService.updateProgress(id, progress, jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID)));
        } catch (DisintegratedJwsException | NotFoundException | IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/user/progress/{id}")
    public ResponseEntity<UserProgress.Dto.ShowProgress> deleteProgress(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(userService.deleteProgress(id, jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID)));
        } catch (DisintegratedJwsException | NotFoundException | IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/user/setAvatar")
    public ResponseEntity<User.Dto.Simple> setAvatar(@RequestHeader(HttpHeaders.AUTHORIZATION) String jws, MultipartFile avatarFile) {
        try {
            return ResponseEntity.ok(userService.setAvatar(
                    Long.valueOf(jwtUtils.obtainClaimWithIntegrityCheck(jws, JwtUtils.CLAIM_ID)),
                    avatarFile
            ));
        } catch (IOException | NotFoundException | DisintegratedJwsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
