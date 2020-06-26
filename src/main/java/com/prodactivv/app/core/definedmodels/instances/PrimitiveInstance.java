package com.prodactivv.app.core.definedmodels.instances;

import com.prodactivv.app.core.definedmodels.definition.Primitive;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PrimitiveInstance {

    @Id
    @GeneratedValue
    private Long id;

    private String value;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "primitive_id", referencedColumnName = "id")
    private Primitive primitive;

}
