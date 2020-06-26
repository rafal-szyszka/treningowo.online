package com.prodactivv.app.core.definedmodels.validation;

import com.prodactivv.app.core.definedmodels.instances.AttributeInstance;
import com.prodactivv.app.core.definedmodels.instances.TypeInstance;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AttributeInstanceValidator implements ConstraintValidator<TypeAttributeInstance, AttributeInstance> {

    @Override
    public boolean isValid(AttributeInstance attributeInstance, ConstraintValidatorContext constraintValidatorContext) {
        TypeInstance parentTypeInstance = attributeInstance.getParentTypeInstance();
        TypeInstance referenceTypeInstance = attributeInstance.getReferenceTypeInstance();

        System.out.println("0: " + attributeInstance.getAttribute().getName());
        System.out.println("1: " + (parentTypeInstance != null && referenceTypeInstance == null));
        System.out.println("2: " + (parentTypeInstance == null && referenceTypeInstance != null));

        return (parentTypeInstance != null && referenceTypeInstance == null) || (parentTypeInstance == null && referenceTypeInstance != null);
    }
}
