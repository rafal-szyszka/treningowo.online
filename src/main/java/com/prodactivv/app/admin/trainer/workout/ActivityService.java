package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.ActivityDay;
import com.prodactivv.app.admin.trainer.models.ActivityDaySuperExercise;
import com.prodactivv.app.admin.trainer.models.ActivityDaySuperExercise.ActivityDaySuperExerciseManagerDto;
import com.prodactivv.app.admin.trainer.models.ActivityWeek;
import com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekDTO;
import com.prodactivv.app.admin.trainer.models.DetailedExercise;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.admin.trainer.models.repositories.ActivityDayRepository;
import com.prodactivv.app.admin.trainer.models.repositories.ActivityDaySuperExerciseRepository;
import com.prodactivv.app.admin.trainer.models.repositories.ActivityWeekRepository;
import com.prodactivv.app.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.prodactivv.app.admin.trainer.models.ActivityDay.ActivityDayDTO;
import static com.prodactivv.app.admin.trainer.models.ActivityDay.ActivityDayManagerDTO;
import static com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekManagerDTO;
import static com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseDTO;
import static com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseManagerDTO;
import static com.prodactivv.app.admin.trainer.models.Workout.WorkoutDTO;

@Service
@RequiredArgsConstructor
public class ActivityService {

    public static final String ACTIVITY_WEEK_NOT_FOUND_MSG = "Activity week %s not found";
    public static final String ACTIVITY_DAY_NOT_FOUND_MSG = "Activity day %s not found";

    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;

    private final ActivityDayRepository repository;
    private final ActivityWeekRepository activityWeekRepository;
    private final ActivityDaySuperExerciseRepository superExerciseRepository;

    public Optional<ActivityDay> createActivityDay(ActivityDayDTO activityDayDTO) throws ExerciseNotFoundException {
        if (activityDayDTO != null) {
            ActivityDay activityDay = new ActivityDay();

            activityDay.setName(activityDayDTO.getName());
            activityDay.setTips(activityDayDTO.getTips());

            if (activityDayDTO.getWorkouts() != null) {
                for (WorkoutDTO workoutDTO : activityDayDTO.getWorkouts()) {
                    activityDay.addWorkout(workoutService.createWorkout(workoutDTO));
                }
            }

            return Optional.of(repository.save(activityDay));
        }
        return Optional.empty();
    }

    public Optional<ActivityDay> createActivityDay(ActivityDayManagerDTO activityDayDTO) throws ExerciseNotFoundException {
        if (activityDayDTO != null) {
            ActivityDay activityDay = new ActivityDay();

            activityDay.setName(activityDayDTO.getName());
            activityDay.setTips(activityDayDTO.getTips());

            activityDay = repository.save(activityDay);

            if (activityDayDTO.getExercises() != null) {
                for (ActivityDaySuperExerciseManagerDto superExerciseDto : activityDayDTO.getExercises()) {
                    DetailedExercise detailedExercise = exerciseService.provideDetails((DetailedExerciseDTO) superExerciseDto.getDetailedExerciseManagerDTO());
                    ActivityDaySuperExercise superExercise = ActivityDaySuperExercise.builder()
                            .detailedExercise(detailedExercise)
                            .activityDay(activityDay)
                            .build();
                    superExerciseRepository.save(superExercise);
                    activityDay.addDetailedExercise(superExercise);
                }
            }

            return Optional.of(activityDay);
        }
        return Optional.empty();
    }

    public ActivityWeek createActivityWeek(ActivityWeekDTO activityWeekDTO) throws ExerciseNotFoundException {
        ActivityWeek activityWeek = new ActivityWeek();
        activityWeek.setName(activityWeekDTO.getName());

        for (ActivityDayDTO activityDayDTO : activityWeekDTO.getActivityDays()) {
            Optional<ActivityDay> activityDay = createActivityDay(activityDayDTO);
            activityDay.ifPresent(activityWeek::addDay);
        }

        return activityWeekRepository.save(activityWeek);
    }

