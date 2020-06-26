package com.prodactivv.app.core.definedmodels.logic;

import com.prodactivv.app.core.definedmodels.definition.AttributeRepository;
import com.prodactivv.app.core.definedmodels.instances.AttributeInstance;
import com.prodactivv.app.core.definedmodels.instances.AttributeInstanceDCO;
import com.prodactivv.app.core.definedmodels.instances.AttributeInstanceRepository;
import com.prodactivv.app.core.definedmodels.instances.TypeInstance;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AttributeInstanceService {

    private final AttributeRepository attributeRepository;
    private final AttributeInstanceRepository attributeInstanceRepository;

    private final PrimitiveInstanceService primitiveInstanceService;
    private final TypeInstanceService typeInstanceService;


    public AttributeInstanceService(AttributeRepository attributeRepository, AttributeInstanceRepository attributeInstanceRepository, PrimitiveInstanceService primitiveInstanceService, @Lazy TypeInstanceService typeInstanceService) {
        this.attributeRepository = attributeRepository;
        this.attributeInstanceRepository = attributeInstanceRepository;
        this.primitiveInstanceService = primitiveInstanceService;
        this.typeInstanceService = typeInstanceService;
    }

    public AttributeInstance create(AttributeInstanceDCO attributeDCO, TypeInstance parentInstance) throws NotFoundException {
        AttributeInstance instance = new AttributeInstance();
        instance.setAttribute(attributeRepository.findById(attributeDCO.getAttributeId()).orElseThrow(NotFoundException::new));

        instance.setParentTypeInstance(parentInstance);
        if (attributeDCO.getPrimitiveInstanceDCO() != null) {
            instance.setPrimitiveInstance(primitiveInstanceService.create(attributeDCO.getPrimitiveInstanceDCO()));
            return attributeInstanceRepository.save(instance);
        }

        if (attributeDCO.getReferenceType() != null) {
            System.out.println("XD: " + attributeDCO.getReferenceType().getTypeId());
            TypeInstance referenceTypeInstance = typeInstanceService.create(attributeDCO.getReferenceType());
            System.out.println("XD2: " + referenceTypeInstance.getId());
            instance.setReferenceTypeInstance(referenceTypeInstance);
            return attributeInstanceRepository.save(instance);
        }

        throw new IllegalStateException("Attribute must have primitive or reference type value!");
    }
}
