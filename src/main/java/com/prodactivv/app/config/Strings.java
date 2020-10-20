package com.prodactivv.app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource(value = "classpath:strings.properties")
public class Strings {

    @Value("${string.workout.plan.default.name}")
    private String workoutPlanDefaultName;

    @Value("${string.workout.plan.week.default.name}")
    private String workoutPlanWeekDefaultName;

    @Value("${string.workout.plan.week.day.default.name}")
    private String workoutPlanWeekDayDefaultName;

    @Value("${string.workout.plan.week.day.workout.default.name}")
    private String workoutPlanWeekDayWorkoutDefaultName;


}
