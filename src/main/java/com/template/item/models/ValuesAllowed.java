package com.template.item.models;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValuesAllowedValidator.class})
public @interface ValuesAllowed {

    String message() default "must be from list of ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String propName();
    String[] values();
}