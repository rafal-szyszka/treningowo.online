package com.prodactivv.app.admin.trainer.models;

import lombok.*;

import javax.persistence.*;

import static com.prodactivv.app.admin.trainer.models.ActivityDay.*;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "monday_activity_day_id", referencedColumnName = "id")
    private ActivityDay monday;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tuesday_activity_day_id", referencedColumnName = "id")
    private ActivityDay tuesday;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wednesday_activity_day_id", referencedColumnName = "id")
    private ActivityDay wednesday;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "thursday_activity_day_id", referencedColumnName = "id")
    private ActivityDay thursday;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "friday_activity_day_id", referencedColumnName = "id")
    private ActivityDay friday;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "saturday_activity_day_id", referencedColumnName = "id")
    private ActivityDay saturday;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sunday_activity_day_id", referencedColumnName = "id")
    private ActivityDay sunday;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutPlanDTO {

        private String name;
        private ActivityDayDTO monday;
        private ActivityDayDTO tuesday;
        private ActivityDayDTO wednesday;
        private ActivityDayDTO thursday;
        private ActivityDayDTO friday;
        private ActivityDayDTO saturday;
        private ActivityDayDTO sunday;

    }
}
