package school.faang.user_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import school.faang.user_service.validation.event.EventValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {EventValidator.class})
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValidation {
    String message() default "Invalid data!";
    Class<?>[] groups() default  {};
    Class<? extends Payload>[] payload() default {};
}
