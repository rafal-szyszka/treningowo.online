package com.prodactivv.app.core.definedmodels.logic;

import com.prodactivv.app.core.definedmodels.definition.*;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TypeService {

    private final TypeRepository typeRepository;
    private final AttributeRepository attributeRepository;
    private final PrimitiveRepository primitiveRepository;

    public TypeService(TypeRepository typeRepository, AttributeRepository attributeRepository, PrimitiveRepository primitiveRepository) {
        this.typeRepository = typeRepository;
        this.attributeRepository = attributeRepository;
        this.primitiveRepository = primitiveRepository;
    }

    public Type defineNew(TypeDCO typeDCO) throws NotFoundException {
        Type newType = new Type(typeDCO.getName());
        newType = typeRepository.save(newType);

        for (AttributeDCO attributeDCO : typeDCO.getAttributes()){
            Attribute attribute = new Attribute(attributeDCO.getName());
            attribute.setParentType(newType);
            if (attributeDCO.getTypeId() != null) {
                attribute.setPrimitive(primitiveRepository.findById(attributeDCO.getTypeId()).orElseThrow(NotFoundException::new));
            }
            if (attributeDCO.getReferenceTypeId() != null) {
                attribute.setReferenceType(typeRepository.findById(attributeDCO.getReferenceTypeId()).orElseThrow(NotFoundException::new));
            }
            newType.addAttribute(attribute);
            attributeRepository.save(attribute);
        }

        return newType;
    }

    public List<Type> getAllDefinedTypes() {
        return typeRepository.findAll();
    }
}
