package com.prodactivv.app.core.definedmodels.instances;

import com.prodactivv.app.core.definedmodels.logic.DataInputInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttributeInstanceDCO {

    private final Long attributeId;
    private final DataInputInterface referenceType;
    private final PrimitiveInstanceDCO primitiveInstanceDCO;

}
