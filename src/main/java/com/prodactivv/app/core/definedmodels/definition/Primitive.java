package com.prodactivv.app.core.definedmodels.definition;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Primitive {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

}


