package com.template.user.model;

import com.template.user.entities.Authority;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class RoleMatchValidator implements ConstraintValidator<RoleMatch, Object> {

    private String role;
    private String message;

    @Override
    public void initialize(RoleMatch constraintAnnotation) {
        this.role = constraintAnnotation.role();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        Object roleObj = wrapper.getPropertyValue(role);

        boolean valid = Arrays.stream(Authority.values()).anyMatch(a -> a.name().equals(roleObj));

        if(!valid) {
            context
                    .buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(role)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }
        return valid;
    }
}
