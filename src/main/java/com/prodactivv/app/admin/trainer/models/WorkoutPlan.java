package com.prodactivv.app.admin.trainer.models;

import com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekDTO;
import com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekManagerDTO;
import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "workout_plan_workout_weeks",
            joinColumns = @JoinColumn(name = "workout_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_week_id"))
    private Set<ActivityWeek> activityWeeks;

    public void addWorkoutWeek(ActivityWeek activityWeek) {
        if (activityWeeks == null) {
            activityWeeks = new HashSet<>();
        }

        activityWeeks.add(activityWeek);
    }

    public boolean removeActivityWeek(ActivityWeek activityWeek) {
        if (activityWeeks != null) {
            return activityWeeks.remove(activityWeek);
        }
        return false;
    }

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutPlanDTO {

        private String name;
        private List<ActivityWeekDTO> activityWeeks;

        public boolean isNotEmpty() {
            return name != null || activityWeeks != null;
        }

        public static WorkoutPlanDTO getEmpty(String planName, String weekName, String dayName) {
            return new WorkoutPlanDTO(
                    planName,
                    Collections.singletonList(ActivityWeekDTO.getEmpty(
                            weekName, dayName
                    ))
            );
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutPlanManagerDTO {

        private Long id;
        private String name;
        private List<ActivityWeekManagerDTO> activityWeeks;

        public static WorkoutPlanManagerDTO of(WorkoutPlan workoutPlan) {
            return new WorkoutPlanManagerDTO(
                    workoutPlan.id,
                    workoutPlan.name,
                    workoutPlan.activityWeeks.stream().sorted()
                            .map(ActivityWeekManagerDTO::of)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList())
            );
        }
    }
}
