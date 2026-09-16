package com.example.java_basic.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "{err.auth.email_exists}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}