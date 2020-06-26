package com.prodactivv.app.core.definedmodels.instances;

import com.prodactivv.app.core.definedmodels.definition.Attribute;
import com.prodactivv.app.core.definedmodels.validation.TypeAttributeInstance;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
//@TypeAttributeInstance
public class AttributeInstance {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "attribute_id", referencedColumnName = "id")
    private Attribute attribute;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "primitive_instance_id", referencedColumnName = "id")
    private PrimitiveInstance primitiveInstance;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "refrence_instance_id", referencedColumnName = "id")
    private TypeInstance referenceTypeInstance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_instance_id")
    private TypeInstance parentTypeInstance;

}
