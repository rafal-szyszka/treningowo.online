package com.prodactivv.app.core.definedmodels.logic;

import com.prodactivv.app.core.definedmodels.definition.PrimitiveRepository;
import com.prodactivv.app.core.definedmodels.instances.PrimitiveInstance;
import com.prodactivv.app.core.definedmodels.instances.PrimitiveInstanceDCO;
import com.prodactivv.app.core.definedmodels.instances.PrimitiveInstanceRepository;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrimitiveInstanceService {

    private final PrimitiveRepository primitiveRepository;
    private final PrimitiveInstanceRepository primitiveInstanceRepository;


    public PrimitiveInstanceService(PrimitiveRepository primitiveRepository, PrimitiveInstanceRepository primitiveInstanceRepository) {
        this.primitiveRepository = primitiveRepository;
        this.primitiveInstanceRepository = primitiveInstanceRepository;
    }


    public PrimitiveInstance create(PrimitiveInstanceDCO primitiveInstanceDCO) throws NotFoundException {
        PrimitiveInstance instance = new PrimitiveInstance();
        instance.setPrimitive(primitiveRepository.findById(primitiveInstanceDCO.getPrimitiveId()).orElseThrow(NotFoundException::new));
        instance.setValue(primitiveInstanceDCO.getValue());
        return primitiveInstanceRepository.save(instance);
    }
}