    public ActivityWeek getActivityWeekById(Long id) throws NotFoundException {
        return activityWeekRepository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_WEEK_NOT_FOUND_MSG, id)));
    }

    public ActivityWeek addActivityDayToActivityWeek(Long id, ActivityDayDTO activityDayDTO) throws NotFoundException, ExerciseNotFoundException {
        ActivityWeek activityWeek = getActivityWeekById(id);

        createActivityDay(activityDayDTO).ifPresent(activityWeek::addDay);

        return activityWeekRepository.save(activityWeek);
    }

    public ActivityWeek addActivityDayToActivityWeek(Long id, ActivityDayManagerDTO activityDayDTO) throws NotFoundException, ExerciseNotFoundException {
        ActivityWeek activityWeek = getActivityWeekById(id);

        createActivityDay(activityDayDTO).ifPresent(activityWeek::addDay);

        return activityWeekRepository.save(activityWeek);
    }

    public ActivityDay addExerciseToActivityDay(Long id, DetailedExerciseDTO exerciseDTO, Long order) throws NotFoundException, ExerciseNotFoundException {
        ActivityDay activityDay = repository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_DAY_NOT_FOUND_MSG, id)));

        DetailedExercise detailedExercise = exerciseService.provideDetails(exerciseDTO);
        addExercise(activityDay, detailedExercise, order);
        return activityDay;
    }

    public ActivityDay addExerciseToActivityDay(Long id, Long detailedExerciseId, Long order) throws NotFoundException, ExerciseNotFoundException {
        ActivityDay activityDay = repository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_DAY_NOT_FOUND_MSG, id)));

        DetailedExercise detailedExercise = exerciseService.getDetailedExercise(detailedExerciseId);
        addExercise(activityDay, detailedExercise, order);
        return activityDay;
    }

    private void addExercise(ActivityDay activityDay, DetailedExercise detailedExercise, Long order) {
        order = shiftExerciseOrder(activityDay, order);
        ActivityDaySuperExercise superExercise = ActivityDaySuperExercise.builder()
                .detailedExercise(detailedExercise)
                .activityDay(activityDay)
                .exerciseOrder(order)
                .build();
        superExerciseRepository.save(superExercise);
        activityDay.addDetailedExercise(superExercise);
        activityDay.getSuperExercises().sort(Comparator.comparing(ActivityDaySuperExercise::getExerciseOrder));
    }

    public ActivityWeekManagerDTO removeActivityWeekFromUserPlan(Long id) throws NotFoundException {
        ActivityWeek activityWeek = activityWeekRepository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_WEEK_NOT_FOUND_MSG, id)));

        activityWeek.getPlans().forEach(workoutPlan -> workoutPlan.removeActivityWeek(activityWeek));
        activityWeek.delete();
        activityWeekRepository.delete(activityWeek);

        return ActivityWeekManagerDTO.of(activityWeek).orElseThrow(new NotFoundException(String.format(ACTIVITY_WEEK_NOT_FOUND_MSG, id)));
    }

    public ActivityDayManagerDTO removeActivityDayFromActivityWeek(Long id) throws NotFoundException {
        ActivityDay activityDay = repository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_DAY_NOT_FOUND_MSG, id)));

        activityDay.getActivityWeeks().forEach(activityWeek -> activityWeek.removeDay(activityDay));
        activityDay.delete();
        repository.delete(activityDay);

        return ActivityDayManagerDTO.of(activityDay);
    }

    public DetailedExerciseManagerDTO removeExerciseFromActivityDay(Long id) throws ExerciseNotFoundException {
        return DetailedExerciseManagerDTO.of(exerciseService.deleteDetailedExercise(id));
    }

    public Long shiftExerciseOrder(ActivityDay activityDay, Long order) {
        if (order == -1L) {
            Optional<ActivityDaySuperExercise> max = activityDay.getSuperExercises().stream()
                    .max(Comparator.comparing(ActivityDaySuperExercise::getExerciseOrder));
            return max.map(activityDaySuperExercise -> activityDaySuperExercise.getExerciseOrder() + 1).orElse(1L);
        }
        activityDay.getSuperExercises().stream()
                .filter(exercise -> exercise.getExerciseOrder() >= order)
                .forEach(exercise -> {
                    exercise.setExerciseOrder(exercise.getExerciseOrder() + 1);
                    superExerciseRepository.save(exercise);
                });

        return order;
    }

    public ActivityDayManagerDTO setActivityDayTips(Long id, String tips) throws NotFoundException {
        ActivityDay activityDay = repository.findById(id).orElseThrow(new NotFoundException(String.format("Activity day %s not found", id)));
        activityDay.setTips(tips);
        return ActivityDayManagerDTO.of(repository.save(activityDay));
    }

    public ActivityDaySuperExerciseManagerDto moveExerciseByStep(Long id, Long step) throws NotFoundException {
        ActivityDaySuperExercise superExercise = superExerciseRepository.findById(id).orElseThrow(new NotFoundException(String.format("Super exercise %s not found", id)));
        List<ActivityDaySuperExercise> collect = superExercise.getActivityDay().getSuperExercises().stream()
                .filter(s -> s.getExerciseOrder().equals(superExercise.getExerciseOrder() + step))
                .collect(Collectors.toList());

        if (collect.size() > 1) {
            throw new IllegalStateException(String.format("Found %s exercises with order %s", collect.size(), superExercise.getExerciseOrder() + step));
        }

        if (collect.size() == 1) {
            ActivityDaySuperExercise exerciseToSwitch = collect.get(0);
            Long tmpOrder = exerciseToSwitch.getExerciseOrder();
            exerciseToSwitch.setExerciseOrder(superExercise.getExerciseOrder());
            superExercise.setExerciseOrder(tmpOrder);
            superExerciseRepository.save(exerciseToSwitch);
            return ActivityDaySuperExerciseManagerDto.of(superExerciseRepository.save(superExercise));
        }

        return ActivityDaySuperExerciseManagerDto.of(superExercise);
    }
}
