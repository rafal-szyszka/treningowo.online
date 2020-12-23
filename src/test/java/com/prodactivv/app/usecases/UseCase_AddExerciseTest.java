package com.prodactivv.app.usecases;

import com.prodactivv.app.admin.trainer.models.DetailedExercise;
import com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseDTO;
import com.prodactivv.app.admin.trainer.models.Exercise;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.admin.trainer.workout.ExerciseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-tests.properties"
)
public class UseCase_AddExerciseTest {

    public static final long NON_EXISTING_EXERCISE_ID = -1L;

    @Autowired
    private ExerciseService exerciseService;

    private Long existingExerciseId;

    @Before
    public void setUp() {
        Exercise exercise = exerciseService.createExercise(
                Exercise.builder()
                        .name("ExerciseName")
                        .section("Section")
                        .videoUrl("https://some.url.com/n89w34yt0w93@#$%%VG@#$%2345-2908345wrel")
                        .build()
        );

        existingExerciseId = exercise.getId();
    }

    @Test
    public void test_createNewExercise() {
        Exercise exercise = exerciseService.createExercise(
                Exercise.builder()
                        .name("ExerciseName")
                        .section("Section")
                        .videoUrl("https://some.url.com/n89w34yt0w93@#$%%VG@#$%2345-2908345wrel")
                        .build()
        );

        assertNotNull(exercise);

        existingExerciseId = exercise.getId();

        assertEquals("ExerciseName", exercise.getName());
        assertEquals("Section", exercise.getSection());
        assertEquals("https://some.url.com/n89w34yt0w93@#$%%VG@#$%2345-2908345wrel", exercise.getVideoUrl());
    }

    @Test
    public void test_provideDetailsToExistingExercise() throws ExerciseNotFoundException {
        DetailedExercise detailedExercise = exerciseService.provideDetails(
                DetailedExerciseDTO.builder()
                        .exerciseId(existingExerciseId)
                        .weight("45KG")
                        .setCount(4)
                        .perSetCount(6)
                        .pace("3012")
                        .time("N/D")
                        .tips("Keep your back straight")
                        .build()
        );

        assertNotNull(detailedExercise);
        assertEquals("45KG", detailedExercise.getWeight());
        assertEquals(Long.valueOf(4), Long.valueOf(detailedExercise.getSetCount()));
        assertEquals(Long.valueOf(6), Long.valueOf(detailedExercise.getPerSetCount()));
        assertEquals("3012", detailedExercise.getPace());
        assertEquals("N/D", detailedExercise.getTime());
        assertEquals("Keep your back straight", detailedExercise.getTips());

        assertEquals(existingExerciseId, detailedExercise.getExercise().getId());
        assertEquals("ExerciseName", detailedExercise.getExercise().getName());
        assertEquals("Section", detailedExercise.getExercise().getSection());
        assertEquals("https://some.url.com/n89w34yt0w93@#$%%VG@#$%2345-2908345wrel", detailedExercise.getExercise().getVideoUrl());
    }

    @Test(expected = ExerciseNotFoundException.class)
    public void test_provideDetailsToExistingExercise_shouldThrowException() throws ExerciseNotFoundException {
        exerciseService.provideDetails(
                DetailedExerciseDTO.builder()
                        .exerciseId(NON_EXISTING_EXERCISE_ID)
                        .weight("45KG")
                        .setCount(4)
                        .perSetCount(6)
                        .pace("3012")
                        .time("N/D")
                        .tips("Keep your back straight")
                        .build()
        );
    }

}
