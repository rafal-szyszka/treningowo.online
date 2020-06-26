package com.prodactivv.app.core.definedmodels.validation;

import com.prodactivv.app.core.definedmodels.definition.Type;
import com.prodactivv.app.core.definedmodels.definition.Attribute;
import com.prodactivv.app.core.definedmodels.definition.Primitive;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TypeAttributeValidator implements ConstraintValidator<TypeAttribute, Attribute> {

    @Override
    public boolean isValid(Attribute attribute, ConstraintValidatorContext constraintValidatorContext) {

        Type referenceType = attribute.getReferenceType();
        Type parentType = attribute.getParentType();
        Primitive primitive = attribute.getPrimitive();

        return
                (referenceType != null && !parentType.getId().equals(referenceType.getId()) && primitive == null)
                ||
                (referenceType == null && primitive != null);
    }
}
