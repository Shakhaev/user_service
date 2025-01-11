package school.faang.user_service.exception.global;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ResourceNotFoundException extends ApiException {
    private static final String MESSAGE = "%s %s not found";

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(MESSAGE, NOT_FOUND, resourceName, resourceId);
    }
}
