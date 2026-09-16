package com.example.java_basic.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
    String message() default "{err.auth.username_exists}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}