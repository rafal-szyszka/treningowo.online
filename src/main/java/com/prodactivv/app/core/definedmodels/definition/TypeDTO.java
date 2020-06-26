package com.prodactivv.app.core.definedmodels.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeDTO {

    private final Long id;
    private final String name;
    private final List<AttributeDTO> attributes;

    public static TypeDTO headerOf(Type type) {
        return new TypeDTO(
                type.getId(),
                type.getName(),
                null
        );
    }

    public static TypeDTO completeOf(Type type) {
        List<AttributeDTO> attributes = type.getAttributes()
                .stream()
                .map(AttributeDTO::of)
                .collect(Collectors.toList());

        return new TypeDTO(
                type.getId(),
                type.getName(),
                attributes
        );
    }
}
