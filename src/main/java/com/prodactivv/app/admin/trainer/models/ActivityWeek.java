package com.prodactivv.app.admin.trainer.models;

import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.prodactivv.app.admin.trainer.models.ActivityDay.ActivityDayDTO;
import static com.prodactivv.app.admin.trainer.models.ActivityDay.ActivityDayManagerDTO;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ActivityWeek {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "activity_week_activity_days",
            joinColumns = @JoinColumn(name = "activity_week_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_day_id"))
    private Set<ActivityDay> activityDays;

    @ManyToMany(mappedBy = "activityWeeks")
    private Set<WorkoutPlan> plans;

    public void addDay(ActivityDay activityDay) {
        if (activityDays == null) {
            activityDays = new HashSet<>();
        }

        activityDays.add(activityDay);
    }

    public void removeDay(ActivityDay activityDay) {
        if (activityDays != null) {
            activityDays.remove(activityDay);
        }
    }

    public void delete() {
        if (plans != null) {
            plans.clear();
        }

        if (activityDays != null) {
            activityDays.clear();
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityWeekDTO {

        private String name;
        private List<ActivityDayDTO> activityDays;

        public static ActivityWeekDTO getEmpty(String weekName, String dayName) {
            return new ActivityWeekDTO(
                    weekName,
                    Collections.singletonList(ActivityDayDTO.getEmpty(dayName))
            );
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityWeekManagerDTO {

        private Long id;
        private String name;
        private List<ActivityDayManagerDTO> activityDays;

        public static Optional<ActivityWeekManagerDTO> of(ActivityWeek activityWeek) {
            if (activityWeek != null) {
                return Optional.of(new ActivityWeekManagerDTO(
                        activityWeek.id,
                        activityWeek.name,
                        getActivityDays(activityWeek)
                ));
            }
            return Optional.empty();
        }

        private static List<ActivityDayManagerDTO> getActivityDays(ActivityWeek activityWeek) {
            if (activityWeek.activityDays != null) {
                return activityWeek.activityDays.stream()
                        .map(ActivityDayManagerDTO::of)
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        }
    }
}
