package school.faang.user_service.utility.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDtoValidator {
    private final Validator validator;

    public boolean validate(UserDto userDto) {
        log.debug("validate userDto: {}", userDto);

        Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(userDto);

        if (constraintViolations.isEmpty()) {
            return true;
        }
        StringBuilder message = new StringBuilder();

        constraintViolations.forEach(constraintViolation ->
                message.append(constraintViolation.getMessage()));

        throw new DataValidationException(message.toString());
    }

    public void validate(List<UserDto> userDtoList) {
        userDtoList.forEach(this::validate);
    }
}
