package com.prodactivv.app.core.definedmodels.logic;

import com.prodactivv.app.core.definedmodels.definition.TypeRepository;
import com.prodactivv.app.core.definedmodels.instances.AttributeInstanceDCO;
import com.prodactivv.app.core.definedmodels.instances.TypeInstance;
import com.prodactivv.app.core.definedmodels.instances.TypeInstanceRepository;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TypeInstanceService {

    private final TypeRepository typeRepository;
    private final TypeInstanceRepository typeInstanceRepository;

    private final AttributeInstanceService attributeInstanceService;

    public TypeInstanceService(TypeRepository typeRepository, TypeInstanceRepository typeInstanceRepository, AttributeInstanceService attributeInstanceService) {
        this.typeRepository = typeRepository;
        this.typeInstanceRepository = typeInstanceRepository;
        this.attributeInstanceService = attributeInstanceService;
    }

    public TypeInstance create(DataInputInterface dataInputInterface) throws NotFoundException {
        TypeInstance instance = new TypeInstance();
        instance.setType(typeRepository.findById(dataInputInterface.getTypeId()).orElseThrow(NotFoundException::new));
        instance = typeInstanceRepository.save(instance);

        for (AttributeInstanceDCO attributeDCO : dataInputInterface.getAttributesDCOs()) {
            instance.addAttribute(attributeInstanceService.create(attributeDCO, instance));
        }

        return instance;
    }
}
