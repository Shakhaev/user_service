package school.faang.user_service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class UserDtoTest {

    @Test
    void validate() {
        UserDto dto = new UserDto(1L, "user", "user@mail.com");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    void validateEmailIsEmpty() {
        UserDto dto = new UserDto(1L, "user", "");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        String expect = "email must not be empty";
        Assertions.assertTrue(violations.stream()
                .anyMatch(constraintViolation ->
                        constraintViolation.getMessage().equals(expect))
        );
    }

    @Test
    void validateEmailIsNotCorrect() {
        UserDto dto = new UserDto(1L, "user", "usermail.com");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        String expect = "must be a well-formed email address";
        Assertions.assertTrue(violations.stream()
                .anyMatch(constraintViolation ->
                        constraintViolation.getMessage().equals(expect))
        );
    }

    @Test
    void validateEmailIsNull() {
        UserDto dto = new UserDto(1L, "user", null);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        String expect = "email must not be null";
        Assertions.assertTrue(violations.stream()
                .anyMatch(constraintViolation ->
                        constraintViolation.getMessage().equals(expect))
        );
    }

    @Test
    void validateUserNameIsEmpty() {
        UserDto dto = new UserDto(1L, "", "user@mail.com");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        String expect = "username must not be empty";
        Assertions.assertTrue(violations.stream()
                .anyMatch(constraintViolation ->
                        constraintViolation.getMessage().equals(expect))
        );
    }

    @Test
    void validateUserNameIsNull() {
        UserDto dto = new UserDto(1L, null, "user@mail.com");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        String expect = "username must not be null";
        Assertions.assertTrue(violations.stream()
                .anyMatch(constraintViolation ->
                        constraintViolation.getMessage().equals(expect))
        );
    }
}