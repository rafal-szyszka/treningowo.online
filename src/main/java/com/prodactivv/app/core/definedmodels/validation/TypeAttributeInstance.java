package com.prodactivv.app.core.definedmodels.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { AttributeInstanceValidator.class })
public @interface TypeAttributeInstance {

    String message() default "com.prodactivv.app.core.definedmodels.TypeAttributeInstance";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
