package com.prodactivv.app.admin.trainer.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.prodactivv.app.admin.trainer.models.ActivityDay.ActivityDayManagerDTO;
import com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseManagerDTO;
import lombok.*;

import javax.persistence.*;

@Entity(name = "activity_day_super_exercises")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDaySuperExercise {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_day_id")
    private ActivityDay activityDay;

    @ManyToOne
    @JoinColumn(name = "detailed_exercise_id")
    private DetailedExercise detailedExercise;

    private Long exerciseOrder;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityDatSuperExerciseManagerDto {

        private Long id;
        private Long order;
        private Long detailedExerciseId;
        private Integer setCount;
        private Integer perSetCount;
        private String weight;
        private String pace;
        private String time;
        private String tips;
        private Long exerciseId;
        private String name;
        private String videoUrl;
        private String description;

        @JsonIgnore
        private DetailedExerciseManagerDTO detailedExerciseManagerDTO;

        public static ActivityDatSuperExerciseManagerDto of(ActivityDaySuperExercise superExercise) {
            DetailedExerciseManagerDTO detailedExerciseManagerDTO = DetailedExerciseManagerDTO.of(superExercise.detailedExercise);
            return new ActivityDatSuperExerciseManagerDto(
                    superExercise.id,
                    superExercise.exerciseOrder,
                    detailedExerciseManagerDTO.id,
                    detailedExerciseManagerDTO.setCount,
                    detailedExerciseManagerDTO.perSetCount,
                    detailedExerciseManagerDTO.weight,
                    detailedExerciseManagerDTO.pace,
                    detailedExerciseManagerDTO.time,
                    detailedExerciseManagerDTO.tips,
                    detailedExerciseManagerDTO.exerciseId,
                    detailedExerciseManagerDTO.name,
                    detailedExerciseManagerDTO.videoUrl,
                    detailedExerciseManagerDTO.description,
                    detailedExerciseManagerDTO
            );
        }
    }
}
