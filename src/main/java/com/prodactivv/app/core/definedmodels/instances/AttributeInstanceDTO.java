package com.prodactivv.app.core.definedmodels.instances;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeInstanceDTO {

    private final String attributeName;
    private final String primitiveValue;
    private final TypeInstanceDTO referenceValue;


    public static AttributeInstanceDTO of(AttributeInstance attributeInstance) {
        System.out.println(attributeInstance.getId());
        return new AttributeInstanceDTO(
                attributeInstance.getAttribute().getName(),
                attributeInstance.getPrimitiveInstance() != null ? attributeInstance.getPrimitiveInstance().getValue() : null,
                attributeInstance.getReferenceTypeInstance() != null ? TypeInstanceDTO.of(attributeInstance.getReferenceTypeInstance()) : null
        );
    }

}
