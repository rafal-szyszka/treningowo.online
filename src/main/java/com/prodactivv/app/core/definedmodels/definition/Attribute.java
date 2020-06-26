package com.prodactivv.app.core.definedmodels.definition;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.prodactivv.app.core.definedmodels.validation.TypeAttribute;
import lombok.*;

import javax.persistence.*;

@Entity
@TypeAttribute
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Attribute {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "primitive_id", referencedColumnName = "id")
    private Primitive primitive;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "refrence_type_id", referencedColumnName = "id")
    private Type referenceType;

    //REMEMBER TO GENERATE EQUALS() AND HASHCODE() METHODS!
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_type_id")
    private Type parentType;

}
