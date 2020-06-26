package com.prodactivv.app.core.definedmodels.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeDTO {

    private final Long id;
    private final String name;
    private final Primitive primitive;
    private final TypeDTO referenceType;
    private final TypeDTO parentType;

    public static AttributeDTO of(Attribute attribute) {
        return new AttributeDTO(
                attribute.getId(),
                attribute.getName(),
                attribute.getPrimitive(),
                attribute.getReferenceType() != null ? TypeDTO.completeOf(attribute.getReferenceType()) : null,
                attribute.getParentType() != null ? TypeDTO.headerOf(attribute.getParentType()) : null
        );
    }
}
