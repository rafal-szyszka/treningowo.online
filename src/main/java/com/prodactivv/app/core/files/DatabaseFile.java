package com.prodactivv.app.core.files;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseFile {

    @Id
    @GeneratedValue
    private Long id;

    private String fileName;

    private String fileLocationType;

    private String fileLocation;

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Downloadable {
            private Long id;
            private String fileName;

            public static Downloadable fromDatabaseFile(DatabaseFile file) {
                return builder()
                        .id(file.id)
                        .fileName(file.fileName)
                        .build();
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Viewable {
            private Long id;
            private String fileName;
            private String fileLocation;

            public static Viewable fromDatabaseFile(DatabaseFile file) {
                return builder()
                        .id(file.id)
                        .fileName(file.fileName)
                        .fileLocation(file.fileLocation)
                        .build();
            }
        }
    }
}
