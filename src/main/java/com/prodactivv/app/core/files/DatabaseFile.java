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
}
