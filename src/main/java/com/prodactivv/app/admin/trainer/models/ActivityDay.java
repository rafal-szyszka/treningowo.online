package com.prodactivv.app.admin.trainer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodactivv.app.admin.trainer.models.ActivityDaySuperExercise.ActivityDaySuperExerciseManagerDto;
import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.prodactivv.app.admin.trainer.models.Workout.WorkoutDTO;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDay implements Comparable<ActivityDay> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "order_index")
    private Long order;

    private String name;

    @Column(columnDefinition = "TEXT", length = 10000)
    private String tips;

    @ManyToMany
    @JoinTable(
            name = "activity_day_workout",
            joinColumns = @JoinColumn(name = "activity_day_id"),
            inverseJoinColumns = @JoinColumn(name = "workout_id"))
    private Set<Workout> workouts;

    @OneToMany(mappedBy = "activityDay")
    @OrderBy("exerciseOrder ASC")
    private List<ActivityDaySuperExercise> superExercises;

    @JsonIgnore
    @ManyToMany(mappedBy = "activityDays")
    private Set<ActivityWeek> activityWeeks;

    public void addWorkout(Workout workout) {
        if (workouts == null) {
            workouts = new HashSet<>();
        }

        workouts.add(workout);
    }

    public void addDetailedExercise(ActivityDaySuperExercise detailedExercise) {
        if (superExercises == null) {
            superExercises = new ArrayList<>();
        }

        superExercises.add(detailedExercise);
    }

    public void deleteSuperExercise(ActivityDaySuperExercise detailedExercise) {
        if (superExercises != null) {
            superExercises.remove(detailedExercise);
        }
    }

    public void delete() {
        if (workouts != null) {
            workouts.clear();
        }

        if (superExercises != null) {
            superExercises.clear();
        }
    }

    @Override
    public int compareTo(ActivityDay o) {
        return order.compareTo(o.order);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivityDayDTO {

        private String name;
        private String tips;
        private Long order;
        private List<WorkoutDTO> workouts;

        public static ActivityDayDTO getEmpty(String dayName) {
            return new ActivityDayDTO(
                    dayName,
                    "",
                    1L,
                    Collections.emptyList()
            );
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDayManagerDTO {

        private Long id;
        private String name;
        private String tips;
        private Long order;
        private List<ActivityDaySuperExerciseManagerDto> exercises;

        public static ActivityDayManagerDTO getEmpty(String name) {
            return new ActivityDayManagerDTO(
                    null,
                    name,
                    "",
                    1L,
                    Collections.emptyList()
            );
        }

        public static ActivityDayManagerDTO of(ActivityDay activityDay) {
            return new ActivityDayManagerDTO(
                    activityDay.id,
                    activityDay.name,
                    activityDay.tips,
                    activityDay.order,
                    getExercises(activityDay)
            );
        }

        private static List<ActivityDaySuperExerciseManagerDto> getExercises(ActivityDay activityDay) {
            if (activityDay.superExercises != null) {
                return activityDay.superExercises.stream()
                        .map(ActivityDaySuperExerciseManagerDto::of)
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        }
    }
}
