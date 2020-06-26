package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.DetailedExercise;
import com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseDTO;
import com.prodactivv.app.admin.trainer.models.Exercise;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping(value = "/detail")
    public ResponseEntity<DetailedExercise> provideDetails(@RequestBody DetailedExerciseDTO detailedExerciseDTO) {
        try {
            return ResponseEntity.ok(exerciseService.provideDetails(detailedExerciseDTO));
        } catch (ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping
    public ResponseEntity<Exercise> addExercise(@RequestBody Exercise exercise) {
        return ResponseEntity.ok(exerciseService.createExercise(exercise));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise exercise) {
        try {
            return ResponseEntity.ok(exerciseService.updateExercise(id, exercise));
        } catch (ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Exercise> deleteExercise(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(exerciseService.deleteExercise(id));
        } catch (ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Exercise> getExercise(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(exerciseService.getExercise(id));
        } catch (ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getExercises());
    }
}
