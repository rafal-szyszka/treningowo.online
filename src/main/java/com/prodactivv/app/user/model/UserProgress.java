package com.prodactivv.app.user.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 10, precision = 2)
    private Double weight;

    private Integer arms;

    private Integer biceps;

    private Integer chest;

    private Integer waist;

    private Integer hips;

    private Integer thigh;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class CreateProgress {
            private Double weight;
            private Integer arms;
            private Integer biceps;
            private Integer chest;
            private Integer waist;
            private Integer hips;
            private Integer thigh;
            private LocalDate date;
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ShowProgress {
            private Long id;
            private Double weight;
            private Integer arms;
            private Integer biceps;
            private Integer chest;
            private Integer waist;
            private Integer hips;
            private Integer thigh;
            private LocalDate date;

            public static ShowProgress fromUserProgress(UserProgress userProgress) {
                return builder()
                        .id(userProgress.id)
                        .weight(userProgress.weight)
                        .arms(userProgress.arms)
                        .biceps(userProgress.biceps)
                        .chest(userProgress.chest)
                        .waist(userProgress.waist)
                        .hips(userProgress.hips)
                        .thigh(userProgress.thigh)
                        .date(userProgress.date)
                        .build();
            }
        }

    }
}
