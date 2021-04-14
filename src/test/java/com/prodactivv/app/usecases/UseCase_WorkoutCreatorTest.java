package com.prodactivv.app.usecases;

import com.prodactivv.app.admin.trainer.models.*;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan.UsersWorkoutPlanDTO;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.admin.trainer.workout.ExerciseService;
import com.prodactivv.app.admin.trainer.workout.UsersWorkoutPlanService;
import com.prodactivv.app.admin.trainer.workout.WorkoutPlanService;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.exceptions.MandatoryRegulationsNotAcceptedException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.service.RegistrationService;
import com.prodactivv.app.user.service.UserRegistrationException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@TestPropertySource(
        locations = "classpath:application-tests.properties"
)
public class UseCase_WorkoutCreatorTest {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private UsersWorkoutPlanService usersWorkoutPlanService;
    @Autowired
    private WorkoutPlanService workoutPlanService;
    @Autowired
    private ExerciseService exerciseService;

    private final User.Dto.UserRegistration user = User.Dto.UserRegistration.builder()
            .sex("MALE")
            .email("rsonic94+tests2@mail.com")
            .birthday(LocalDate.parse("1994-03-20"))
            .name("TEST")
            .lastName("TEST")
            .password("test")
            .termsOfUse(true)
            .privacyPolicy(true)
            .build();

    private User.Dto.Simple userDTO;
    private UsersWorkoutPlanDTO usersWorkoutPlan;
    private Exercise exercise;

    @Before
    public void setUp() throws UserRegistrationException, ExerciseNotFoundException, UserNotFoundException, MandatoryRegulationsNotAcceptedException {
        userDTO = registrationService.signUp(user);
        usersWorkoutPlan = usersWorkoutPlanService.createUsersWorkoutPlan(
                userDTO.getId(),
                workoutPlanService.createWorkoutPlan(
                        WorkoutPlan.WorkoutPlanDTO.getEmpty("planName", "weekName", "dayName")
                )
        );

        exercise = exerciseService.createExercise(
                Exercise.builder()
                        .name("ExerciseName")
                        .section("Section")
                        .videoUrl("https://some.url.com/n89w34yt0w93@#$%%VG@#$%2345-2908345wrel")
                        .build()
        );
    }

    @Test
    public void test_createWorkoutPlan() throws ExerciseNotFoundException {
        WorkoutPlan workoutPlan = workoutPlanService.createWorkoutPlan(
                WorkoutPlan.WorkoutPlanDTO.getEmpty("planName", "weekName", "dayName")
        );

        assertNotNull(workoutPlan);

        assertEquals("planName", workoutPlan.getName());

        assertEquals(1L, workoutPlan.getActivityWeeks().size());
        assertEquals("weekName", workoutPlan.getActivityWeeks().iterator().next().getName());
        assertEquals(1L, workoutPlan.getActivityWeeks().iterator().next().getActivityDays().size());

        ActivityDay activityDay = workoutPlan.getActivityWeeks().iterator().next().getActivityDays().iterator().next();
        assertEquals("dayName", activityDay.getName());
        assertEquals("", activityDay.getTips());
        assertNull(activityDay.getSuperExercises());
    }

    @Test
    public void test_createUserWorkoutPlan() throws UserNotFoundException, ExerciseNotFoundException {
        WorkoutPlan workoutPlan = workoutPlanService.createWorkoutPlan(
                WorkoutPlan.WorkoutPlanDTO.getEmpty("planName", "weekName", "dayName")
        );

        UsersWorkoutPlanDTO usersWorkoutPlan = usersWorkoutPlanService.createUsersWorkoutPlan(
                userDTO.getId(),
                workoutPlan
        );

        assertFalse(usersWorkoutPlan.getIsActive());
        assertEquals(userDTO.getId(), usersWorkoutPlan.getUser().getId());
        assertEquals(workoutPlan.getId(), usersWorkoutPlan.getWorkoutPlan().getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void test_createUserWorkoutPlan_shouldThrowException() throws UserNotFoundException {
        usersWorkoutPlanService.createUsersWorkoutPlan(
                -1L,
                WorkoutPlan.builder()
                        .name("WorkoutPlanName")
                        .activityWeeks(new HashSet<>())
                        .build()
        );
    }

    @Test
    public void test_addNewActivityWeek_shouldHaveTwoActivityWeeksAndLastShouldHaveOneEmptyActivityDay() throws NotFoundException, ExerciseNotFoundException {
        ActivityWeek.ActivityWeekManagerDTO activityWeekManagerDTO = workoutPlanService.addEmptyActivityWeekToUserPlan(
                usersWorkoutPlan.getWorkoutPlan().getId(),
                ActivityWeek.ActivityWeekDTO.getEmpty("Week 2", "Day 1")
        );

        assertEquals("Week 2", activityWeekManagerDTO.getName());
        assertTrue(activityWeekManagerDTO.getActivityDays().iterator().hasNext());
        assertEquals("Day 1", activityWeekManagerDTO.getActivityDays().iterator().next().getName());
    }

    @Test
    public void test_addNewActivityDay_shouldHaveToActivityDaysInFirstActivityWeek() throws Exception {
        ActivityWeek.ActivityWeekManagerDTO week = workoutPlanService.addEmptyActivityDayToActivityWeek(
                usersWorkoutPlan.getWorkoutPlan().getActivityWeeks().get(0).getId(),
                ActivityDay.ActivityDayManagerDTO.getEmpty("Day 2")
        );

        assertEquals(2, week.getActivityDays().size());

        week.getActivityDays().forEach(day -> assertEquals(0L, day.getExercises().size()));
    }

    @Test
    @Ignore
    public void test_addNewDetailedExerciseToActivityDay() throws NotFoundException, ExerciseNotFoundException {
        Long dayId = usersWorkoutPlan.getWorkoutPlan().getActivityWeeks().get(0).getActivityDays().get(0).getId();
        ActivityDay.ActivityDayManagerDTO activityDayManagerDTO = workoutPlanService.addExerciseToActivityDay(
                dayId,
                DetailedExercise.DetailedExerciseDTO.builder()
                        .exerciseId(exercise.getId())
                        .pace("3011")
                        .perSetCount("6")
                        .setCount("4")
                        .time("N/D")
                        .tips("N/D")
                        .weight("45KG")
                        .build(),
                -1L
        );

        assertEquals(dayId, activityDayManagerDTO.getId());
        assertEquals(1L, activityDayManagerDTO.getExercises().size());
    }

}
