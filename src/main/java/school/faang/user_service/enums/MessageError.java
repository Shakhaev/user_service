package school.faang.user_service.enums;

public enum MessageError {

    USER_NOT_FOUND_EXCEPTION("User by ID is not found");

    private final String message;

    MessageError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
