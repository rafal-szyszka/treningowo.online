package com.prodactivv.app.core.definedmodels.logic;

import com.prodactivv.app.core.definedmodels.instances.AttributeInstanceDCO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DataInputInterface {

    private final Long typeId;
    private final List<AttributeInstanceDCO> attributesDCOs;

}
