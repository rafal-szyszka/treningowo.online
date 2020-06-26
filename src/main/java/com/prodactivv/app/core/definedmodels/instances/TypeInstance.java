package com.prodactivv.app.core.definedmodels.instances;

import com.prodactivv.app.core.definedmodels.definition.Type;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class TypeInstance {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private Type type;
    
    @OneToMany(
            mappedBy = "parentTypeInstance",
            cascade = CascadeType.MERGE,
            orphanRemoval = true)
    private List<AttributeInstance> attributes;

    public void addAttribute(AttributeInstance attribute) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }

        attributes.add(attribute);
    }
}
