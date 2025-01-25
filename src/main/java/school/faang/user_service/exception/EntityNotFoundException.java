package school.faang.user_service.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String error) {
        super(error);
    }
}