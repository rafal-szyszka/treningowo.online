package com.prodactivv.app.core.definedmodels.definition;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TypeDCO {

    private final String name;
    private final List<AttributeDCO> attributes;

}
