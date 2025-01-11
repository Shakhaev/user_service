package school.faang.user_service.exception.event.exceptions;


import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class InsufficientSkillsException extends ApiException {
    private static final String MESSAGE = "User with id: %s doesn't have the necessary skills to run this event";

    public InsufficientSkillsException(Long userId) {
        super(MESSAGE, FORBIDDEN, userId);
    }
}
