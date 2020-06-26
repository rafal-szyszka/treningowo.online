package com.prodactivv.app.core.definedmodels.instances;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeInstanceDTO {

    private final Long id;
    private final String type;
    private final List<AttributeInstanceDTO> attributes;

    public static TypeInstanceDTO of(TypeInstance typeInstance) {
        List<AttributeInstanceDTO> attributes = typeInstance.getAttributes()
                .stream()
                .map(AttributeInstanceDTO::of)
                .collect(Collectors.toList());

        return new TypeInstanceDTO(
                typeInstance.getId(),
                typeInstance.getType().getName(),
                attributes
        );
    }
}
