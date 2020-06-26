package com.prodactivv.app.core.definedmodels.definition;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Type {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "parentType",
            cascade = CascadeType.MERGE,
            orphanRemoval = true)
    private List<Attribute> attributes;

    public void addAttribute(Attribute attribute) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }

        attributes.add(attribute);
    }
}
