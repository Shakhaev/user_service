package school.faang.user_service.dto.event.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Documented
@Constraint(validatedBy = EnumValidator.EnumValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClass();
    String message() default "Value is not valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {
        private Set<String> allowedValues;

        @Override
        public void initialize(EnumValidator constraintAnnotation) {
            allowedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.toSet());
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value == null || allowedValues.contains(value);
        }
    }
}
