package school.faang.user_service.exception.skill;

import school.faang.user_service.exception.ValidateException;

public class SkillDtoNullObjectValidationException extends ValidateException {
    public SkillDtoNullObjectValidationException(String message) {
        super(message);
    }
}
