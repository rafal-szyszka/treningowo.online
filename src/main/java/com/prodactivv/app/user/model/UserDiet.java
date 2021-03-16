package com.prodactivv.app.user.model;

import com.prodactivv.app.core.files.DatabaseFile;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserDiet {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "diet_file_id")
    private DatabaseFile dietFile;

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Diet {
            private Long id;
            private DatabaseFile.Dto.Viewable diet;

            public static Diet fromUserDiet(UserDiet userDiet) {
                return builder()
                        .id(userDiet.id)
                        .diet(DatabaseFile.Dto.Viewable.fromDatabaseFile(userDiet.getDietFile()))
                        .build();
            }

        }

    }

}
